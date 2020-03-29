package com.elikill58.negativity.universal.ban.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.dataStorage.database.DatabaseMigrator;

public class DatabaseBanLogsStorage implements BanLogsStorage {

	public DatabaseBanLogsStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "bans/logs");
			}
		} catch (Exception e) {
			Adapter.getAdapter().error("Failed to execute ban logs database migration: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public List<Ban> load(UUID playerId) {
		List<Ban> loadedBans = new ArrayList<>();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_bans_log WHERE id = ?")) {
			stm.setString(1, playerId.toString());

			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				String reason = rs.getString("reason");
				long expirationTime = rs.getLong("expiration_time");
				String cheatName = rs.getString("cheat_name");
				String bannedBy = rs.getString("banned_by");
				BanStatus status = rs.getBoolean("revoked") ? BanStatus.REVOKED : BanStatus.EXPIRED;
				loadedBans.add(new Ban(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, cheatName, status));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return loadedBans;
	}

	@Override
	public void save(Ban ban) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"INSERT INTO negativity_bans_log (id, reason, banned_by, expiration_time, cheat_name, revoked) VALUES (?,?,?,?,?,?)")) {
			stm.setString(1, ban.getPlayerId().toString());
			stm.setString(2, ban.getReason());
			stm.setString(3, ban.getBannedBy());
			stm.setLong(4, ban.getExpirationTime());
			stm.setString(5, ban.getCheatName());
			stm.setBoolean(6, ban.getStatus() == BanStatus.REVOKED);
			stm.executeUpdate();
		} catch (Exception e) {
			Adapter.getAdapter().error("An error occurred while saving a logged ban of player with ID " + ban.getPlayerId());
			e.printStackTrace();
		}
	}
}
