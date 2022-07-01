package com.elikill58.negativity.universal.storage.account.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.database.DatabaseMigrator;
import com.elikill58.negativity.universal.report.Report;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;

public class DatabaseNegativityAccountStorage extends NegativityAccountStorage {

	public DatabaseNegativityAccountStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "accounts");
			} else {
				Adapter.getAdapter().getLogger().warn("Can't load account storage because the database isn't fully available.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CompletableFuture<@Nullable NegativityAccount> loadAccount(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_accounts WHERE id = ?")) {
				stm.setString(1, playerId.toString());
				ResultSet result = stm.executeQuery();
				if (result.next()) {
					String playerName = result.getString("playername");
					String language = result.getString("language");
					Minerate minerate = deserializeMinerate(result.getInt("minerate_full_mined"), result.getString("minerate"));
					int mostClicksPerSecond = result.getInt("most_clicks_per_second");
					Map<String, Long> warns = deserializeViolations(result.getString("violations_by_cheat"));
					List<Report> reports = deserializeReports(result.getString("reports"));
					String IP = result.getString("ip");
					long creationTime = result.getTimestamp("creation_time").getTime();
					boolean showAlert = result.getBoolean("show_alert");
					return new NegativityAccount(playerId, playerName, language, minerate, mostClicksPerSecond, warns, reports, IP, creationTime, showAlert);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		return CompletableFuture.runAsync(() -> {
			try (PreparedStatement stm = Database.getConnection().prepareStatement(
					"REPLACE INTO negativity_accounts (id, playername, language, minerate_full_mined, minerate, most_clicks_per_second, violations_by_cheat, reports, ip, creation_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				stm.setString(1, account.getPlayerId().toString());
				stm.setString(2, account.getPlayerName());
				stm.setString(3, account.getLang());
				stm.setInt(4, account.getMinerate().getFullMined());
				stm.setString(5, serializeMinerate(account.getMinerate()));
				stm.setInt(6, account.getMostClicksPerSecond());
				stm.setString(7, serializeViolations(account));
				stm.setString(8, serializeReports(account));
				stm.setString(9, account.getIp());
				stm.setTimestamp(10, new Timestamp(account.getCreationTime()));
				stm.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	private static String serializeMinerate(Minerate minerate) {
		StringJoiner joiner = new StringJoiner(";");
		for (Minerate.MinerateType type : Minerate.MinerateType.values()) {
			Integer rate = minerate.getMinerateType(type);
			joiner.add(type.getOreName() + '=' + rate);
		}
		return joiner.toString();
	}

	private static Minerate deserializeMinerate(int minerateFullMined, String serialized) {
		HashMap<Minerate.MinerateType, Integer> mined = new HashMap<>();
		String[] rateEntries = serialized.split(";");
		for (String fullRateEntry : rateEntries) {
			String[] entry = fullRateEntry.split("=");
			if (entry.length != 2) {
				continue;
			}

			Minerate.MinerateType minerateType = Minerate.MinerateType.getMinerateType(entry[0]);
			if (minerateType == null) {
				continue;
			}

			try {
				int value = Integer.parseInt(entry[1]);
				mined.put(minerateType, value);
			} catch (NumberFormatException e) {
				Adapter.getAdapter().getLogger().warn("Malformed minerate value in entry " + fullRateEntry);
			}
		}
		return new Minerate(mined, minerateFullMined);
	}

	private static String serializeViolations(NegativityAccount account) {
		StringJoiner joiner = new StringJoiner(";");
		for (Map.Entry<String, Long> entry : account.getAllWarns().entrySet()) {
			joiner.add(entry.getKey() + '=' + entry.getValue());
		}
		return joiner.toString();
	}

	private static Map<String, Long> deserializeViolations(String serialized) {
		Map<String, Long> violations = new HashMap<>();
		String[] fullEntries = serialized.split(";");
		for (String fullEntry : fullEntries) {
			String[] entry = fullEntry.split("=");
			if (entry.length != 2) {
				continue;
			}

			try {
				violations.put(entry[0], Long.parseLong(entry[1]));
			} catch (NumberFormatException e) {
				Adapter.getAdapter().getLogger().warn("Malformed minerate value in entry " + fullEntry);
			}
		}
		return violations;
	}

	private static String serializeReports(NegativityAccount account) {
		StringJoiner joiner = new StringJoiner(";");
		account.getReports().forEach((r) -> {
			joiner.add(r.toJsonString());
		});
		return joiner.toString();
	}

	private static List<Report> deserializeReports(String serialized) {
		List<Report> reports = new ArrayList<Report>();
		String[] fullEntries = serialized.split(";");
		for (String fullEntry : fullEntries) {
			Report report = Report.fromJson(fullEntry);
			if(report != null)
				reports.add(report);
		}
		return reports;
	}
	
	@Override
	public List<UUID> getPlayersOnIP(String ip) {
		return CompletableFuture.supplyAsync(() -> {
			List<UUID> list = new ArrayList<>();
			try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_accounts WHERE ip = ?")) {
				stm.setString(1, ip);
				ResultSet result = stm.executeQuery();
				while (result.next()) {
					list.add(UUID.fromString(result.getString("id")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return list;
		}).join();
	}
}
