package com.elikill58.negativity.universal.config;

import java.util.Collection;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.universal.DefaultConfigValue;

public abstract class BukkitConfigAdapter implements ConfigAdapter {

	protected ConfigurationSection config;

	public BukkitConfigAdapter(ConfigurationSection config) {
		this.config = config;
	}
	
	@Override
	public boolean contains(String key) {
		return config.contains(key);
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
	public ConfigAdapter getChild(String key) {
		ConfigurationSection section = config.getConfigurationSection(key);
		if (section == null) {
			section = config.createSection(key);
		}
		return new BukkitConfigAdapter.Volatile(section);
	}

	@Override
	public Collection<String> getKeys() {
		return config.getKeys(false);
	}

	@Override
	public void set(String key, Object value) {
		config.set(key, value);
	}

	public static class Volatile extends BukkitConfigAdapter {

		public Volatile(ConfigurationSection config) {
			super(config);
		}

		@Override
		public void save() {
		}

		@Override
		public void load() {
		}
	}

	public static class PluginConfig extends BukkitConfigAdapter {

		private final Plugin plugin;

		public PluginConfig(Plugin plugin) {
			super(plugin.getConfig());
			this.plugin = plugin;
		}

		@Override
		public void save() {
			this.plugin.saveConfig();
		}

		@Override
		public void load() {
			this.plugin.saveDefaultConfig();
			this.plugin.reloadConfig();
			this.config = this.plugin.getConfig();
		}
	}
}
