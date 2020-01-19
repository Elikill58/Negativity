package com.elikill58.negativity.universal.ban.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.LoggedBan;

public class DatabaseBanLogsStorage implements BanLogsStorage {

	@Override
	public List<LoggedBan> load(UUID playerId) {
		List<LoggedBan> loadedBans = new ArrayList<>();
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_bans_log WHERE id = ?")) {
			stm.setString(1, playerId.toString());

			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				String reason = rs.getString("reason");
				long expirationTime = rs.getLong("expiration_time");
				boolean isDefinitive = rs.getBoolean("definitive");
				String cheatName = rs.getString("cheat_name");
				String bannedBy = rs.getString("banned_by");
				boolean revoked = rs.getBoolean("revoked");
				loadedBans.add(new LoggedBan(playerId, reason, bannedBy, isDefinitive, BanType.UNKNOW, expirationTime, cheatName, revoked));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return loadedBans;
	}

	@Override
	public void save(LoggedBan ban) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"INSERT INTO negativity_bans_log (id, reason, banned_by, definitive, expiration_time, cheat_name, revoked) VALUES (?,?,?,?,?,?,?)")) {
			stm.setString(1, ban.getPlayerId().toString());
			stm.setString(2, ban.getReason());
			stm.setString(3, ban.getBannedBy());
			stm.setBoolean(4, ban.isDefinitive());
			stm.setLong(5, ban.getExpirationTime());
			stm.setString(6, ban.getCheatName());
			stm.setBoolean(7, ban.isRevoked());
			stm.executeUpdate();
		} catch (Exception e) {
			Adapter.getAdapter().error("An error occurred while saving a logged ban of player with ID " + ban.getPlayerId());
			e.printStackTrace();
		}
	}
}
