package com.elikill58.negativity.universal.warn.processor.hook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.database.DatabaseMigrator;
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnResult;
import com.elikill58.negativity.universal.warn.WarnResult.WarnResultType;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;

public class NegativityDatabaseWarnProcessor implements WarnProcessor {

	@Override
	public void enable() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "warns");
			}
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("Failed to execute active bans database migration: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public WarnResult executeWarn(Warn warn) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"INSERT INTO negativity_warns (uuid, reason, banned_by, sanctionner, ip, expiration_time, active, revocation_time) VALUES (?,?,?,?,?,?,?,?)")) {
			stm.setString(1, warn.getPlayerId().toString());
			stm.setString(2, warn.getReason());
			stm.setString(3, warn.getBannedBy());
			stm.setString(4, warn.getSanctionnerType().name());
			stm.setString(5, warn.getIp());
			stm.setLong(6, warn.getExecutionTime());
			stm.setBoolean(7, warn.isActive());
			stm.setLong(8, warn.getRevocationTime());
			stm.executeUpdate();
			return new WarnResult(WarnResultType.DONE, warn);
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("An error occurred while saving the active warn of player with ID " + warn.getPlayerId());
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public WarnResult revokeWarn(UUID playerId) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"UPDATE negativity_warns SET active = 0, revocation_time = CURRENT_TIMESTAMP WHERE uuid = ?")) {
			stm.setString(1, playerId.toString());
			stm.executeUpdate();
			return new WarnResult(WarnResultType.DONE);
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("An error occurred while saving the active warn of player with ID " + playerId);
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public WarnResult revokeWarn(Warn warn) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"UPDATE negativity_warns SET active = 0, revocation_time = CURRENT_TIMESTAMP WHERE id = ?")) {
			stm.setInt(1, warn.getId());
			stm.executeUpdate();
			return new WarnResult(WarnResultType.DONE);
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("An error occurred while saving the active warn : " + warn);
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public List<Warn> getActiveWarn(UUID playerId) {
		List<Warn> list = new ArrayList<>();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_warns WHERE uuid = ? AND active = 1")) {
			stm.setString(1, playerId.toString());
			ResultSet rs = stm.executeQuery();
			while(rs.next()) {
				list.add(getWarn(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Warn> getActiveWarnOnSameIP(String ip) {
		List<Warn> list = new ArrayList<>();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_warns WHERE ip = ? AND active = 1")) {
			stm.setString(1, ip);
			ResultSet rs = stm.executeQuery();
			while(rs.next()) {
				list.add(getWarn(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Warn> getAllWarns() {
		List<Warn> list = new ArrayList<>();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_warns")) {
			ResultSet rs = stm.executeQuery();
			while(rs.next()) {
				list.add(getWarn(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public String getName() {
		return "Negativity With database";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from Negativity by using database");
	}

	private Warn getWarn(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		UUID playerId = UUID.fromString(rs.getString("uuid"));
		String reason = rs.getString("reason");
		long executionTime = rs.getLong("execution_time");
		String bannedBy = rs.getString("banned_by");
		String ip = rs.getString("ip");
		boolean active = rs.getBoolean("active");
		long revocationTime = rs.getLong("revocation_time");
		return new Warn(id, playerId, reason, bannedBy, SanctionnerType.UNKNOW, ip, executionTime, active, revocationTime);
	}
}
