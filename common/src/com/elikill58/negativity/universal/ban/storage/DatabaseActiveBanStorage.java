package com.elikill58.negativity.universal.ban.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.database.DatabaseMigrator;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class DatabaseActiveBanStorage implements ActiveBanStorage {

	public DatabaseActiveBanStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "bans/active");
			}
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("Failed to execute active bans database migration: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Nullable
	@Override
	public Ban load(UUID playerId) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_bans_active WHERE id = ?")) {
			stm.setString(1, playerId.toString());

			ResultSet rs = stm.executeQuery();
			if (!rs.next()) {
				return null;
			}
			return getBan(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void save(Ban ban) {
		remove(ban.getPlayerId());
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"INSERT INTO negativity_bans_active (id, reason, banned_by, expiration_time, cheat_name, ip, execution_time) VALUES (?,?,?,?,?,?,?)")) {
			stm.setString(1, ban.getPlayerId().toString());
			stm.setString(2, ban.getReason());
			stm.setString(3, ban.getBannedBy());
			stm.setLong(4, ban.getExpirationTime());
			stm.setString(5, UniversalUtils.truncate(ban.getCheatName(), 64));
			stm.setString(6, ban.getIp());
			stm.setTimestamp(7, ban.getExecutionTime() >= 0 ? new Timestamp(ban.getExecutionTime()) : null);
			stm.executeUpdate();
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("An error occurred while saving the active ban of player with ID " + ban.getPlayerId());
			e.printStackTrace();
		}
	}

	@Override
	public void remove(UUID playerId) {
		Adapter ada = Adapter.getAdapter();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("DELETE FROM negativity_bans_active WHERE id = ?")) {
			stm.setString(1, playerId.toString());
			stm.executeUpdate();
		} catch (SQLException e) {
			ada.getLogger().error("An error occurred while removing the active ban for player ID " + playerId);
			e.printStackTrace();
		}
	}
	
	@Override
	public List<Ban> loadBanOnIP(String ip) {
		List<Ban> list = new ArrayList<>();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_bans_active WHERE ip = ?")) {
			stm.setString(1, ip);

			ResultSet rs = stm.executeQuery();
			while(rs.next()) {
				list.add(getBan(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

	@Override
	public List<Ban> getAll() {
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_bans_active")) {
			ResultSet rs = stm.executeQuery();
			List<Ban> list = new ArrayList<Ban>();
			while(rs.next()) {
				list.add(getBan(rs));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Ban>();
	}

	private Ban getBan(ResultSet rs) throws SQLException {
		UUID playerId = UUID.fromString(rs.getString("id"));
		String reason = rs.getString("reason");
		long expirationTime = rs.getLong("expiration_time");
		String cheatName = rs.getString("cheat_name");
		String bannedBy = rs.getString("banned_by");
		String ip = rs.getString("ip");
		Timestamp executionTimestamp = rs.getTimestamp("execution_time");
		long executionTime = executionTimestamp == null ? -1 : executionTimestamp.getTime();
		return new Ban(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, cheatName, ip, BanStatus.ACTIVE, executionTime);
	}
}
