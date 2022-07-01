package com.elikill58.negativity.universal.storage.proof.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.api.yaml.YamlConfiguration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Proof;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.storage.proof.NegativityProofStorage;
import com.elikill58.negativity.universal.storage.proof.OldProofFileMigration;

public class FileNegativityProofStorage extends NegativityProofStorage {

	private final File proofDir;
	
	public FileNegativityProofStorage(File proofDir) {
		this.proofDir = proofDir;
	}
	
	@Override
	public void enable() {
		try {
			proofDir.mkdirs();
			int migrated = 0;
			for(File file : proofDir.listFiles()) {
				if(!file.isFile())
					continue;
				if(file.getName().endsWith(".txt")) {
					UUID uuid = UUID.fromString(file.getName().split("\\.")[0]);
					saveProof(Files.readAllLines(file.toPath()).stream().map(line -> OldProofFileMigration.getProof(uuid, line)).collect(Collectors.toList()));
					file.delete(); // remove old files
					migrated++;
				}
			}
			if(migrated > 0)
				Adapter.getAdapter().getLogger().info("Migrated " + migrated + " files from old (txt) to new system (yml).");
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().printError("Failed to migrate old proof file to new one.", e);
		}
	}

	@Override
	public CompletableFuture<List<Proof>> getProof(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			File file = new File(proofDir, playerId + ".yml");
			if (!file.exists())
				return null;
			Configuration proofConfig = YamlConfiguration.load(file);
			List<Proof> proof = new ArrayList<>();
			proofConfig.getKeys().forEach(key -> proof.addAll(getProofInCheatKeySection(playerId, proofConfig.getSection(key), CheatKeys.fromLowerKey(key))));
			return proof;
		});
	}

	@Override
	public CompletableFuture<List<Proof>> getProofForCheat(UUID playerId, CheatKeys key) {
		return CompletableFuture.supplyAsync(() -> {
			File file = new File(proofDir, playerId + ".yml");
			if (!file.exists())
				return null;
			Configuration proofConfig = YamlConfiguration.load(file);
			if(proofConfig.contains(key.getLowerKey()))
				return getProofInCheatKeySection(playerId, proofConfig.getSection(key.getLowerKey()), key);
			return new ArrayList<>();
		});
	}
	
	private List<Proof> getProofInCheatKeySection(UUID uuid, Configuration config, CheatKeys cheatKey) {
		List<Proof> proof = new ArrayList<>();
		config.getKeys().forEach(key -> {
			Configuration c = config.getSection(key);
			proof.add(new Proof(Integer.parseInt(key), uuid, ReportType.valueOf(c.getString("report_type", "WARNING")), cheatKey, c.getString("check.name"), c.getInt("ping"), c.getInt("amount"), c.getInt("reliability"), new Timestamp(c.getLong("time")), c.getString("check.informations"), Version.getVersion(c.getString("version")), c.getLong("warn"), fileToTps(c.getDoubleList("tps"))));
		});
		return proof;
	}
	
	private List<Double> tpsToFile(double[] tps) {
		List<Double> list = new ArrayList<>();
		for(double d : tps)
			list.add(d);
		return list;
	}
	
	private double[] fileToTps(List<Double> list) {
		double[] tps = new double[list.size()];
		for(int i = 0; i < list.size(); i++)
			tps[i] = list.get(i);
		return tps;
	}

	@Override
	public void saveProof(Proof proof) {
		CompletableFuture.runAsync(() -> {
			File file = new File(proofDir, proof.getUUID().toString() + ".yml");
			if(!file.exists()) {
				try {
					proofDir.mkdirs();
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Configuration accountConfig = YamlConfiguration.load(file);
			Configuration cheatSection = accountConfig.getSection(proof.getCheatKey().getLowerKey());
			if(cheatSection == null)
				cheatSection = accountConfig.createSection(proof.getCheatKey().getLowerKey());
			int key = 0;
			while(cheatSection.contains(String.valueOf(key)))
				key++;
			Configuration proofConfig = cheatSection.createSection(String.valueOf(key));
			proofConfig.set("report_type", proof.getReportType().name());
			proofConfig.set("check.name", proof.getCheckName());
			proofConfig.set("check.informations", proof.getCheckInformations());
			proofConfig.set("ping", proof.getPing());
			proofConfig.set("amount", proof.getAmount());
			proofConfig.set("reliability", proof.getReliability());
			proofConfig.set("time", proof.getTime().getTime());
			proofConfig.set("version", proof.getVersion().name());
			proofConfig.set("warn", proof.getWarn());
			proofConfig.set("tps", tpsToFile(proof.getTps()));
			accountConfig.save();
		});
	}
	
	@Override
	public void saveProof(List<Proof> allProofs) {
		CompletableFuture.runAsync(() -> {
			HashMap<UUID, List<Proof>> proofs = new HashMap<>();
			allProofs.forEach(p -> proofs.computeIfAbsent(p.getUUID(), (a) -> new ArrayList<>()).add(p));
			
			proofs.forEach((uuid, list) -> {
				File file = new File(proofDir, uuid.toString() + ".yml");
				if(!file.exists()) {
					try {
						proofDir.mkdirs();
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Configuration accountConfig = YamlConfiguration.load(file);
				list.forEach(proof -> {
					Configuration cheatSection = accountConfig.getSection(proof.getCheatKey().getLowerKey());
					if(cheatSection == null)
						cheatSection = accountConfig.createSection(proof.getCheatKey().getLowerKey());
					int key = 0;
					while(cheatSection.contains(String.valueOf(key)))
						key++;
					Configuration proofConfig = cheatSection.createSection(String.valueOf(key));
					proofConfig.set("report_type", proof.getReportType().name());
					proofConfig.set("check.name", proof.getCheckName());
					proofConfig.set("check.informations", proof.getCheckInformations());
					proofConfig.set("ping", proof.getPing());
					proofConfig.set("amount", proof.getAmount());
					proofConfig.set("reliability", proof.getReliability());
					proofConfig.set("time", proof.getTime().getTime());
					proofConfig.set("version", proof.getVersion().name());
					proofConfig.set("warn", proof.getWarn());
					proofConfig.set("tps", tpsToFile(proof.getTps()));
				});
				accountConfig.save();
			});
		});
	}
}
