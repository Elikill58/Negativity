package com.elikill58.negativity.universal.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.universal.DefaultConfigValue;

public class BukkitConfigAdapter implements ConfigAdapter {

	private final Plugin plugin;
	private final FileConfiguration config;

	public BukkitConfigAdapter(Plugin plugin, FileConfiguration config) {
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public String getString(String key) {
		if (config.contains(key)) {
			return config.getString(key);
		}
		return DefaultConfigValue.getDefaultValueString(key);
	}

	@Override
	public boolean getBoolean(String key) {
		if (config.contains(key)) {
			return config.getBoolean(key);
		}
		return DefaultConfigValue.getDefaultValueBoolean(key);
	}

	@Override
	public int getInt(String key) {
		if (config.contains(key)) {
			return config.getInt(key);
		}
		return DefaultConfigValue.getDefaultValueInt(key);
	}

	@Override
	public double getDouble(String key) {
		if (config.contains(key)) {
			return config.getDouble(key);
		}
		return DefaultConfigValue.getDefaultValueDouble(key);
	}

	@Override
	public List<String> getStringList(String key) {
		return config.getStringList(key);
	}

	@Override
	public void set(String key, Object value) {
		config.set(key, value);
		plugin.saveConfig();
	}
}
