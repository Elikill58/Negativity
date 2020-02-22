package com.elikill58.negativity.universal.dataStorage.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

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

	@Nullable
	@Override
	public NegativityAccount loadAccount(UUID playerId) {
		File file = new File(userDir, playerId + ".yml");
		if (!file.exists()) {
			return new NegativityAccount(playerId);
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		String language = config.getString("lang", TranslatedMessages.getDefaultLang());
		Minerate minerate = deserializeMinerate(config.getConfigurationSection("minerate"));
		int mostClicksPerSecond = config.getInt("better-click");
		Map<String, Integer> warns = deserializeViolations(config.getConfigurationSection("cheats"));
		return new NegativityAccount(playerId, language, minerate, mostClicksPerSecond, warns);
	}

	@Override
	public void saveAccount(NegativityAccount account) {
		File file = new File(userDir, account.getPlayerId() + ".yml");
		YamlConfiguration accountConfig = YamlConfiguration.loadConfiguration(file);
		accountConfig.set("lang", account.getLang());
		serializeMinerate(account.getMinerate(), accountConfig.createSection("minerate"));
		accountConfig.set("better-click", account.getMostClicksPerSecond());
		serializeViolations(account, accountConfig.createSection("cheats"));
		try {
			accountConfig.save(file);
		} catch (IOException e) {
			SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not save account to file.", e);
		}
	}

	private void serializeMinerate(Minerate minerate, ConfigurationSection minerateSection) {
		for (Minerate.MinerateType minerateType : Minerate.MinerateType.values()) {
			String key = minerateType.getName().toLowerCase(Locale.ROOT);
			minerateSection.set(key, minerate.getMinerateType(minerateType));
		}
	}

	private Minerate deserializeMinerate(@Nullable ConfigurationSection minerateSection) {
		Minerate minerate = new Minerate();
		if (minerateSection == null) {
			return minerate;
		}

		for (String minerateKey : minerateSection.getKeys(false)) {
			Minerate.MinerateType type = Minerate.MinerateType.getMinerateType(minerateKey);
			if (type == null) {
				continue;
			}
			minerate.setMine(type, minerateSection.getInt(minerateKey));
		}

		return minerate;
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
