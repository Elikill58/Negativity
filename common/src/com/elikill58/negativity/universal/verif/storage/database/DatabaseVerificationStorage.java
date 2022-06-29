package com.elikill58.negativity.universal.verif.storage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.database.DatabaseMigrator;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.Verificator;
import com.elikill58.negativity.universal.verif.storage.VerificationStorage;

public class DatabaseVerificationStorage extends VerificationStorage {

	public DatabaseVerificationStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "verifications");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CompletableFuture<List<Verificator>> loadAllVerifications(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			NegativityPlayer np = NegativityPlayer.getCached(playerId);
			List<Verificator> list = new ArrayList<>();
			try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_verifications WHERE uuid = ?")) {
				stm.setString(1, playerId.toString());
				ResultSet resultQuery = stm.executeQuery();
				while (resultQuery.next()) {
					Map<CheatKeys, VerifData> cheats = new HashMap<>(); // don't need to load it
					List<String> result = Arrays.asList(resultQuery.getString("result").split("\n"));
					String startedBy = resultQuery.getString("startedBy");
					int version = resultQuery.getInt("version");
					Version playerVersion = Version.getVersionByName(resultQuery.getString("player_version"));
					list.add(new Verificator(np, startedBy, cheats, result, version, playerVersion));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return list;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletableFuture<Void> saveVerification(Verificator verif) {
		return CompletableFuture.runAsync(() -> {
			try (PreparedStatement stm = Database.getConnection().prepareStatement(
					"INSERT INTO negativity_verifications (uuid, startedBy, result, cheats, player_version, version) VALUES (?, ?, ?, ?, ?, ?)")) {
				stm.setString(1, verif.getPlayerId().toString());
				stm.setString(2, verif.getAsker());
				stm.setString(3, verif.getMessages().stream().collect(Collectors.joining("\n")));
				JSONObject jsonCheat = new JSONObject();
				verif.getCheats().forEach((cheat, verifData) -> {
					if(verifData.hasSomething()) {
						jsonCheat.put(cheat, verifData.toJson());
					} else
						jsonCheat.put(cheat, null);
				});
				stm.setString(4, jsonCheat.toJSONString());
				stm.setString(5, verif.getPlayerVersion().name());
				stm.setInt(6, verif.getVersion());
				stm.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
}
