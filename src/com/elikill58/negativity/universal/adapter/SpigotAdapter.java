package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.DefaultConfigValue;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.google.common.io.ByteStreams;

public class SpigotAdapter extends Adapter {

	private FileConfiguration config;
	private JavaPlugin pl;
	private final HashMap<String, YamlConfiguration> LANGS = new HashMap<>();

	public SpigotAdapter(JavaPlugin pl, FileConfiguration config) {
		this.pl = pl;
		this.config = config;
	}

	@Override
	public String getName() {
		return "spigot";
	}

	@Override
	public Object getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public String getStringInConfig(String dir) {
		if (config.contains(dir))
			return config.getString(dir);
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
		ConfigurationSection cs = config.getConfigurationSection(dir);
		if (cs == null)
			return list;
		for (String s : cs.getKeys(false))
			list.put(s, config.getString(dir + "." + s));
		return list;
	}

	@Override
	public boolean getBooleanInConfig(String dir) {
		if (config.contains(dir))
			return config.getBoolean(dir);
		return DefaultConfigValue.getDefaultValueBoolean(dir);
	}

	@Override
	public int getIntegerInConfig(String dir) {
		if (config.contains(dir))
			return config.getInt(dir);
		return DefaultConfigValue.getDefaultValueInt(dir);
	}

	@Override
	public void set(String dir, Object value) {
		config.set(dir, value);
		SpigotNegativity.getInstance().saveConfig();
	}

	@Override
	public double getDoubleInConfig(String dir) {
		if (config.contains(dir))
			return config.getDouble(dir);
		return DefaultConfigValue.getDefaultValueDouble(dir);
	}

	@Override
	public List<String> getStringListInConfig(String dir) {
		return config.getStringList(dir);
	}

	@Override
	public String getStringInOtherConfig(String fileDir, String valueDir, String fileName) {
		File f = new File(pl.getDataFolder().getAbsolutePath() + fileDir);
		if (!f.exists())
			copy(fileName, f);
		return YamlConfiguration.loadConfiguration(f).getString(valueDir);
	}

	@Override
	public File copy(String lang, File f) {
		if (f.exists())
			return f;
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
		// TODO : Espagnol & Allemand
		try (InputStream in = pl.getResource(fileName); OutputStream out = new FileOutputStream(f)) {
			ByteStreams.copy(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	@Override
	public void loadLang() {
		File langDir = new File(pl.getDataFolder().getAbsolutePath() + File.separator + "lang" + File.separator);
		if (!langDir.exists())
			langDir.mkdirs();

		if (!TranslatedMessages.activeTranslation) {
			String defaultLang = TranslatedMessages.DEFAULT_LANG;
			LANGS.put(defaultLang, YamlConfiguration.loadConfiguration(copy(defaultLang, new File(langDir.getAbsolutePath() + "/" + defaultLang + ".yml"))));
			return;
		}

		try {
			for (String l : TranslatedMessages.LANGS)
				LANGS.put(l, YamlConfiguration
						.loadConfiguration(copy(l, new File(langDir.getAbsolutePath() + "/" + l + ".yml"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getStringFromLang(String lang, String key) {
		return LANGS.get(lang).getString(key);
	}

	@Override
	public List<String> getStringListFromLang(String lang, String key) {
		return LANGS.get(lang).getStringList(key);
	}

	@Override
	public void reload() {
		SpigotNegativity sn = SpigotNegativity.getInstance();
		sn.reloadConfig();
		//Bukkit.getScheduler().cancelAllTasks();
        Bukkit.getPluginManager().disablePlugin(sn);
        Bukkit.getPluginManager().enablePlugin(sn);
	}

	@Override
	public Object getItem(String itemName) {
		for(Material m : Material.values())
			if(m.name().equalsIgnoreCase(itemName))
				return m;
		return null;
	}

	@Override
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	@Override
	public void reloadConfig() {
		SpigotNegativity.getInstance().reloadConfig();
	}

	@Nonnull
	@Override
	public NegativityAccount getNegativityAccount(UUID playerId) {
		return SpigotNegativityPlayer.getNegativityPlayer(Bukkit.getOfflinePlayer(playerId));
	}
}
