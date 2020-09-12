package com.elikill58.negativity.universal.dataStorage.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.api.yaml.config.YamlConfiguration;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.Report;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;

public class FileNegativityAccountStorage extends NegativityAccountStorage {

	private final File userDir;

	public FileNegativityAccountStorage(File userDir) {
		this.userDir = userDir;
	}

	@Override
	public CompletableFuture<@Nullable NegativityAccount> loadAccount(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			File file = new File(userDir, playerId + ".yml");
			if (!file.exists()) {
				return new NegativityAccount(playerId);
			}
			Configuration config = YamlConfiguration.load(file);
			String playerName = config.getString("playername");
			String language = config.getString("lang", TranslatedMessages.getDefaultLang());
			Minerate minerate = deserializeMinerate(config.getInt("minerate-full-mined"), config.getSection("minerate"));
			int mostClicksPerSecond = config.getInt("better-click");
			Map<String, Integer> warns = deserializeViolations(config.getSection("cheats"));
			List<Report> reports = deserializeReports(config);
			long creationTime = config.getLong("creation-time", System.currentTimeMillis());
			return new NegativityAccount(playerId, playerName, language, minerate, mostClicksPerSecond, warns, reports, creationTime);
		});
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		return CompletableFuture.runAsync(() -> {
			File file = new File(userDir, account.getPlayerId() + ".yml");
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Configuration accountConfig = YamlConfiguration.load(file);
			accountConfig.set("playername", account.getPlayerName());
			accountConfig.set("lang", account.getLang());
			accountConfig.set("minerate-full-mined", account.getMinerate().getFullMined());
			serializeMinerate(account.getMinerate(), accountConfig.createSection("minerate"));
			accountConfig.set("better-click", account.getMostClicksPerSecond());
			serializeViolations(account, accountConfig.createSection("cheats"));
			serializeReports(account, accountConfig);
			accountConfig.set("creation-time", account.getCreationTime());
			accountConfig.save();
		});
	}

	private void serializeMinerate(Minerate minerate, Configuration minerateSection) {
		for (Minerate.MinerateType minerateType : Minerate.MinerateType.values()) {
			String key = minerateType.getName().toLowerCase(Locale.ROOT);
			minerateSection.set(key, minerate.getMinerateType(minerateType));
		}
	}

	private Minerate deserializeMinerate(int fullMined, @Nullable Configuration minerateSection) {
		HashMap<Minerate.MinerateType, Integer> mined = new HashMap<>();
		if (minerateSection == null) {
			return new Minerate(mined, fullMined);
		}

		for (String minerateKey : minerateSection.getKeys()) {
			Minerate.MinerateType type = Minerate.MinerateType.getMinerateType(minerateKey);
			if (type == null) {
				continue;
			}
			mined.put(type, minerateSection.getInt(minerateKey));
		}

		return new Minerate(mined, fullMined);
	}

	private void serializeViolations(NegativityAccount account, Configuration cheatsSection) {
		for (Map.Entry<String, Integer> entry : account.getAllWarns().entrySet()) {
			String cheatKey = entry.getKey().toLowerCase(Locale.ROOT);
			cheatsSection.set(cheatKey, entry.getValue());
		}
	}

	private Map<String, Integer> deserializeViolations(@Nullable Configuration cheatsSection) {
		Map<String, Integer> violations = new HashMap<>();
		if (cheatsSection == null) {
			return violations;
		}

		for (String cheatKey : cheatsSection.getKeys()) {
			violations.put(cheatKey, cheatsSection.getInt(cheatKey));
		}
		return violations;
	}

	private void serializeReports(NegativityAccount account, Configuration section) {
		List<String> list = new ArrayList<>();
		account.getReports().forEach((r) -> {
			list.add(r.toJsonString());
		});
		section.set("reports", list);
	}

	private List<Report> deserializeReports(@Nullable Configuration cheatsSection) {
		List<Report> reports = new ArrayList<>();
		if (cheatsSection == null) {
			return reports;
		}

		for (String fullEntry : cheatsSection.getStringList("reports")) {
			reports.add(Report.fromJson(fullEntry));
		}
		return reports;
	}
}
