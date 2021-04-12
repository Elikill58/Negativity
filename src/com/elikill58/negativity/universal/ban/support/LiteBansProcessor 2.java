package com.elikill58.negativity.universal.ban.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;

import litebans.api.Database;

public class LiteBansProcessor implements BanProcessor {

	@Override
	public Ban executeBan(Ban ban) {
		try {
			String timeValue = (ban.isDefinitive() ? "" : getTimeFromLong(ban.getExpirationTime() - ban.getExecutionTime()) + " ");
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
			return ban;
		} catch (CommandException e) {
			return null;
		}
	}

	@Override
	public Ban revokeBan(UUID playerId) {
		try {
			Ban activeBan = getActiveBan(playerId);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unban " + playerId);
			return activeBan;
		} catch (CommandException e) {
			return null;
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
			            long revocation = rs.getTimestamp("removed_by_date").getTime();
			            long time = rs.getLong("time");
			            long until = rs.getLong("until");
			            boolean active = rs.getBoolean("active");
			            BanStatus banState = (active ? BanStatus.ACTIVE : (removedByName.equalsIgnoreCase("#expired") ? BanStatus.EXPIRED : BanStatus.REVOKED));
			            return new Ban(playerId, reason, bannedByName, banType, until, reason, banState, time, revocation);
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
			            long revocation = rs.getTimestamp("removed_by_date").getTime();
			            long time = rs.getLong("time");
			            long until = rs.getLong("until");
			            boolean active = rs.getBoolean("active");
			            BanStatus banState = (active ? BanStatus.ACTIVE : (removedByName.equalsIgnoreCase("#expired") ? BanStatus.EXPIRED : BanStatus.REVOKED));
			            loggedBans.add(new Ban(playerId, reason, bannedByName, banType, until, reason, banState, time, revocation));
			        }
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			return loggedBans;
		}).join();
	}

	public static final long YEARS = 3600 * 24 * 30 * 12;
	public static final long MONTHS = 3600 * 24 * 30;
	public static final long WEEKS = 3600 * 24 * 7;
	public static final long DAYS = 3600 * 24;
	public static final long HOURS = 3600;
	public static final long MINUTES = 60;
	public static final long SECONDS = 1;


	private String getTimeFromLong(long l) {
		long time = l / 1000; // set in seconds from milliseconds
		if(time > YEARS) {
			return (((double) time / YEARS)) + "years ";
		}
		if(time > MONTHS) {
			return (((double) time / MONTHS)) + "mo ";
		}
		if(time > WEEKS) {
			return (((double) time / WEEKS)) + "w ";
		}
		if(time > DAYS) {
			return (((double) time / DAYS)) + "d";
		}
		if(time > HOURS) {
			return (((double) time / HOURS)) + "h ";
		}
		if(time > MINUTES) {
			return (((double) time / MINUTES)) + "m ";
		}
		return (((double) time / SECONDS)) + "s";
	}
	
	public String getFullTimeFromLong(long l) {
		StringJoiner s = new StringJoiner("");
		long time = l / 1000; // set in seconds from milliseconds
		if(time > YEARS) {
			int years = (int) (time / YEARS);
			s.add(years + "years");
			time -= years * YEARS;
		}
		if(time > MONTHS) {
			int months = (int) (time / MONTHS);
			s.add(months + "mo");
			time -= months * MONTHS;
		}
		if(time > WEEKS) {
			int weeks = (int) (time / WEEKS);
			s.add(weeks + "w");
			time -= weeks * WEEKS;
		}
		if(time > DAYS) {
			int days = (int) (time / DAYS);
			s.add(days + "d");
			time -= days * DAYS;
		}
		if(time > HOURS) {
			int hours = (int) (time / HOURS);
			s.add(hours + "h");
			time -= hours * HOURS;
		}
		if(time > MINUTES) {
			int minutes = (int) (time / MINUTES);
			s.add(minutes + "m");
			time -= minutes * MINUTES;
		}
		if(time > SECONDS) {
			int seconds = (int) (time / SECONDS);
			s.add(seconds + "s");
			time -= seconds * SECONDS;
		}
		return s.toString();
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
