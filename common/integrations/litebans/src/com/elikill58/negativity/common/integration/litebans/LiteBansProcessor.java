package com.elikill58.negativity.common.integration.litebans;

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
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnResult;
import com.elikill58.negativity.universal.warn.WarnResult.WarnResultType;
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
				if (modUUID != null) { // get UUID to support when MOD change her name
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
						return getBan(rs);
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
						loggedBans.add(getBan(rs));
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
						loggedBans.add(getBan(rs));
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
						loggedBans.add(getBan(rs));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return loggedBans;
		}).join();
	}

	@Override
	public WarnResult executeWarn(Warn warn) {
		try {
			String sender;
			if (UniversalUtils.isUUID(warn.getWarnedBy())) {
				sender = "--sender-uuid=" + warn.getWarnedBy().toString();
			} else {
				sender = "--sender=" + warn.getWarnedBy();
			}
			Adapter.getAdapter().runConsoleCommand("warn " + warn.getPlayerId() + " " + sender + " " + warn.getReason());
			return new WarnResult(WarnResultType.DONE);
		} catch (Exception e) {
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public WarnResult revokeWarn(UUID playerId, String revoker) {
		try {
			Adapter.getAdapter().runConsoleCommand("unwarn " + playerId + " --sender" + (UniversalUtils.isUUID(revoker) ? "-uuid" : "") + "=" + revoker);
			return new WarnResult(WarnResultType.DONE);
		} catch (Exception e) {
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public WarnResult revokeWarn(Warn warn, String revoker) {
		try {
			Adapter.getAdapter().runConsoleCommand("unwarn " + warn.getPlayerId() + " --sender" + (UniversalUtils.isUUID(revoker) ? "-uuid" : "") + "=" + revoker);
			return new WarnResult(WarnResultType.DONE);
		} catch (Exception e) {
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public List<Warn> getWarn(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			List<Warn> loggedBans = new ArrayList<>();
			try (PreparedStatement st = Database.get().prepareStatement("SELECT * FROM {warnings} WHERE uuid = ?")) {
				st.setString(1, playerId.toString());
				try (ResultSet rs = st.executeQuery()) {
					while (rs.next()) {
						loggedBans.add(getWarn(rs));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return loggedBans;
		}).join();
	}

	@Override
	public List<Warn> getActiveWarnOnSameIP(String ip) {
		return CompletableFuture.supplyAsync(() -> {
			List<Warn> loggedBans = new ArrayList<>();
			try (PreparedStatement st = Database.get().prepareStatement("SELECT * FROM {warnings} WHERE ip = ?")) {
				st.setString(1, ip);
				try (ResultSet rs = st.executeQuery()) {
					while (rs.next()) {
						loggedBans.add(getWarn(rs));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return loggedBans;
		}).join();
	}

	private Warn getWarn(ResultSet rs) throws SQLException {
		String bannedUUID = rs.getString("banned_by_uuid");
		return new Warn(rs.getInt("id"), UUID.fromString(rs.getString("uuid")), rs.getString("reason"), bannedUUID == null ? rs.getString("banned_by_name") : bannedUUID,
				getSanctionnerType(bannedUUID), rs.getString("ip"), rs.getLong("time"), rs.getBoolean("active"), rs.getTimestamp("removed_by_date").getTime(),
				rs.getString("removed_by_uuid"));
	}

	private Ban getBan(ResultSet rs) throws SQLException {
		String reason = rs.getString("reason");
		SanctionnerType banType = getSanctionnerType(rs.getString("banned_by_uuid"));
		String removedName = rs.getString("removed_by_name");
		return new Ban(UUID.fromString(rs.getString("uuid")), reason, rs.getString("banned_by_name"), banType, rs.getLong("until"), reason, rs.getString("ip"),
				rs.getBoolean("active") ? BanStatus.ACTIVE : (removedName.equalsIgnoreCase("#expired") ? BanStatus.EXPIRED : BanStatus.REVOKED), rs.getLong("time"),
				rs.getTimestamp("removed_by_date").getTime());
	}

	@Override
	public String getName() {
		return "LiteBans";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from LiteBans plugin.", "", ChatColor.RED + "Not available:", "&6- Remove all warns for player (only one/one)");
	}

	public SanctionnerType getSanctionnerType(String type) {
		if (type == null)
			return SanctionnerType.UNKNOW;
		if (type.equalsIgnoreCase("CONSOLE"))
			return SanctionnerType.CONSOLE;
		try {
			UUID.fromString(type);
			return SanctionnerType.MOD;
		} catch (IllegalArgumentException exception) {
			/* not UUID */ }
		return SanctionnerType.PLUGIN;
	}

	public static class Provider implements BanProcessorProvider, WarnProcessorProvider, PluginDependentExtension {

		@Override
		public String getId() {
			return "litebans";
		}

		@Override
		public LiteBansProcessor create(Adapter adapter) {
			return new LiteBansProcessor();
		}

		@Override
		public String getPluginId() {
			return "LiteBans";
		}
	}
}
