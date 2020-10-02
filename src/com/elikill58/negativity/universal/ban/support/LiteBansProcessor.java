package com.elikill58.negativity.universal.ban.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.utils.ChatUtils;

import litebans.api.Database;

public class LiteBansProcessor implements BanProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		try {
			String timeValue = (ban.isDefinitive() ? "" : ChatUtils.getTimeFromLong(ban.getExpirationTime() - ban.getExecutionTime()) + " ");
			String sender;
			switch (ban.getBanType()) {
			case MOD:
				Player mod = Bukkit.getPlayer(ban.getBannedBy());
				if(mod != null) { // get UUID to support when MOD change her name
					sender = "--sender-uuid=" + mod.getUniqueId().toString();
					break;
				}
			case CONSOLE:
			case PLUGIN:
			case UNKNOW:
			default: // no UUID, so just see name
				sender = "--sender=" + ban.getBannedBy();
				break;
			}
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + ban.getPlayerId().toString() + " " + timeValue + sender + " " + ban.getReason());
			return new BanResult(BanResultType.DONE, ban);
		} catch (CommandException e) {
			e.printStackTrace();
			return new BanResult(BanResultType.EXCEPTION, null);
		}
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		try {
			Ban activeBan = getActiveBan(playerId);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unban " + playerId);
			return new BanResult(activeBan);
		} catch (CommandException e) {
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
			            BanType banType = getBanType(rs.getString("banned_by_uuid"));
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
			            BanType banType = getBanType(rs.getString("banned_by_uuid"));
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
			            BanType banType = getBanType(rs.getString("banned_by_uuid"));
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
	
	public BanType getBanType(String type) {
		if(type.equalsIgnoreCase("CONSOLE"))
			return BanType.CONSOLE;
		try{
		    UUID.fromString(type);
		    return BanType.MOD;
		} catch (IllegalArgumentException exception){ /* not UUID */ }
		return BanType.PLUGIN;
	}
}
