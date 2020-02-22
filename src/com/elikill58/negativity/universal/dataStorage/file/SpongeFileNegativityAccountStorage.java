package com.elikill58.negativity.universal.dataStorage.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.universal.Minerate;
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
			return new NegativityAccount(playerId);
		}

		try {
			ConfigurationNode node = HoconConfigurationLoader.builder().setPath(filePath).build().load();
			String language = node.getNode("lang").getString(TranslatedMessages.getDefaultLang());
			Minerate minerate = deserializeMinerate(node.getNode("minerate"));
			int mostClicksPerSecond = node.getNode("better-click").getInt();
			Map<String, Integer> warns = deserializeViolations(node.getNode("cheats"));
			return new NegativityAccount(playerId, language, minerate, mostClicksPerSecond, warns);
		} catch (IOException e) {
			SpongeNegativity.getInstance().getLogger().error("Could not load account {} to file", playerId, e);
		}
		return new NegativityAccount(playerId);
	}

	@Override
	public void saveAccount(NegativityAccount account) {
		Path filePath = userDir.resolve(account.getPlayerId() + ".yml");
		HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(filePath).build();
		try {
			Files.createDirectories(userDir);
			CommentedConfigurationNode accountNode = Files.exists(filePath) ? loader.load() : loader.createEmptyNode();
			accountNode.getNode("lang").setValue(account.getLang());
			serializeMinerate(account.getMinerate(), accountNode.getNode("minerate"));
			accountNode.getNode("better-click").setValue(account.getMostClicksPerSecond());
			serializeViolations(account, accountNode.getNode("cheats"));
			loader.save(accountNode);
		} catch (IOException e) {
			SpongeNegativity.getInstance().getLogger().error("Could not save account {} to file", account.getPlayerId(), e);
		}
	}

	private static void serializeMinerate(Minerate minerate, ConfigurationNode minerateNode) {
		for (Minerate.MinerateType minerateType : Minerate.MinerateType.values()) {
			String minerateKey = minerateType.getName().toLowerCase(Locale.ROOT);
			Integer value = minerate.getMinerateType(minerateType);
			minerateNode.getNode(minerateKey).setValue(value);
		}
	}

	private static Minerate deserializeMinerate(ConfigurationNode minerateNode) {
		Minerate minerate = new Minerate();
		for (Map.Entry<Object, ? extends ConfigurationNode> minerateEntry : minerateNode.getChildrenMap().entrySet()) {
			Minerate.MinerateType minerateType = Minerate.MinerateType.getMinerateType(minerateEntry.getKey().toString());
			if (minerateType == null) {
				continue;
			}
			int value = minerateEntry.getValue().getInt();
			minerate.setMine(minerateType, value);
		}
		return minerate;
	}

	private void serializeViolations(NegativityAccount account, CommentedConfigurationNode cheatsNode) {
		for (Map.Entry<String, Integer> entry : account.getAllWarns().entrySet()) {
			String key = entry.getKey().toLowerCase(Locale.ROOT);
			cheatsNode.getNode(key).setValue(entry.getValue());
		}
	}

	private Map<String, Integer> deserializeViolations(ConfigurationNode cheatsNode) {
		Map<String, Integer> violations = new HashMap<>();
		for (Map.Entry<Object, ? extends ConfigurationNode> entry : cheatsNode.getChildrenMap().entrySet()) {
			String cheatkey = entry.getKey().toString();
			int value = entry.getValue().getInt();
			violations.put(cheatkey, value);
		}
		return violations;
	}
}
