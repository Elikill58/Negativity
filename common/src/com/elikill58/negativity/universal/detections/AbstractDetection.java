package com.elikill58.negativity.universal.detections;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.api.yaml.YamlConfiguration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.detections.keys.IDetectionKeys;
import com.elikill58.negativity.universal.file.FileSaverTimer;
import com.elikill58.negativity.universal.file.hook.FileRunnableSaverAction;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public abstract class AbstractDetection<T extends IDetectionKeys<T>> {

	protected final T key;
	protected final Material material;
	protected final boolean needPacket;
	protected final String[] aliases;
	protected Configuration config;
	
	public AbstractDetection(T key, Material material, boolean needPacket, String... aliases) {
		this.key = key;
		this.material = material;
		this.needPacket = needPacket;
		this.aliases = aliases;

		
		String fileName = key.getLowerKey() + ".yml";
		Path moduleFile = key.getFolder().resolve(fileName);
		try {
			moduleFile = UniversalUtils.copyBundledFile(key.getPathBundle() + fileName, moduleFile);
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
			Adapter.getAdapter().getLogger().error("Failed to load cheat " + this.key);
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the key of the actual detection
	 * 
	 * @return the detection key
	 */
	public T getKey() {
		return key;
	}

	/**
	 * Get the detection material which can be showed on inventory
	 * 
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Get all alias of this detection
	 * 
	 * @return cheat aliases
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Check if the cheat need packet for at least one detection
	 * 
	 * @return true if the detection need packet
	 */
	public boolean needPacket() {
		return needPacket;
	}
	
	public Configuration getConfig() {
		return config;
	}
	
	/**
	 * Save the configuration of the cheat
	 */
	public void saveConfig() {
		FileSaverTimer.getInstance().addAction(new FileRunnableSaverAction(config::save));
	}
	
	/**
	 * Get the name of the special detection
	 * 
	 * @return the name
	 */
	public String getName() {
		return config.getString("name", key.getLowerKey());
	}

	/**
	 * Check if the special detection if active
	 * 
	 * @return true if is active
	 */
	public boolean isActive() {
		return config.getBoolean("active", true);
	}
}
