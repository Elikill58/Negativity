package com.elikill58.negativity.spigot.utils;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ItemUtils {

	public static final Material MATERIAL_CLOSE = getMaterialWithCompatibility("BARRIER", "REDSTONE");
	
	public static final Material SKELETON_SKULL = getMaterialWithCompatibility("SKELETON_SKULL", "SKULL_ITEM", "LEGACY_SKULL_ITEM");
	public static final Material PLAYER_HEAD = getMaterialWithCompatibility("PLAYER_HEAD", "SKULL_ITEM", "LEGACY_SKULL_ITEM");
	
	public static final Material STAINED_CLAY = getMaterialWithCompatibility("STAINED_CLAY", "LEGACY_STAINED_CLAY");
	public static final Material EYE_OF_ENDER = getMaterialWithCompatibility("EYE_OF_ENDER", "LEGACY_EYE_OF_ENDER");
	public static final Material PAPER = getMaterialWithCompatibility("PAPER", "LEGACY_PAPER");
	public static final Material EMPTY_MAP = getMaterialWithCompatibility("EMPTY_MAP", "LEGACY_EMPTY_MAP");
	public static final Material BOOK_AND_QUILL = getMaterialWithCompatibility("BOOK_AND_QUILL", "LEGACY_BOOK_AND_QUILL");
	public static final Material WEB = getMaterialWithCompatibility("WEB", "COBWEB");
	public static final Material FIREBALL = getMaterialWithCompatibility("FIREBALL", "LEGACY_FIREBALL");
	public static final Material FIREWORK = getMaterialWithCompatibility("FIREWORK", "LEGACY_FIREWORK");
	public static final Material LEASH = getMaterialWithCompatibility("LEASH", "LEGACY_LEASH");
	public static final Material NETHER_STAR = getMaterialWithCompatibility("NETHER_STAR", "LEGACY_NETHER_STAR");

	public static final Material STATIONARY_WATER = getMaterialWithCompatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER");
	public static final Material WATER_LILY = getMaterialWithCompatibility("WATER_LILY", "LEGACY_WATER_LILY");
	
	public static final Material GRAY_STAINED_GLASS_PANE = getMaterialWithCompatibility("STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE");
	public static final Material WHITE_STAINED_GLASS = getMaterialWithCompatibility("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS");
	
	public static final Material DIAMOND_SPADE = getMaterialWithCompatibility("DIAMOND_SPADE", "LEGACY_DIAMOND_SPADE");
	public static final Material IRON_SPADE = getMaterialWithCompatibility("IRON_SPADE", "LEGACY_IRON_SPADE");
	
	public static final Material RED_WOOL = getMaterialWithCompatibility("RED_WOOL", "WOOL");
	public static final Material LIME_WOOL = getMaterialWithCompatibility("LIME_WOOL", "WOOL");

	public static final Material BIRCH_WOOD_STAIRS = getMaterialWithCompatibility("BIRCH_WOOD_STAIRS", "BIRCH_STAIRS");
	
	public static Material getMaterialWithCompatibility(String... tempMat) {
		for(String s : tempMat) {
			try {
				Material m = (Material) Material.class.getField(s).get(Material.class);
				if(m != null)
					return m;
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException e2) {
				e2.printStackTrace();
			} catch (NoSuchFieldException e) {}
		}
		String temp = "";
		for(String s : tempMat)
			temp = temp + (temp.equalsIgnoreCase("") ? "" : ", ") + s;
		Adapter.getAdapter().error("Failed to find Material " + temp);
		return null;
	}

	public static ItemStack createItem(Material m, String name, String... lore) {
		return createItem(m, name, 1, lore);
	}

	public static ItemStack createItem(Material m, String name, int quantite, String... lore) {
		ItemStack item = new ItemStack(m, quantite);
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(Utils.coloredMessage(lore));
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack createItem(Material m, String name, int amount, byte b, String... lore) {
		ItemStack item = new ItemStack(m, amount, b);
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack hideAttributes(ItemStack stack) {
		if (Version.getVersion().isNewerThan(Version.V1_7)) {
			ItemMeta meta = stack.getItemMeta();
			// All ItemFlags are used to hide attributes, their javadoc says so too.
			meta.addItemFlags(ItemFlag.values());
			stack.setItemMeta(meta);
		}
		return stack;
	}
}
