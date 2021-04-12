package com.elikill58.negativity.universal.dataStorage.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;

public class SpigotFileNegativityAccountStorage extends NegativityAccountStorage {

	private final File userDir;

	public SpigotFileNegativityAccountStorage(File userDir) {
		this.userDir = userDir;
	}

	@Override
	public CompletableFuture<@Nullable NegativityAccount> loadAccount(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			File file = new File(userDir, playerId + ".yml");
			if (!file.exists()) {
				return new NegativityAccount(playerId);
			}
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			String playerName = config.getString("playername");
			String language = config.getString("lang", TranslatedMessages.getDefaultLang());
			Minerate minerate = deserializeMinerate(config.getInt("minerate-full-mined"), config.getConfigurationSection("minerate"));
			int mostClicksPerSecond = config.getInt("better-click");
			Map<String, Integer> warns = deserializeViolations(config.getConfigurationSection("cheats"));
			long creationTime = config.getLong("creation-time", System.currentTimeMillis());
			return new NegativityAccount(playerId, playerName, language, minerate, mostClicksPerSecond, warns, creationTime);
		});
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		return CompletableFuture.runAsync(() -> {
			File file = new File(userDir, account.getPlayerId() + ".yml");
			YamlConfiguration accountConfig = YamlConfiguration.loadConfiguration(file);
			accountConfig.set("playername", account.getPlayerName());
			accountConfig.set("lang", account.getLang());
			accountConfig.set("minerate-full-mined", account.getMinerate().getFullMined());
			serializeMinerate(account.getMinerate(), accountConfig.createSection("minerate"));
			accountConfig.set("better-click", account.getMostClicksPerSecond());
			serializeViolations(account, accountConfig.createSection("cheats"));
			accountConfig.set("creation-time", account.getCreationTime());
			try {
				accountConfig.save(file);
			} catch (IOException e) {
				SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not save account to file.", e);
			}
		});
	}

	private void serializeMinerate(Minerate minerate, ConfigurationSection minerateSection) {
		for (Minerate.MinerateType minerateType : Minerate.MinerateType.values()) {
			String key = minerateType.getName().toLowerCase(Locale.ROOT);
			minerateSection.set(key, minerate.getMinerateType(minerateType));
		}
	}

	private Minerate deserializeMinerate(int fullMined, @Nullable ConfigurationSection minerateSection) {
		HashMap<Minerate.MinerateType, Integer> mined = new HashMap<>();
		if (minerateSection == null) {
			return new Minerate(mined, fullMined);
		}

		for (String minerateKey : minerateSection.getKeys(false)) {
			Minerate.MinerateType type = Minerate.MinerateType.getMinerateType(minerateKey);
			if (type == null) {
				continue;
			}
			mined.put(type, minerateSection.getInt(minerateKey));
		}

		return new Minerate(mined, fullMined);
	}

	private void serializeViolations(NegativityAccount account, ConfigurationSection cheatsSection) {
		for (Map.Entry<String, Integer> entry : account.getAllWarns().entrySet()) {
			String cheatKey = entry.getKey().toLowerCase(Locale.ROOT);
			cheatsSection.set(cheatKey, entry.getValue());
		}
	}

	private Map<String, Integer> deserializeViolations(@Nullable ConfigurationSection cheatsSection) {
		Map<String, Integer> violations = new HashMap<>();
		if (cheatsSection == null) {
			return violations;
		}

		for (String cheatKey : cheatsSection.getKeys(false)) {
			violations.put(cheatKey, cheatsSection.getInt(cheatKey));
		}
		return violations;
	}
}
