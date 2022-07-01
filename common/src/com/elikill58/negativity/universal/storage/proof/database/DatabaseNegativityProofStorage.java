package com.elikill58.negativity.universal.storage.proof.database;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Proof;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.database.DatabaseMigrator;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.storage.proof.NegativityProofStorage;
import com.elikill58.negativity.universal.storage.proof.OldProofFileMigration;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class DatabaseNegativityProofStorage extends NegativityProofStorage {

	public DatabaseNegativityProofStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "proofs");
			} else {
				Adapter.getAdapter().getLogger().warn("Can't load proof storage because the database isn't fully available.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void enable() {
		try {
			File folder = Adapter.getAdapter().getDataFolder().toPath().resolve("user").resolve("proof").toFile();
			if(!folder.exists())
				return;
			int migrated = 0;
			for(File file : Adapter.getAdapter().getDataFolder().toPath().resolve("user").resolve("proof").toFile().listFiles()) {
				if(!file.isFile())
					continue;
				UUID uuid = UUID.fromString(file.getName().split("\\.")[0]);
				if(file.getName().endsWith(".txt")) {
					saveProof(Files.readAllLines(file.toPath()).stream().map(line -> OldProofFileMigration.getProof(uuid, line)).collect(Collectors.toList()));
					migrated++;
				} else if(file.getName().endsWith(".yml")) {
					saveProof(NegativityProofStorage.getStorages().get("file").getProof(uuid).join());
					migrated++;
				}
			}
			folder.delete();
			if(migrated > 0)
				Adapter.getAdapter().getLogger().info("Migrated " + migrated + " files from text files to database one.");
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().printError("Failed to migrate old proof file to new database.", e);
		}
	}

	@Override
	public CompletableFuture<List<Proof>> getProof(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			List<Proof> proofs = new ArrayList<>();
			try (PreparedStatement stm = Database.getConnection()
					.prepareStatement("SELECT * FROM negativity_proofs WHERE uuid = ?")) {
				stm.setString(1, playerId.toString());
				ResultSet result = stm.executeQuery();
				while (result.next()) {
					proofs.add(getWithSet(result));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return proofs;
		});
	}

	@Override
	public CompletableFuture<List<Proof>> getProofForCheat(UUID playerId, CheatKeys key) {
		return CompletableFuture.supplyAsync(() -> {
			List<Proof> proofs = new ArrayList<>();
			try (PreparedStatement stm = Database.getConnection()
					.prepareStatement("SELECT * FROM negativity_proofs WHERE uuid = ? AND cheat_key = ?")) {
				stm.setString(1, playerId.toString());
				stm.setString(2, key.getLowerKey());
				ResultSet result = stm.executeQuery();
				while (result.next()) {
					proofs.add(getWithSet(result));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return proofs;
		});
	}

	private Proof getWithSet(ResultSet rs) throws SQLException {
		return new Proof(rs.getInt("id"), UUID.fromString(rs.getString("uuid")), ReportType.valueOf(rs.getString("report_type")), CheatKeys.fromLowerKey(rs.getString("check_key")),
				rs.getString("check_name"), rs.getInt("ping"), rs.getLong("amount"), rs.getInt("reliability"),
				rs.getTimestamp("time"), rs.getString("check_informations"),
				Version.getVersion(rs.getString("version")), rs.getLong("warn"), sqlToSql(rs.getString("tps")));
	}

	private String tpsToSql(double[] tps) {
		StringJoiner sql = new StringJoiner(";");
		for (double d : tps)
			sql.add(String.valueOf(d));
		return sql.toString();
	}

	private double[] sqlToSql(String sql) {
		String[] args = sql.split(";");
		double[] tps = new double[args.length];
		for(int i = 0; i < args.length; i++) {
			String s = args[i];
			if (s.isEmpty() || !UniversalUtils.isDouble(s))
				continue;
			tps[i] = Double.parseDouble(s);
		}
		return tps;
	}

	@Override
	public void saveProof(Proof proof) {
		CompletableFuture.runAsync(() -> {
			try (PreparedStatement stm = Database.getConnection().prepareStatement(
					"INSERT INTO negativity_proofs(uuid, report_type, cheat_key, check_name, ping, amount, reliability, time, check_informations, version, warn, tps) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)")) {
				stm.setString(1, proof.getUUID().toString());
				stm.setString(2, proof.getReportType().name());
				stm.setString(3, proof.getCheatKey().getLowerKey());
				stm.setString(4, proof.getCheckName());
				stm.setInt(5, proof.getPing());
				stm.setLong(6, proof.getAmount());
				stm.setInt(7, proof.getReliability());
				stm.setTimestamp(8, proof.getTime());
				stm.setString(9, proof.getCheckInformations());
				stm.setString(10, proof.getVersion().name());
				stm.setLong(11, proof.getWarn());
				stm.setString(12, tpsToSql(proof.getTps()));
				stm.executeUpdate();
			} catch (SQLException e) {
				Adapter.getAdapter().getLogger().printError("Failed to save proof " + proof, e);
			}
		});
	}
}
