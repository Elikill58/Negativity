package com.elikill58.negativity.universal.ban.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.ActiveBan;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class DatabaseActiveBanStorage implements ActiveBanStorage {

	@Nullable
	@Override
	public ActiveBan load(UUID playerId) {
		Adapter ada = Adapter.getAdapter();
		try (PreparedStatement stm = Database.getConnection()
				.prepareStatement("SELECT * FROM " + Database.table_ban + " WHERE " + ada.getStringInConfig("ban.db.column.uuid") + " = ?")) {
			stm.setString(1, playerId.toString());

			ResultSet rs = stm.executeQuery();
			if (!rs.next()) {
				return null;
			}

			boolean hasCheatDetect = false, hasBy = false;
			try {
				rs.findColumn(ada.getStringInConfig("ban.db.column.cheat_detect"));
				hasCheatDetect = true;
			} catch (SQLException sqlexce) {
			}
			try {
				rs.findColumn(ada.getStringInConfig("ban.db.column.by"));
				hasBy = true;
			} catch (SQLException sqlexce) {
			}
			String reason = rs.getString(ada.getStringInConfig("ban.db.column.reason"));
			int expirationTime = rs.getInt(ada.getStringInConfig("ban.db.column.time"));
			boolean isDefinitive = rs.getBoolean(ada.getStringInConfig("ban.db.column.def"));
			String cheatName = hasCheatDetect ? rs.getString(ada.getStringInConfig("ban.db.column.cheat_detect")) : "Unknow";
			String bannedBy = hasBy ? rs.getString(ada.getStringInConfig("ban.db.column.by")) : "console";

			return new ActiveBan(playerId, reason, bannedBy, isDefinitive, BanType.UNKNOW, expirationTime, cheatName);
		} catch (Exception e) {
			ada.error("An error occurred while loading the active ban for player ID " + playerId);
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void save(ActiveBan ban) {
		remove(ban.getPlayerId());
		Adapter ada = Adapter.getAdapter();
		try {
			NegativityAccount account = ada.getNegativityAccount(ban.getPlayerId());
			String values = ada.getStringInConfig("ban.db.column.uuid") + ","
					+ ada.getStringInConfig("ban.db.column.time") + ","
					+ ada.getStringInConfig("ban.db.column.def") + ","
					+ ada.getStringInConfig("ban.db.column.reason") + ","
					+ ada.getStringInConfig("ban.db.column.cheat_detect") + ","
					+ ada.getStringInConfig("ban.db.column.by");
			String parentheses = "";
			List<String> content = new ArrayList<>();
			HashMap<String, String> hash = ada.getKeysListInConfig("ban.db.column.other");
			for (String keys : hash.keySet()) {
				values += "," + keys;
				parentheses += ",?";
				content.add(DatabaseBanLogsStorage.fillPlaceholders(account, ban, hash.get(keys)));
			}

			try (PreparedStatement stm = Database.getConnection().prepareStatement(
					"INSERT INTO " + Database.table_ban + "(" + values + ") VALUES (?,?,?,?,?,?" + parentheses + ")")) {
				stm.setString(1, ban.getPlayerId().toString());
				stm.setInt(2, (int) (ban.getExpirationTime()));
				stm.setBoolean(3, ban.isDefinitive());
				stm.setString(4, ban.getReason());
				stm.setString(5, ban.getCheatName());
				stm.setString(6, ban.getBannedBy());
				int i = 7;
				for (String cc : content) {
					String s = DatabaseBanLogsStorage.fillPlaceholders(account, ban, cc);
					if (UniversalUtils.isInteger(s))
						stm.setInt(i++, Integer.parseInt(s));
					else
						stm.setString(i++, s);
				}
				stm.executeUpdate();
			}
		} catch (Exception e) {
			ada.error("An error occurred while saving the active ban for player ID " + ban.getPlayerId());
			e.printStackTrace();
		}
	}

	@Override
	public void remove(UUID playerId) {
		Adapter ada = Adapter.getAdapter();
		String uuidColumnName = ada.getStringInConfig("ban.db.column.uuid");
		try (PreparedStatement stm = Database.getConnection()
				.prepareStatement("DELETE " + Database.table_ban + " WHERE " + uuidColumnName + " = ?")) {
			stm.setString(1, playerId.toString());
			stm.executeUpdate();
		} catch (SQLException e) {
			ada.error("An error occurred while removing the active ban for player ID " + playerId);
			e.printStackTrace();
		}
	}
}
