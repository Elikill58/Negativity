package com.elikill58.negativity.universal;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.api.yaml.config.YamlConfiguration;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public abstract class Special {

	public static final String BUNDLED_SPECIAL_MODULES_BASE = Cheat.BUNDLED_MODULES_BASE + "special/";
	private static final Path MODULE_FOLDER = Cheat.MODULE_FOLDER.resolve("special");
	private static final List<Special> SPECIALS = new ArrayList<>();
	private final String key;
	private Configuration config;
	private boolean needPacket;
	private String[] aliases;

	/**
	 * Create a special detection and load his config
	 * 
	 * @param key the key of the special detection
	 * @param needPacket if it need packet to work
	 * @param alias all alias of the special detection
	 */
	public Special(String key, boolean needPacket, String... alias) {
		this.needPacket = needPacket;
		this.key = key.toLowerCase(Locale.ROOT);
		this.aliases = alias;
		
		String fileName = this.key + ".yml";
		Path moduleFile = MODULE_FOLDER.resolve(fileName);
		try {
			moduleFile = UniversalUtils.copyBundledFile(BUNDLED_SPECIAL_MODULES_BASE + fileName, moduleFile);
			if (moduleFile == null) {
				Adapter.getAdapter().getLogger().error("Could not find default module file '" + fileName + "'");
				return;
			}
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Failed to copy default module file '" + fileName + "' to '" + moduleFile + "'");
			e.printStackTrace();
			return;
		}
		
		try (BufferedReader reader = Files.newBufferedReader(moduleFile)) {
			this.config = YamlConfiguration.load(moduleFile.toFile(), reader);
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("Failed to load special " + this.key);
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the special key
	 * 
	 * @return the key in upper case
	 */
	public String getKey() {
		return key.toUpperCase(Locale.ROOT);
	}
	
	/**
	 * Get the configuration of special detection
	 * 
	 * @return the configuration
	 */
	public Configuration getConfig() {
		return config;
	}
	
	/**
	 * Save the configuration of the special detection
	 */
	public void saveConfig() {
		config.save();
	}
	
	/**
	 * Get the name of the special detection
	 * 
	 * @return the name
	 */
	public String getName() {
		return config.getString("name", key);
	}

	/**
	 * Check if the special detection if active
	 * 
	 * @return true if is active
	 */
	public boolean isActive() {
		return config.getBoolean("active", true);
	}

	/**
	 * Know if the special detection need packet to work
	 * 
	 * @return true if it need packet
	 */
	public boolean needPacket() {
		return needPacket;
	}
	
	/**
	 * Get all aliases of the special detection
	 * 
	 * @return all aliases
	 */
	public String[] getAliases() {
		return aliases;
	}
	
	/**
	 * Get special detection from the name
	 * 
	 * @param name the special detection name
	 * @return the special detection or null
	 */
	public static Special fromString(String name) {
		for (Special c : Special.values()) {
			try {
				if (c.getKey().equalsIgnoreCase(name) || c.getName().equalsIgnoreCase(name) || Arrays.asList(c.getAliases()).contains(name))
					return c;
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static List<Special> values() {
		return SPECIALS;
	}
	
	/**
	 * Get special detection from the key
	 * 
	 * @param key the special key
	 * @return the special detection or null
	 */
	public static Special forKey(String key) {
		return SPECIALS.stream().filter((c) -> c.getKey().equalsIgnoreCase(key)).findAny().orElse(null);
	}
	
	/**
	 * Load all special detection
	 * Support reload
	 */
	public static void loadSpecial() {
		SPECIALS.clear();
		Adapter ada = Adapter.getAdapter();
		for (Special special : ServiceLoader.load(Special.class, Special.class.getClassLoader())) {
			if(special instanceof Listeners) {
				try {
					EventManager.registerEvent((Listeners) special);
				} catch (Exception temp) {
					ada.getLogger().error("Failed to load special " + special.getName());
					temp.printStackTrace();
				}
			}
			SPECIALS.add(special);
		}
		ada.getLogger().info("Loaded " + SPECIALS.size() + " special detections.");
	}
}
