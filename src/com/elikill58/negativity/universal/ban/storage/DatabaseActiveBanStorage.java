package com.elikill58.negativity.universal.ban.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;

public class DatabaseActiveBanStorage implements ActiveBanStorage {

	@Nullable
	@Override
	public ActiveBan load(UUID playerId) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_active_bans WHERE id = ?")) {
			stm.setString(1, playerId.toString());

			ResultSet rs = stm.executeQuery();
			if (!rs.next()) {
				return null;
			}

			String reason = rs.getString("reason");
			long expirationTime = rs.getLong("expiration_time");
			boolean isDefinitive = rs.getBoolean("definitive");
			String cheatName = rs.getString("cheat_name");
			String bannedBy = rs.getString("banned_by");
			return new ActiveBan(playerId, reason, bannedBy, isDefinitive, BanType.UNKNOW, expirationTime, cheatName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void save(ActiveBan ban) {
		remove(ban.getPlayerId());
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"INSERT INTO negativity_active_bans (id, reason, banned_by, definitive, expiration_time, cheat_name) VALUES (?,?,?,?,?,?)")) {
			stm.setString(1, ban.getPlayerId().toString());
			stm.setString(2, ban.getReason());
			stm.setString(3, ban.getBannedBy());
			stm.setBoolean(4, ban.isDefinitive());
			stm.setLong(5, ban.getExpirationTime());
			stm.setString(6, ban.getCheatName());
			stm.executeUpdate();
		} catch (Exception e) {
			Adapter.getAdapter().error("An error occurred while saving the active ban of player with ID " + ban.getPlayerId());
			e.printStackTrace();
		}
	}

	@Override
	public void remove(UUID playerId) {
		Adapter ada = Adapter.getAdapter();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("DELETE FROM negativity_active_bans WHERE id = ?")) {
			stm.setString(1, playerId.toString());
			stm.executeUpdate();
		} catch (SQLException e) {
			ada.error("An error occurred while removing the active ban for player ID " + playerId);
			e.printStackTrace();
		}
	}
}
