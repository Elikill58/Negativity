package com.elikill58.negativity.universal.config;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.spongepowered.api.util.TypeTokens;

import com.elikill58.negativity.universal.DefaultConfigValue;

import ninja.leaping.configurate.ConfigurationNode;

public class SpongeConfigAdapter implements ConfigAdapter {

	private final ConfigurationNode root;
	private final Logger logger;

	public SpongeConfigAdapter(ConfigurationNode root, Logger logger) {
		this.root = root;
		this.logger = logger;
	}

	@Override
	public String getString(String key) {
		try {
			return getFinalNode(key).getValue(TypeTokens.STRING_TOKEN, (Supplier<String>) () -> DefaultConfigValue.getDefaultValueString(key));
		} catch (Exception e) {
			logger.error("Could not get String at {} from the configuration", key, e);
			return DefaultConfigValue.getDefaultValueString(key);
		}
	}

	@Override
	public boolean getBoolean(String key) {
		try {
			return getFinalNode(key).getValue(TypeTokens.BOOLEAN_TOKEN, (Supplier<Boolean>) () -> DefaultConfigValue.getDefaultValueBoolean(key));
		} catch (Exception e) {
			logger.error("Could not get boolean at {} from the configuration", key, e);
			return DefaultConfigValue.getDefaultValueBoolean(key);
		}
	}

	@Override
	public int getInt(String key) {
		try {
			return getFinalNode(key).getValue(TypeTokens.INTEGER_TOKEN, (Supplier<Integer>) () -> DefaultConfigValue.getDefaultValueInt(key));
		} catch (Exception e) {
			logger.error("Could not get int at {} from the configuration", key, e);
			return DefaultConfigValue.getDefaultValueInt(key);
		}
	}

	@Override
	public double getDouble(String key) {
		try {
			return getFinalNode(key).getValue(TypeTokens.DOUBLE_TOKEN, (Supplier<Double>) () -> DefaultConfigValue.getDefaultValueDouble(key));
		} catch (Exception e) {
			logger.error("Could not get double at {} from the configuration", key, e);
			return DefaultConfigValue.getDefaultValueDouble(key);
		}
	}

	@Override
	public List<String> getStringList(String key) {
		try {
			return getFinalNode(key).getList(TypeTokens.STRING_TOKEN);
		} catch (Exception e) {
			logger.error("Could not get String list from the configuration", e);
			return Collections.emptyList();
		}
	}

	@Override
	public void set(String key, Object value) {
		try {
			getFinalNode(key).setValue(value);
		} catch (Exception e) {
			logger.error("Could not set value {} at {} in the configuration", value, key, e);
		}
	}

	private ConfigurationNode getFinalNode(String dir) {
		Object[] path = dir.split("\\.");
		return this.root.getNode(path);
	}
}
