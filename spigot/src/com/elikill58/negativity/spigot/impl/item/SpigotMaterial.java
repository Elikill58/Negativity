package com.elikill58.negativity.spigot.impl.item;

import java.util.Arrays;

import com.elikill58.negativity.api.item.Material;

public class SpigotMaterial extends Material {

	private final org.bukkit.Material type;
	private final byte damage;
	
	public SpigotMaterial(org.bukkit.Material type, String id) {
		this.type = type;
		this.damage = getDamageWithId(id.toUpperCase());
	}
	
	private byte getDamageWithId(String id) {
		// STAINED_CLAY = TERRACOTA for 1.12 and lower
		for(String types : Arrays.asList("_WOOL", "_STAINED_CLAY", "_CARPET", "_STAINED_GLASS_PANE"))
			if(id.endsWith(types))
				return getByteForWoolTerracotaGlassCarpet(id.replace(types, ""));
		if(id.endsWith("_BANNER"))
			return getByteForBanner(id.replace("_BANNER", ""));
		if(id.endsWith("_WOOD"))
			return getForWood(id.replace("_WOOD", ""));
		return -1;
	}
	
	/**
	 * Get the data value for wool, terracota, glass and carpet color<p>
	 * Source:<p>
	 * https://minecraft.gamepedia.com/Java_Edition_data_value/Pre-flattening#Wool.2C_terracotta.2C_stained_Glass_and_carpet
	 * 
	 * @param color the color name (in upper case)
	 * @return the data value in byte
	 */
	private byte getByteForWoolTerracotaGlassCarpet(String color) {
		switch (color) {
		case "ORANGE":
			return 1;
		case "MAGENTA":
			return 2;
		case "LIGHT_BLUE":
			return 3;
		case "YELLOW":
			return 4;
		case "LIME":
			return 5;
		case "PINK":
			return 6;
		case "GRAY":
			return 7;
		case "LIGHT_GRAY":
			return 8;
		case "CYAN":
			return 9;
		case "PURPLE":
			return 10;
		case "BLUE":
			return 11;
		case "BROWN":
			return 12;
		case "GREEN":
			return 13;
		case "RED":
			return 14;
		case "BLACK":
			return 15;
		default: // white color
			return 0;
		}
	}

	/**
	 * Get the data value for banner color<p>
	 * 
	 * @param color the color name (in upper case)
	 * @return the data value in byte
	 */
	private byte getByteForBanner(String color) {
		switch (color) {
		case "WHITE":
			return 15;
		case "ORANGE":
			return 14;
		case "MAGENTA":
			return 13;
		case "LIGHT_BLUE":
			return 12;
		case "YELLOW":
			return 11;
		case "LIME":
			return 10;
		case "PINK":
			return 9;
		case "GRAY":
			return 8;
		case "LIGHT_GRAY":
			return 7;
		case "CYAN":
			return 6;
		case "PURPLE":
			return 5;
		case "BLUE":
			return 4;
		case "BROWN":
			return 3;
		case "GREEN":
			return 2;
		case "RED":
			return 1;
		default: // black color
			return 0;
		}
	}
	
	/**
	 * Get the data value for wood type<p>
	 * Source:<p>
	 * https://minecraft.gamepedia.com/Java_Edition_data_value/Pre-flattening#Wood
	 * 
	 * @param woodName the wood type name (in upper case)
	 * @return the data value in byte
	 */
	private byte getForWood(String woodName) {
		switch (woodName) {
		case "SPRUCE":
			return 1;
		case "BIRCH":
			return 2;
		case "JUNGLE":
			return 3;
		default:
			return 0;
		}
	}
	
	@Override
	public boolean isSolid() {
		return type.isSolid();
	}

	@Override
	public String getId() {
		return type.name();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isTransparent() {
		return type.isTransparent();
	}
	
	/**
	 * Get the damage value for Spigot 1.12 and lower
	 * 
	 * @return the byte damage
	 */
	public byte getDamage() {
		return damage;
	}

	@Override
	public Object getDefault() {
		return type;
	}
}
