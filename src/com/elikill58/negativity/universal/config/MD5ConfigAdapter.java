package com.elikill58.negativity.universal.config;

import java.util.List;

import com.elikill58.negativity.universal.DefaultConfigValue;

import net.md_5.bungee.config.Configuration;

public class MD5ConfigAdapter implements ConfigAdapter {

	private final Configuration config;

	public MD5ConfigAdapter(Configuration config) {
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
	}
}
