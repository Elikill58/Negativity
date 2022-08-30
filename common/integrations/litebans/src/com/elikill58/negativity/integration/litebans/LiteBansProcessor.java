package com.elikill58.negativity.integration.litebans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;
import com.elikill58.negativity.universal.utils.ChatUtils;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;
import com.elikill58.negativity.universal.warn.processor.WarnProcessorProvider;

import litebans.api.Database;

public class LiteBansProcessor implements BanProcessor, WarnProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		try {
			String timeValue = (ban.isDefinitive() ? "" : ChatUtils.getTimeFromLong(ban.getExpirationTime() - ban.getExecutionTime()) + " ");
			String sender;
			switch (ban.getBanType()) {
			case MOD:
				UUID modUUID = ban.getBannedByUUID();
				if(modUUID != null) { // get UUID to support when MOD change her name
					sender = "--sender-uuid=" + modUUID.toString();
					break;
				}
			case CONSOLE:
			case PLUGIN:
			case UNKNOW:
			default: // no UUID, so just see name
				sender = "--sender=" + ban.getBannedBy();
				break;
			}
			Adapter.getAdapter().runConsoleCommand("ban " + ban.getPlayerId() + " " + timeValue + sender + " " + ban.getReason());
			return new BanResult(BanResultType.DONE, ban);
		} catch (Exception e) {
			e.printStackTrace();
			return new BanResult(BanResultType.EXCEPTION, null);
		}
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		try {
			Ban activeBan = getActiveBan(playerId);
			Adapter.getAdapter().runConsoleCommand("unban " + playerId);
			return new BanResult(activeBan);
		} catch (Exception e) {
			e.printStackTrace();
			return new BanResult(BanResultType.EXCEPTION);
		}
	}

	@Override
	public Ban getActiveBan(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			try (PreparedStatement st = Database.get().prepareStatement("SELECT * FROM {bans} WHERE uuid = ? AND active = ? LIMIT 1")) {
			    st.setString(1, playerId.toString());
			    st.setBoolean(2, true);
			    try (ResultSet rs = st.executeQuery()) {
			        while (rs.next()) {
			            String reason = rs.getString("reason");
			            String bannedByName = rs.getString("banned_by_name");
			            SanctionnerType banType = getSanctionnerType(rs.getString("banned_by_uuid"));
			            String removedByName = rs.getString("removed_by_name");
			            String ip = rs.getString("ip");
			            long revocation = rs.getTimestamp("removed_by_date").getTime();
			            long time = rs.getLong("time");
			            long until = rs.getLong("until");
			            boolean active = rs.getBoolean("active");
			            BanStatus banState = (active ? BanStatus.ACTIVE : (removedByName.equalsIgnoreCase("#expired") ? BanStatus.EXPIRED : BanStatus.REVOKED));
			            return new Ban(playerId, reason, bannedByName, banType, until, reason, ip, banState, time, revocation);
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
		    return null;
		}).join();
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			List<Ban> loggedBans = new ArrayList<>();
			try (PreparedStatement st = Database.get().prepareStatement("SELECT * FROM {bans} WHERE uuid = ?")) {
			    st.setString(1, playerId.toString());
			    try (ResultSet rs = st.executeQuery()) {
			        while (rs.next()) {
			            String reason = rs.getString("reason");
			            String bannedByName = rs.getString("banned_by_name");
			            SanctionnerType banType = getSanctionnerType(rs.getString("banned_by_uuid"));
			            String removedByName = rs.getString("removed_by_name");
			            String ip = rs.getString("ip");
			            long revocation = rs.getTimestamp("removed_by_date").getTime();
			            long time = rs.getLong("time");
			            long until = rs.getLong("until");
			            boolean active = rs.getBoolean("active");
			            BanStatus banState = (active ? BanStatus.ACTIVE : (removedByName.equalsIgnoreCase("#expired") ? BanStatus.EXPIRED : BanStatus.REVOKED));
			            loggedBans.add(new Ban(playerId, reason, bannedByName, banType, until, reason, ip, banState, time, revocation));
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			return loggedBans;
		}).join();
	}
	
	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		return CompletableFuture.supplyAsync(() -> {
			List<Ban> loggedBans = new ArrayList<>();
			try (PreparedStatement st = Database.get().prepareStatement("SELECT * FROM {bans} WHERE ip = ?")) {
			    st.setString(1, ip);
			    try (ResultSet rs = st.executeQuery()) {
			        while (rs.next()) {
			        	UUID playerId = UUID.fromString(rs.getString("uuid"));
			            String reason = rs.getString("reason");
			            String bannedByName = rs.getString("banned_by_name");
			            SanctionnerType banType = getSanctionnerType(rs.getString("banned_by_uuid"));
			            String removedByName = rs.getString("removed_by_name");
			            long revocation = rs.getTimestamp("removed_by_date").getTime();
			            long time = rs.getLong("time");
			            long until = rs.getLong("until");
			            boolean active = rs.getBoolean("active");
			            BanStatus banState = (active ? BanStatus.ACTIVE : (removedByName.equalsIgnoreCase("#expired") ? BanStatus.EXPIRED : BanStatus.REVOKED));
			            loggedBans.add(new Ban(playerId, reason, bannedByName, banType, until, reason, ip, banState, time, revocation));
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			return loggedBans;
		}).join();
	}
	
	@Override
	public List<Ban> getAllBans() {
		return CompletableFuture.supplyAsync(() -> {
			List<Ban> loggedBans = new ArrayList<>();
			try (PreparedStatement st = Database.get().prepareStatement("SELECT * FROM {bans} WHERE active = ?")) {
			    st.setBoolean(1, true);
			    try (ResultSet rs = st.executeQuery()) {
			        while (rs.next()) {
			        	UUID playerId = UUID.fromString(rs.getString("uuid"));
			            String reason = rs.getString("reason");
			            String ip = rs.getString("ip");
			            String bannedByName = rs.getString("banned_by_name");
			            SanctionnerType banType = getSanctionnerType(rs.getString("banned_by_uuid"));
			            long revocation = rs.getTimestamp("removed_by_date").getTime();
			            long time = rs.getLong("time");
			            long until = rs.getLong("until");
			            loggedBans.add(new Ban(playerId, reason, bannedByName, banType, until, reason, ip, BanStatus.ACTIVE, time, revocation));
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			return loggedBans;
		}).join();
	}
	
	@Override
	public String getName() {
		return "LiteBans";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from LiteBans plugin.");
	}
	
	public SanctionnerType getSanctionnerType(String type) {
		if(type.equalsIgnoreCase("CONSOLE"))
			return SanctionnerType.CONSOLE;
		try{
		    UUID.fromString(type);
		    return SanctionnerType.MOD;
		} catch (IllegalArgumentException exception){ /* not UUID */ }
		return SanctionnerType.PLUGIN;
	}
	
	public static class Provider implements BanProcessorProvider, PluginDependentExtension {
		
		@Override
		public String getId() {
			return "litebans";
		}
		
		@Override
		public BanProcessor create(Adapter adapter) {
			return new LiteBansProcessor();
		}
		
		@Override
		public String getPluginId() {
			return "LiteBans";
		}
	}
	
	public static class WarnProvider implements WarnProcessorProvider, PluginDependentExtension {
		
		@Override
		public String getId() {
			return "litebans";
		}
		
		@Override
		public WarnProcessor create(Adapter adapter) {
			return new LiteBansProcessor();
		}
		
		@Override
		public String getPluginId() {
			return "LiteBans";
		}
	}
}
