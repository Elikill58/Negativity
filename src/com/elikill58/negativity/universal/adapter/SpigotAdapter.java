package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.SpigotTranslationProvider;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.dataStorage.file.SpigotFileNegativityAccountStorage;
import com.elikill58.negativity.universal.config.BukkitConfigAdapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.translation.CachingTranslationProvider;
import com.elikill58.negativity.universal.translation.TranslationProvider;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.common.io.ByteStreams;

public class SpigotAdapter extends Adapter implements TranslationProviderFactory {

	private JavaPlugin pl;
	private ConfigAdapter config;
	private HashMap<UUID, NegativityAccount> account = new HashMap<>();
	/*private LoadingCache<UUID, NegativityAccount> accountCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new NegativityAccountLoader());*/

	public SpigotAdapter(JavaPlugin pl) {
		this.pl = pl;
		this.config = new BukkitConfigAdapter.PluginConfig(pl);
		NegativityAccountStorage.register("file", new SpigotFileNegativityAccountStorage(new File(pl.getDataFolder(), "user")));
	}

	@Override
	public String getName() {
		return "spigot";
	}

	@Override
	public ConfigAdapter getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public void log(String msg) {
		pl.getLogger().info(msg);
	}

	@Override
	public void warn(String msg) {
		pl.getLogger().warning(msg);
	}

	@Override
	public void error(String msg) {
		pl.getLogger().severe(msg);
	}

	@Override
	public File copy(String lang, File f) {
		if (f.exists())
			return f;

		File parentDir = f.getParentFile();
		if (!parentDir.exists())
			parentDir.mkdirs();

		String fileName = "en_US.yml";
		if (lang.toLowerCase().contains("fr") || lang.toLowerCase().contains("be"))
			fileName = "fr_FR.yml";
		else if (lang.toLowerCase().contains("pt") || lang.toLowerCase().contains("br"))
			fileName = "pt_BR.yml";
		else if (lang.toLowerCase().contains("no"))
			fileName = "no_NO.yml";
		else if (lang.toLowerCase().contains("ru"))
			fileName = "ru_RU.yml";
		else if (lang.toLowerCase().contains("zh") || lang.toLowerCase().contains("cn"))
			fileName = "zh_CN.yml";
		else if (lang.toLowerCase().contains("de"))
			fileName = "de_DE.yml";
		else if (lang.toLowerCase().contains("nl"))
			fileName = "nl_NL.yml";
		else if (lang.toLowerCase().contains("sv"))
			fileName = "sv_SV.yml";
		else if (lang.toLowerCase().contains("es"))
			fileName = "es_ES.yml";
		else if (lang.toLowerCase().contains("vi") || lang.toLowerCase().contains("vn"))
			fileName = "vi_VN.yml";
		else if (lang.toLowerCase().contains("pl"))
			fileName = "pl_PL.yml";
		
		try (InputStream in = pl.getResource(fileName); OutputStream out = new FileOutputStream(f)) {
			ByteStreams.copy(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this;
	}

	@Nullable
	@Override
	public TranslationProvider createTranslationProvider(String language) {
		String languageFileName = language + ".yml";
		File translationFile = new File(pl.getDataFolder(), "lang" + File.separator + languageFileName);
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(copy(language, translationFile)), StandardCharsets.UTF_8)) {
			YamlConfiguration msgConfig = YamlConfiguration.loadConfiguration(reader);
			return new CachingTranslationProvider(new SpigotTranslationProvider(msgConfig));
		} catch (Exception e) {
			pl.getLogger().log(Level.SEVERE, "Could not load translation file " + languageFileName, e);
			return null;
		}
	}

	@Nullable
	@Override
	public TranslationProvider createFallbackTranslationProvider() {
		InputStream fallbackResource = SpigotNegativity.getInstance().getResource("en_US.yml");
		if (fallbackResource == null) {
			SpigotNegativity.getInstance().getLogger().warning("Could not find the fallback messages resource.");
			return null;
		}
		try (InputStreamReader fallbackResourceReader = new InputStreamReader(fallbackResource)) {
			YamlConfiguration msgConfig = YamlConfiguration.loadConfiguration(fallbackResourceReader);
			return new CachingTranslationProvider(new SpigotTranslationProvider(msgConfig));
		} catch (Exception e) {
			pl.getLogger().log(Level.SEVERE, "Could not load the fallback translation resource ", e);
			return null;
		}
	}

	@Override
	public void reload() {
		reloadConfig();
		UniversalUtils.init();
		Cheat.loadCheat();
		ProxyCompanionManager.updateForceDisabled(getConfig().getBoolean("disableProxyIntegration"));
		SpigotNegativity.trySendProxyPing();
		SpigotNegativity.setupValue();
		for(Player p : Utils.getOnlinePlayers())
			SpigotNegativity.manageAutoVerif(p);
	}

	@Override
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	@Override
	public void reloadConfig() {
		try {
			getConfig().load();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to reload configuration", e);
		}
	}

	@Nullable
	@Override
	public NegativityPlayer getNegativityPlayer(UUID playerId) {
		Player player = Bukkit.getPlayer(playerId);
		return player != null ? SpigotNegativityPlayer.getNegativityPlayer(player) : null;
	}

	@Override
	public void alertMod(ReportType type, Object p, Cheat c, int reliability, String proof, String hover_proof) {
		SpigotNegativity.alertMod(type, (Player) p, c, reliability, proof, hover_proof);
	}

	@Override
	public void runConsoleCommand(String cmd) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}

	@Nonnull
	@Override
	public NegativityAccount getNegativityAccount(UUID playerId) {
		NegativityAccountStorage storage = NegativityAccountStorage.getStorage();
		if (storage != null) {
			return account.computeIfAbsent(playerId, storage::getOrCreateAccount);
		}

		return new NegativityAccount(playerId);
	}

	@Nullable
	@Override
	public NegativityAccount invalidateAccount(UUID playerId) {
		return account.remove(playerId);
	}

	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				Object data = new JSONParser().parse(response);
				if (data instanceof JSONObject) {
					JSONObject json = (JSONObject) data;
					Object isMcleaks = json.get("isMcleaks");
					if (isMcleaks != null) {
						return Boolean.getBoolean(isMcleaks.toString());
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return false;
		});
	}

	@Override
	public List<UUID> getOnlinePlayers() {
		List<UUID> list = new ArrayList<>();
		for(Player temp : Utils.getOnlinePlayers())
			list.add(temp.getUniqueId());
		return list;
	}
}
