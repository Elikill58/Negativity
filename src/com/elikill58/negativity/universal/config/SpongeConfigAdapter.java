package com.elikill58.negativity.universal.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.spongepowered.api.util.TypeTokens;

import com.elikill58.negativity.universal.DefaultConfigValue;
import com.elikill58.negativity.universal.utils.IOSupplier;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public abstract class SpongeConfigAdapter implements ConfigAdapter {

	protected ConfigurationNode root;
	protected final Logger logger;

	public SpongeConfigAdapter(ConfigurationNode root, Logger logger) {
		this.root = root;
		this.logger = logger;
	}

	@Override
	public boolean contains(String key) {
		return getFinalNode(key).getValue() != null;
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
	public ConfigAdapter getChild(String key) {
		return new Volatile(getFinalNode(key), logger);
	}

	@Override
	public Collection<String> getKeys() {
		Set<Object> originalKeys = root.getChildrenMap().keySet();
		Set<String> keys = new HashSet<>();
		for (Object originalKey : originalKeys) {
			keys.add(originalKey.toString());
		}
		return keys;
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

	public static class Volatile extends SpongeConfigAdapter {

		public Volatile(ConfigurationNode root, Logger logger) {
			super(root, logger);
		}

		@Override
		public void save() {
		}

		@Override
		public void load() {
		}
	}

	public static class ByLoader extends SpongeConfigAdapter {

		private final ConfigurationLoader<?> loader;
		private final Path file;
		private final IOSupplier<InputStream> defaultConfigSupplier;

		public ByLoader(Logger logger, ConfigurationLoader<?> loader, Path file, IOSupplier<InputStream> defaultConfigSupplier) {
			super(loader.createEmptyNode(), logger);
			this.loader = loader;
			this.file = file;
			this.defaultConfigSupplier = defaultConfigSupplier;
		}

		@Override
		public void save() throws IOException {
			Files.createDirectories(file);
			loader.save(root);
		}

		@Override
		public void load() throws IOException {
			if (Files.notExists(file)) {
				Files.createDirectories(file.getParent());
				try (InputStream defaultConfigIn = defaultConfigSupplier.get()) {
					Files.copy(defaultConfigIn, file, StandardCopyOption.REPLACE_EXISTING);
				}
			}
			root = loader.load();
		}
	}
}
