package com.elikill58.negativity.universal.ban.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.dataStorage.database.DatabaseMigrator;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class DatabaseActiveBanStorage implements ActiveBanStorage {

	public DatabaseActiveBanStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "bans/active");
			}
		} catch (Exception e) {
			Adapter.getAdapter().error("Failed to execute active bans database migration: " + e.getMessage());
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

			String reason = rs.getString("reason");
			long expirationTime = rs.getLong("expiration_time");
			String cheatName = rs.getString("cheat_name");
			String bannedBy = rs.getString("banned_by");
			return new Ban(playerId, reason, bannedBy, BanType.UNKNOW, expirationTime, cheatName, BanStatus.ACTIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void save(Ban ban) {
		remove(ban.getPlayerId());
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"INSERT INTO negativity_bans_active (id, reason, banned_by, expiration_time, cheat_name) VALUES (?,?,?,?,?)")) {
			stm.setString(1, ban.getPlayerId().toString());
			stm.setString(2, ban.getReason());
			stm.setString(3, ban.getBannedBy());
			stm.setLong(4, ban.getExpirationTime());
			stm.setString(5, UniversalUtils.trimExcess(ban.getCheatName(), 64));
			stm.executeUpdate();
		} catch (Exception e) {
			Adapter.getAdapter().error("An error occurred while saving the active ban of player with ID " + ban.getPlayerId());
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
			ada.error("An error occurred while removing the active ban for player ID " + playerId);
			e.printStackTrace();
		}
	}
}
