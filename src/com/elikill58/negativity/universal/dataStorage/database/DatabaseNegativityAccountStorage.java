package com.elikill58.negativity.universal.dataStorage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;

public class DatabaseNegativityAccountStorage extends NegativityAccountStorage {

	public DatabaseNegativityAccountStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "accounts");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CompletableFuture<@Nullable NegativityAccount> loadAccount(UUID playerId) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_accounts WHERE id = ?")) {
			stm.setString(1, playerId.toString());
			ResultSet result = stm.executeQuery();
			if (result.next()) {
				String language = result.getString("language");
				Minerate minerate = deserializeMinerate(result.getInt("minerate_full_mined"), result.getString("minerate"));
				int mostClicksPerSecond = result.getInt("most_clicks_per_second");
				Map<String, Integer> warns = deserializeViolations(result.getString("violations_by_cheat"));
				return CompletableFuture.completedFuture(new NegativityAccount(playerId, language, minerate, mostClicksPerSecond, warns));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		try (PreparedStatement stm = Database.getConnection().prepareStatement(
				"REPLACE INTO negativity_accounts (id, playername, language, minerate_full_mined, minerate, most_clicks_per_second, violations_by_cheat) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
			stm.setString(1, account.getPlayerId().toString());
			stm.setString(2, null);
			stm.setString(3, account.getLang());
			stm.setInt(4, account.getMinerate().getFullMined());
			stm.setString(5, serializeMinerate(account.getMinerate()));
			stm.setInt(6, account.getMostClicksPerSecond());
			stm.setString(7, serializeViolations(account));
			stm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
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
				Adapter.getAdapter().warn("Malformed minerate value in entry " + fullRateEntry);
			}
		}
		return new Minerate(mined, minerateFullMined);
	}

	private static String serializeViolations(NegativityAccount account) {
		StringJoiner joiner = new StringJoiner(";");
		for (Map.Entry<String, Integer> entry : account.getAllWarns().entrySet()) {
			joiner.add(entry.getKey() + '=' + entry.getValue());
		}
		return joiner.toString();
	}

	private static Map<String, Integer> deserializeViolations(String serialized) {
		Map<String, Integer> violations = new HashMap<>();
		String[] fullEntries = serialized.split(";");
		for (String fullEntry : fullEntries) {
			String[] entry = fullEntry.split("=");
			if (entry.length != 2) {
				continue;
			}

			try {
				int value = Integer.parseInt(entry[1]);
				violations.put(entry[0], value);
			} catch (NumberFormatException e) {
				Adapter.getAdapter().warn("Malformed minerate value in entry " + fullEntry);
			}
		}
		return violations;
	}
}
