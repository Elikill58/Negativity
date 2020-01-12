package com.elikill58.negativity.universal.dataStorage.file;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.bukkit.configuration.file.YamlConfiguration;

import com.elikill58.negativity.spigot.SpigotNegativity;
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
			return new NegativityAccount(playerId, TranslatedMessages.getDefaultLang());
		}
		String language = YamlConfiguration.loadConfiguration(file).getString("lang", TranslatedMessages.getDefaultLang());
		return new NegativityAccount(playerId, language);
	}

	@Override
	public void saveAccount(NegativityAccount account) {
		File file = new File(userDir, account.getPlayerId() + ".yml");
		YamlConfiguration accountConfig = YamlConfiguration.loadConfiguration(file);
		accountConfig.set("lang", account.getLang());
		try {
			accountConfig.save(file);
		} catch (IOException e) {
			SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not save account to file.", e);
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void close() {
	}
}
