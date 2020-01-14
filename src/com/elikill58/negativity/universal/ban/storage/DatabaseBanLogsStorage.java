package com.elikill58.negativity.universal.ban.storage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.BaseBan;
import com.elikill58.negativity.universal.ban.LoggedBan;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class DatabaseBanLogsStorage implements BanLogsStorage {

	@Override
	public List<LoggedBan> load(UUID playerId) {
		List<LoggedBan> loadedBans = new ArrayList<>();
		Adapter ada = Adapter.getAdapter();
		try (PreparedStatement stm = Database.getConnection()
				.prepareStatement("SELECT * FROM " + Database.table_ban_log + " WHERE " + ada.getStringInConfig("ban.db.column.uuid") + " = ?")) {
			stm.setString(1, playerId.toString());

			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
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
				loadedBans.add(new LoggedBan(playerId, reason, bannedBy, isDefinitive, BanType.UNKNOW, expirationTime, cheatName, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return loadedBans;
	}

	@Override
	public void save(LoggedBan ban) {
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
				content.add(fillPlaceholders(account, ban, hash.get(keys)));
			}

			try (PreparedStatement stm = Database.getConnection().prepareStatement(
					"INSERT INTO " + Database.table_ban_log + "(" + values + ") VALUES (?,?,?,?,?,?" + parentheses + ")")) {
				stm.setString(1, ban.getPlayerId().toString());
				stm.setInt(2, (int) (ban.getExpirationTime()));
				stm.setBoolean(3, ban.isDefinitive());
				stm.setString(4, ban.getReason());
				stm.setString(5, ban.getCheatName());
				stm.setString(6, ban.getBannedBy());
				int i = 7;
				for (String cc : content) {
					String s = fillPlaceholders(account, ban, cc);
					if (UniversalUtils.isInteger(s))
						stm.setInt(i++, Integer.parseInt(s));
					else
						stm.setString(i++, s);
				}
				stm.executeUpdate();
			}
		} catch (Exception e) {
			ada.error("An error occurred while saving a logged ban of player with ID " + ban.getPlayerId());
			e.printStackTrace();
		}
	}

	static String fillPlaceholders(NegativityAccount nAccount, BaseBan ban, String s) {
		String life = "?";
		String name = "???";
		String level = "?";
		String gamemode = "?";
		String walkSpeed = "?";
		NegativityPlayer nPlayer = Adapter.getAdapter().getNegativityPlayer(nAccount.getPlayerId());
		if (nPlayer != null) {
			life = String.valueOf(nPlayer.getLife());
			name = nPlayer.getName();
			level = String.valueOf(nPlayer.getLevel());
			gamemode = nPlayer.getGameMode();
			walkSpeed = String.valueOf(nPlayer.getWalkSpeed());
		}

		return s.replaceAll("%uuid%", nAccount.getUUID())
				.replaceAll("%name%", "")
				.replaceAll("%reason%", ban.getReason())
				.replaceAll("%life%", life)
				.replaceAll("%name%", name)
				.replaceAll("%level%", level)
				.replaceAll("%gm%", gamemode)
				.replaceAll("%walk_speed%", walkSpeed);
	}
}
