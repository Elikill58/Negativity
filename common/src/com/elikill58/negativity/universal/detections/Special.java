package com.elikill58.negativity.universal.detections;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.detections.keys.SpecialKeys;

public abstract class Special extends AbstractDetection<SpecialKeys> {

	private static final List<Special> SPECIALS = new ArrayList<>();

	/**
	 * Create a special detection and load his config
	 * 
	 * @param key the key of the special detection
	 * @param material the material used in inventory to represent this special detection
	 */
	public Special(SpecialKeys key, Material material) {
		super(key, material);
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
				if (c.getKey().getKey().equalsIgnoreCase(name) || c.getName().equalsIgnoreCase(name))
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
		return SPECIALS.stream().filter((c) -> c.getKey().getKey().equalsIgnoreCase(key)).findAny().orElse(null);
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
