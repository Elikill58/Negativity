package com.elikill58.negativity.universal.dataStorage.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

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

	@Override
	public CompletableFuture<@Nullable NegativityAccount> loadAccount(UUID playerId) {
		Path filePath = userDir.resolve(playerId + ".yml");
		if (Files.notExists(filePath)) {
			return CompletableFuture.completedFuture(new NegativityAccount(playerId));
		}

		try {
			ConfigurationNode node = HoconConfigurationLoader.builder().setPath(filePath).build().load();
			String playerName = node.getNode("playername").getString();
			String language = node.getNode("lang").getString(TranslatedMessages.getDefaultLang());
			Minerate minerate = deserializeMinerate(node.getNode("minerate-full-mined").getInt(), node.getNode("minerate"));
			int mostClicksPerSecond = node.getNode("better-click").getInt();
			Map<String, Integer> warns = deserializeViolations(node.getNode("cheats"));
			return CompletableFuture.completedFuture(new NegativityAccount(playerId, playerName, language, minerate, mostClicksPerSecond, warns));
		} catch (IOException e) {
			SpongeNegativity.getInstance().getLogger().error("Could not load account {} to file", playerId, e);
		}
		return CompletableFuture.completedFuture(new NegativityAccount(playerId));
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		Path filePath = userDir.resolve(account.getPlayerId() + ".yml");
		HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(filePath).build();
		try {
			Files.createDirectories(userDir);
			CommentedConfigurationNode accountNode = Files.exists(filePath) ? loader.load() : loader.createEmptyNode();
			accountNode.getNode("playername").setValue(account.getPlayerName());
			accountNode.getNode("lang").setValue(account.getLang());
			accountNode.getNode("minerate-full-mined").setValue(account.getMinerate().getFullMined());
			serializeMinerate(account.getMinerate(), accountNode.getNode("minerate"));
			accountNode.getNode("better-click").setValue(account.getMostClicksPerSecond());
			serializeViolations(account, accountNode.getNode("cheats"));
			loader.save(accountNode);
		} catch (IOException e) {
			SpongeNegativity.getInstance().getLogger().error("Could not save account {} to file", account.getPlayerId(), e);
		}
		return CompletableFuture.completedFuture(null);
	}

	private static void serializeMinerate(Minerate minerate, ConfigurationNode minerateNode) {
		for (Minerate.MinerateType minerateType : Minerate.MinerateType.values()) {
			String minerateKey = minerateType.getName().toLowerCase(Locale.ROOT);
			Integer value = minerate.getMinerateType(minerateType);
			minerateNode.getNode(minerateKey).setValue(value);
		}
	}

	private static Minerate deserializeMinerate(int fullMined, ConfigurationNode minerateNode) {
		HashMap<Minerate.MinerateType, Integer> mined = new HashMap<>();
		for (Map.Entry<Object, ? extends ConfigurationNode> minerateEntry : minerateNode.getChildrenMap().entrySet()) {
			Minerate.MinerateType minerateType = Minerate.MinerateType.getMinerateType(minerateEntry.getKey().toString());
			if (minerateType == null) {
				continue;
			}
			int value = minerateEntry.getValue().getInt();
			mined.put(minerateType, value);
		}
		return new Minerate(mined, fullMined);
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
