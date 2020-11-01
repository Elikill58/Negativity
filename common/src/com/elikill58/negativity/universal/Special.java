package com.elikill58.negativity.universal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.api.yaml.config.YamlConfiguration;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public abstract class Special {

	private static final File MODULE_FOLDER = new File(Cheat.MODULE_FOLDER, "special");
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
		this.key = key.toLowerCase();
		this.aliases = alias;
		
		File moduleFile = new File(MODULE_FOLDER, this.key + ".yml");
		if(!moduleFile.exists()) {
			try {
				URI migrationsDirUri = Special.class.getResource("/modules/special").toURI();
				if (migrationsDirUri.getScheme().equals("jar")) {
					try (FileSystem jarFs = FileSystems.newFileSystem(migrationsDirUri, Collections.emptyMap())) {
						Path cheatPath = jarFs.getPath("/modules/special", this.key + ".yml");
						if(Files.isRegularFile(cheatPath)) {
							Files.copy(cheatPath, Paths.get(moduleFile.toURI()));
						} else {
							Adapter.getAdapter().getLogger().error("Cannot load cheat " + this.key + ": unable to find default config.");
							return;
						}
					}
				}
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		this.config = YamlConfiguration.load(moduleFile);
	}
	
	/**
	 * Get the special key
	 * 
	 * @return the key in upper case
	 */
	public String getKey() {
		return key.toUpperCase();
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
		try {
			MODULE_FOLDER.mkdirs();
			String dir = Special.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceAll("%20", " ");
			if (dir.endsWith(".class"))
				dir = dir.substring(0, dir.lastIndexOf('!'));

			if (dir.startsWith("file:/"))
				dir = dir.substring(UniversalUtils.getOs() == UniversalUtils.OS.LINUX ? 5 : 6);

			for (Object classDir : UniversalUtils.getClasseNamesInPackage(dir, "com.elikill58.negativity.common.special")) {
				try {
					Special cheat = (Special) Class.forName(classDir.toString().replaceAll(".class", "")).newInstance();
					EventManager.registerEvent((Listeners) cheat);
					SPECIALS.add(cheat);
				} catch (Exception temp) {
					// on ignore
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ada.getLogger().info("Loaded " + SPECIALS.size() + " special detections.");
	}
}
