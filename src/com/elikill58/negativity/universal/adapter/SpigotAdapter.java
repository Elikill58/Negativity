package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.SpigotTranslationProvider;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.DefaultConfigValue;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.translation.CachingTranslationProvider;
import com.elikill58.negativity.universal.translation.TranslationProvider;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.common.io.ByteStreams;

public class SpigotAdapter extends Adapter implements TranslationProviderFactory {

	private JavaPlugin pl;
	private HashMap<UUID, NegativityAccount> account = new HashMap<>();
	/*private LoadingCache<UUID, NegativityAccount> accountCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new NegativityAccountLoader());*/

	public SpigotAdapter(JavaPlugin pl) {
		this.pl = pl;
	}

	@Override
	public String getName() {
		return "spigot";
	}

	@Override
	public Object getConfig() {
		return pl.getConfig();
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public String getStringInConfig(String dir) {
		if (pl.getConfig().contains(dir))
			return pl.getConfig().getString(dir);
		return DefaultConfigValue.getDefaultValueString(dir);
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
	public HashMap<String, String> getKeysListInConfig(String dir) {
		HashMap<String, String> list = new HashMap<>();
		ConfigurationSection cs = pl.getConfig().getConfigurationSection(dir);
		if (cs == null)
			return list;
		for (String s : cs.getKeys(false))
			list.put(s, pl.getConfig().getString(dir + "." + s));
		return list;
	}

	@Override
	public boolean getBooleanInConfig(String dir) {
		if (pl.getConfig().contains(dir))
			return pl.getConfig().getBoolean(dir);
		return DefaultConfigValue.getDefaultValueBoolean(dir);
	}

	@Override
	public int getIntegerInConfig(String dir) {
		if (pl.getConfig().contains(dir))
			return pl.getConfig().getInt(dir);
		return DefaultConfigValue.getDefaultValueInt(dir);
	}

	@Override
	public void set(String dir, Object value) {
		pl.getConfig().set(dir, value);
		SpigotNegativity.getInstance().saveConfig();
	}

	@Override
	public double getDoubleInConfig(String dir) {
		if (pl.getConfig().contains(dir))
			return pl.getConfig().getDouble(dir);
		return DefaultConfigValue.getDefaultValueDouble(dir);
	}

	@Override
	public List<String> getStringListInConfig(String dir) {
		return pl.getConfig().getStringList(dir);
	}

	@Override
	public String getStringInOtherConfig(Path relativeFile, String key, String defaultValue) {
		Path configFile = pl.getDataFolder().toPath().resolve(relativeFile);
		if (Files.notExists(configFile))
			return defaultValue;
		return YamlConfiguration.loadConfiguration(configFile.toFile()).getString(key, defaultValue);
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
		SpigotNegativity.isOnBungeecord = getBooleanInConfig("hasBungeecord");
		SpigotNegativity.log = getBooleanInConfig("log_alerts");
		SpigotNegativity.log_console = getBooleanInConfig("log_alerts_in_console");
		SpigotNegativity.hasBypass = getBooleanInConfig("Permissions.bypass.active");
		//Bukkit.getScheduler().cancelAllTasks();
        /*Bukkit.getPluginManager().disablePlugin(sn);
        Bukkit.getPluginManager().enablePlugin(sn);*/
	}

	@Override
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	@Override
	public void reloadConfig() {
		SpigotNegativity.getInstance().reloadConfig();
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
		NegativityAccount existingAccount = account.get(playerId);
		if (existingAccount != null) {
			return existingAccount;
		}

		NegativityAccount na = new NegativityAccount(playerId, TranslatedMessages.getLang(playerId));
		account.put(playerId, na);
		return na;
	}

	@Override
	public void invalidateAccount(UUID playerId) {
		account.remove(playerId);
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
}
