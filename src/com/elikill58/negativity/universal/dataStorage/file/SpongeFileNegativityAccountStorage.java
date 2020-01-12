package com.elikill58.negativity.universal.dataStorage.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class SpongeFileNegativityAccountStorage extends NegativityAccountStorage {

	private final Path userDir;

	public SpongeFileNegativityAccountStorage(Path userDir) {
		this.userDir = userDir;
	}

	@Nullable
	@Override
	public NegativityAccount loadAccount(UUID playerId) {
		Path filePath = userDir.resolve(playerId + ".yml");
		if (Files.notExists(filePath)) {
			return new NegativityAccount(playerId, TranslatedMessages.getDefaultLang());
		}

		try {
			ConfigurationNode node = HoconConfigurationLoader.builder().setPath(filePath).build().load();
			String language = node.getNode("lang").getString(TranslatedMessages.getDefaultLang());
			return new NegativityAccount(playerId, language);
		} catch (IOException e) {
			SpongeNegativity.getInstance().getLogger().error("Could not load account {} to file", playerId, e);
		}
		return new NegativityAccount(playerId, TranslatedMessages.getDefaultLang());
	}

	@Override
	public void saveAccount(NegativityAccount account) {
		Path filePath = userDir.resolve(account.getPlayerId() + ".yml");
		HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(filePath).build();
		try {
			CommentedConfigurationNode accountNode = Files.exists(filePath) ? loader.load() : loader.createEmptyNode();
			accountNode.getNode("lang").setValue(account.getLang());
			loader.save(accountNode);
		} catch (IOException e) {
			SpongeNegativity.getInstance().getLogger().error("Could not save account {} to file", account.getPlayerId(), e);
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void close() {
	}
}
