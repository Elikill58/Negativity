package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.TypeTokens;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.DefaultConfigValue;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class SpongeAdapter extends Adapter {

	private ConfigurationNode config;
	private Logger logger;
	private SpongeNegativity pl;
	private final HashMap<String, ConfigurationNode> LANGS = new HashMap<>();
	private LoadingCache<UUID, NegativityAccount> accountCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new NegativityAccountLoader());

	public SpongeAdapter(SpongeNegativity sn) {
		this.pl = sn;
		this.logger = sn.getLogger();
		this.config = SpongeNegativity.getConfig();
	}

	@Override
	public String getName() {
		return "sponge";
	}

	@Override
	public Object getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder().toFile();
	}

	@Override
	public String getStringInConfig(String dir) {
		try {
			return getFinalNode(dir).getString();
		} catch (Exception e) {
			return DefaultConfigValue.getDefaultValueString(dir);
		}
	}

	@Override
	public boolean getBooleanInConfig(String dir) {
		try {
			return getFinalNode(dir).getBoolean();
		} catch (Exception e) {
			return DefaultConfigValue.getDefaultValueBoolean(dir);
		}
	}

	private ConfigurationNode getFinalNode(String dir) throws Exception {
		ConfigurationNode node = config;
		String[] parts = dir.split("\\.");
		for (String s : parts)
			node = node.getNode(s);
		return node;
	}

	private ConfigurationNode getFinalNode(String dir, ConfigurationNode localConf) throws Exception {
		ConfigurationNode node = localConf;
		String[] parts = dir.split("\\.");
		for (String s : parts)
			node = node.getNode(s);
		return node;
	}

	@Override
	public void log(String msg) {
		logger.info(msg);
	}

	@Override
	public void warn(String msg) {
		logger.warn(msg);
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public HashMap<String, String> getKeysListInConfig(String dir) {
		final HashMap<String, String> hash = new HashMap<>();
		try {
			getFinalNode(dir).getChildrenMap().forEach((obj, cn) -> {
				hash.put(obj.toString(), cn.getString());
			});
		} catch (Exception e) {
		}
		return hash;
	}

	@Override
	public int getIntegerInConfig(String dir) {
		try {
			return getFinalNode(dir).getInt();
		} catch (Exception e) {
			return DefaultConfigValue.getDefaultValueInt(dir);
		}
	}

	@Override
	public void set(String dir, Object value) {
		try {
			getFinalNode(dir).setValue(value);
		} catch (Exception e) {
		}
	}

	@Override
	public double getDoubleInConfig(String dir) {
		try {
			return getFinalNode(dir).getDouble();
		} catch (Exception e) {
			return DefaultConfigValue.getDefaultValueDouble(dir);
		}
	}

	@Override
	public List<String> getStringListInConfig(String dir) {
		try {
			return getFinalNode(dir).getList(TypeTokens.STRING_TOKEN);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public String getStringInOtherConfig(String fileDir, String valuePath, String fileName) {
		// Guard for usages that cannot be changed because other Adapter implementations are not updated.
		// If fileDir starts with File.separatorChar it is considered an absolute path by Path#resolve, definitely not what we want.
		if (fileDir.charAt(0) == File.separatorChar)
			fileDir = fileDir.substring(1);

		Path filePath = pl.getDataFolder().resolve(fileDir).resolve(fileName);
		if (Files.notExists(filePath))
			copy(fileName, filePath);

		try {
			ConfigurationNode node = loadHoconFile(filePath);
			String[] parts = valuePath.split("\\.");
			for (String s : parts)
				node = node.getNode(s);

			return node.getString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public File copy(String lang, File f) {
		return copy(lang, f.toPath()).toFile();
	}

	public Path copy(String lang, Path filePath) {
		String fileName = "en_US.yml";
		String lowercaseLang = lang.toLowerCase();
		if (lowercaseLang.contains("fr") || lowercaseLang.contains("be"))
			fileName = "fr_FR.yml";
		else if (lowercaseLang.contains("pt") || lowercaseLang.contains("br"))
			fileName = "pt_BR.yml";
		else if (lowercaseLang.contains("no"))
			fileName = "no_NO.yml";
		else if (lowercaseLang.contains("ru"))
			fileName = "ru_RU.yml";
		else if (lowercaseLang.contains("zh") || lowercaseLang.contains("cn"))
			fileName = "zh_CN.yml";
		// TODO : Espagnol & Allemand

		if (Files.notExists(filePath)) {
			pl.getContainer().getAsset(fileName).ifPresent(asset -> {
				try {
					Path parentDir = filePath.normalize().getParent();
					if (parentDir != null) {
						Files.createDirectories(parentDir);
					}

					asset.copyToFile(filePath, false);
				} catch (IOException e) {
					logger.error("Failed to copy default language file " + asset.getFileName(), e);
				}
			});
		}

		return filePath;
	}

	private ConfigurationNode loadHoconFile(Path filePath) throws IOException {
		return HoconConfigurationLoader.builder().setPath(filePath).build().load();
	}

	@Override
	public void loadLang() {
		Path messagesDir = pl.getDataFolder().resolve("messages");

		try {
			for (String lang : TranslatedMessages.LANGS) {
				LANGS.put(lang, loadHoconFile(copy(lang, messagesDir.resolve(lang + ".yml"))));
			}
		} catch (IOException e) {
			logger.error("Failed to copy default language files", e);
		}
	}

	@Override
	public String getStringFromLang(String lang, String key) {
		try {
			String node = getFinalNode(key, LANGS.get(lang)).getString();
			return node == null ? DefaultConfigValue.STRINGS.get(key) : node;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<String> getStringListFromLang(String lang, String key) {
		try {
			return getFinalNode(key, LANGS.get(lang)).getList(TypeTokens.STRING_TOKEN);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/*private void load(String l, ConfigurationNode config) {
		config.getChildrenMap().forEach((objKey, cn) -> {
			String key = objKey.toString();
			Object obj = cn.getValue();
			if (obj instanceof ConfigurationNode) {
				load(l, cn.getNode());
			} else if (obj instanceof String) {
				HashMap<String, String> msg = LANG_MSG.containsKey(cn.getString()) ? LANG_MSG.get(cn.getString())
						: new HashMap<>();
				msg.put(key, config.getString(key));
				LANG_MSG.put(l, msg);
			} else if (obj instanceof List) {
				HashMap<String, List<String>> msg = LANG_MSG_LIST.containsKey(key) ? LANG_MSG_LIST.get(key)
						: new HashMap<>();
				try {
					msg.put(key, config.getList(TypeToken.of(String.class)));
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
				LANG_MSG_LIST.put(l, msg);
			} else {
				System.out.println("[Negativity] Unknow type for " + obj.getClass() + " Lang: " + l);
			}
		});
	}*/

	@Override
	public void reload() {
	}

	@Override
	public Object getItem(String itemName) {
		Collection<ItemType> list = Sponge.getRegistry().getAllOf(ItemType.class);
		for(ItemType it : list)
			if(it.getName().equalsIgnoreCase(itemName))
				return it;
		return null;
	}

	@Override
	public String getVersion() {
		return Sponge.getPlatform().getMinecraftVersion().getName();
	}

	@Override
	public void reloadConfig() {
	}

	@Nonnull
	@Override
	public NegativityAccount getNegativityAccount(UUID playerId) {
		try {
			return accountCache.get(playerId);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Nullable
	@Override
	public NegativityPlayer getNegativityPlayer(UUID playerId) {
		Player player = Sponge.getServer().getPlayer(playerId).orElse(null);
		return player != null ? SpongeNegativityPlayer.getNegativityPlayer(player) : null;
	}

	@Override
	public void invalidateAccount(UUID playerId) {
		accountCache.invalidate(playerId);
	}

	private static class NegativityAccountLoader extends CacheLoader<UUID, NegativityAccount> {

		@Override
		public NegativityAccount load(UUID playerId) throws Exception {
			NegativityAccount account = new NegativityAccount(playerId);

			Path userFilePath = SpongeNegativity.getInstance().getDataFolder().resolve("user").resolve(playerId.toString() + ".yml");
			ConfigurationNode userData = HoconConfigurationLoader.builder().setPath(userFilePath).build().load();
			account.setLang(userData.getNode("lang").getString(TranslatedMessages.DEFAULT_LANG));

			account.loadBanRequest();
			return account;
		}
	}
}
