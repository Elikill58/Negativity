package com.elikill58.negativity.universal.detections;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.api.yaml.YamlConfiguration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public abstract class AbstractDetection<T extends IDetectionKey<T>> implements Comparable<T> {

	protected final T key;
	protected final Material material;
	protected Configuration config;
	protected boolean active = false, disabledJava = false, disabledBedrock = false;
	protected String name;
	
	public AbstractDetection(T key, Material material) {
		this.key = key;
		this.material = material;
		
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
		this.active = config.getBoolean("active", true);
		this.name = config.getString("name", config.getString("exact_name", key.getLowerKey()));
		this.disabledBedrock = config.getBoolean("bedrock.disabled", false);
		this.disabledJava = config.getBoolean("java.disabled", false);
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
	
	public Configuration getConfig() {
		return config;
	}
	
	/**
	 * Save the configuration of the cheat
	 */
	public void saveConfig() {
		CompletableFuture.runAsync(config::save);
	}
	
	/**
	 * Get the name of the special detection
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the exact name of the cheat but for command (without special char, space ...)
	 * 
	 * @return the name formatted
	 */
	public String getCommandName() {
		return name.replace(" ", "").replace("-", "").replace("_", "");
	}

	/**
	 * Check if the special detection if active
	 * 
	 * @return true if is active
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Set if the cheat is active
	 * Warn: this don't save the config
	 * 
	 * @param active the new value
	 * @return the given boolean value
	 */
	public boolean setActive(boolean active) {
		config.set("active", active);
		this.active = active;
		return active;
	}
	
	public boolean isDisabledForBedrock() {
		return disabledBedrock || Negativity.disabledBedrock;
	}
	
	public boolean setDisabledForBedrock(boolean b) {
		disabledBedrock = b;
		config.getBoolean("bedrock.disabled", b);
		return b;
	}
	
	public boolean isDisabledForJava() {
		return disabledJava || Negativity.disabledJava;
	}
	
	public boolean setDisabledForJava(boolean b) {
		disabledJava = b;
		config.getBoolean("java.disabled", b);
		return b;
	}
	
	@Override
	public int compareTo(T o) {
		return key.compareTo(o);
	}
}
