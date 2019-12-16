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
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.DefaultConfigValue;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.translation.CachingTranslationProvider;
import com.elikill58.negativity.universal.translation.ConfigurateTranslationProvider;
import com.elikill58.negativity.universal.translation.TranslationProvider;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class SpongeAdapter extends Adapter implements TranslationProviderFactory {

	private Logger logger;
	private SpongeNegativity pl;
	private LoadingCache<UUID, NegativityAccount> accountCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new NegativityAccountLoader());
	private Path messagesDir;

	public SpongeAdapter(SpongeNegativity sn) {
		this.pl = sn;
		this.logger = sn.getLogger();
		this.messagesDir = sn.getDataFolder().resolve("messages");
	}

	@Override
	public String getName() {
		return "sponge";
	}

	@Override
	public Object getConfig() {
		return SpongeNegativity.getConfig();
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
		ConfigurationNode node = SpongeNegativity.getConfig();
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
	public String getStringInOtherConfig(Path relativeFile, String key, String defaultValue) {
		Path filePath = pl.getDataFolder().resolve(relativeFile);
		if (Files.notExists(filePath))
			return defaultValue;

		try {
			ConfigurationNode node = loadHoconFile(filePath);
			String[] parts = key.split("\\.");
			for (String s : parts)
				node = node.getNode(s);

			return node.getString(defaultValue);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return defaultValue;
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
		else if (lowercaseLang.contains("de"))
			fileName = "de_DE.yml";
		else if (lowercaseLang.contains("nl"))
			fileName = "nl_NL.yml";
		else if (lowercaseLang.contains("sv"))
			fileName = "sv_SV.yml";

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
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this;
	}

	@Nullable
	@Override
	public TranslationProvider createTranslationProvider(String language) {
		String translationFile = language + ".yml";
		Path languageFile = messagesDir.resolve(translationFile);
		try {
			ConfigurationNode messagesNode = loadHoconFile(copy(language, languageFile));
			return new CachingTranslationProvider(new ConfigurateTranslationProvider(messagesNode));
		} catch (IOException e) {
			logger.error("Failed to load translation file {}.", translationFile, e);
			return null;
		}
	}

	@Override
	public void reload() {
		reloadConfig();
		UniversalUtils.init();
		Cheat.loadCheat();
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
		pl.loadConfig();
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
		public NegativityAccount load(UUID playerId) {
			NegativityAccount account = new NegativityAccount(playerId, TranslatedMessages.getLang(playerId), false, new ArrayList<>());

			account.loadBanRequest();
			return account;
		}
	}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, String hover_proof) {
		SpongeNegativity.alertMod(type, (Player) p, c, reliability, hover_proof);
	}

	@Override
	public void runConsoleCommand(String cmd) {
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
	}
}
