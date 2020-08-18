package com.elikill58.negativity.universal.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;

import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.api.yaml.config.YamlConfiguration;
import com.elikill58.negativity.universal.DefaultConfigValue;
import com.elikill58.negativity.universal.utils.IOSupplier;

public abstract class MD5ConfigAdapter implements ConfigAdapter {

	protected Configuration config;

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
	public ConfigAdapter getChild(String key) {
		Configuration section = config.getSection(key);
		if (section == null) {
			section = new Configuration();
			config.set(key, section);
		}
		return new Volatile(section);
	}

	@Override
	public Collection<String> getKeys() {
		return config.getKeys();
	}

	@Override
	public void set(String key, Object value) {
		config.set(key, value);
	}

	public static class Volatile extends MD5ConfigAdapter {

		public Volatile(Configuration config) {
			super(config);
		}

		@Override
		public void save() {
		}

		@Override
		public void load() {
		}
	}

	public static class ByProvider extends MD5ConfigAdapter {

		private final Path file;
		private final IOSupplier<InputStream> defaultConfigSupplier;

		public ByProvider(Configuration initialConfig, Path file, IOSupplier<InputStream> defaultConfigSupplier) {
			super(initialConfig);
			this.file = file;
			this.defaultConfigSupplier = defaultConfigSupplier;
		}

		public ByProvider(Path file, IOSupplier<InputStream> defaultConfigSupplier) {
			this(new Configuration(), file, defaultConfigSupplier);
		}

		@Override
		public void save() throws IOException {
			Files.createDirectories(file.getParent());
			YamlConfiguration.save(config, Files.newBufferedWriter(file));
		}

		@Override
		public void load() throws IOException {
			if (Files.notExists(file)) {
				Files.createDirectories(file.getParent());
				try (InputStream defaultConfigIn = defaultConfigSupplier.get()) {
					Files.copy(defaultConfigIn, file, StandardCopyOption.REPLACE_EXISTING);
				}
			}
			config = YamlConfiguration.load(file.toFile(), Files.newBufferedReader(file));
		}
	}
}
