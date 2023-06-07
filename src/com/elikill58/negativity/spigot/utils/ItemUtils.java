package com.elikill58.negativity.spigot.utils;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;

@SuppressWarnings("deprecation")
public class ItemUtils {

	public static final Material MATERIAL_CLOSE = getMaterialWithCompatibility("BARRIER", "REDSTONE");
	
	public static final Material SKELETON_SKULL = getMaterialWithCompatibility("SKELETON_SKULL", "SKULL_ITEM");
	public static final Material PLAYER_HEAD = getMaterialWithCompatibility("PLAYER_HEAD", "SKULL_ITEM");
	
	// items
	public static final Material EYE_OF_ENDER = getMaterialWithCompatibility("EYE_OF_ENDER", "ENDER_EYE");
	public static final Material EMPTY_MAP = getMaterialWithCompatibility("EMPTY_MAP", "MAP");
	public static final Material BOOK_AND_QUILL = getMaterialWithCompatibility("BOOK_AND_QUILL", "WRITTEN_BOOK");
	public static final Material WEB = getMaterialWithCompatibility("WEB", "COBWEB");
	public static final Material FIREBALL = getMaterialWithCompatibility("FIREBALL", "FIRE_CHARGE");
	public static final Material FIREWORK = getMaterialWithCompatibility("FIREWORK", "FIREWORK_ROCKET");
	public static final Material LEASH = getMaterialWithCompatibility("LEASH", "LEAD");

	// blocks
	public static final Material STATIONARY_WATER = getMaterialWithCompatibility("STATIONARY_WATER", "WATER");
	public static final Material WATER_LILY = getMaterialWithCompatibility("WATER_LILY", "LILY_PAD");
	public static final Material GRASS = getMaterialWithCompatibility("GRASS_BLOCK", "GRASS");
	public static final Material SCAFFOLD = getMaterialWithCompatibility("SCAFFOLD", "VINE");

	public static final Material BIRCH_WOOD_STAIRS = getMaterialWithCompatibility("BIRCH_WOOD_STAIRS", "BIRCH_STAIRS");
	
	// colored items
	public static final Material GRAY_STAINED_GLASS_PANE = getMaterialWithCompatibility("STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE");
	public static final Material WHITE_STAINED_GLASS = getMaterialWithCompatibility("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE");
	
	public static final Material LIME_STAINED_CLAY = getMaterialWithCompatibility("STAINED_CLAY", "LIME_TERRACOTTA");
	public static final Material ORANGE_STAINED_CLAY = getMaterialWithCompatibility("STAINED_CLAY", "ORANGE_TERRACOTTA");
	public static final Material RED_STAINED_CLAY = getMaterialWithCompatibility("STAINED_CLAY", "RED_TERRACOTTA");
	
	public static final Material RED_WOOL = getMaterialWithCompatibility("RED_WOOL", "WOOL");
	public static final Material LIME_WOOL = getMaterialWithCompatibility("LIME_WOOL", "WOOL");
	
	// tools
	public static final Material DIAMOND_SPADE = getMaterialWithCompatibility("DIAMOND_SPADE", "DIAMOND_SHOVEL");
	public static final Material IRON_SPADE = getMaterialWithCompatibility("IRON_SPADE", "IRON_SHOVEL");
	
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
		Adapter.getAdapter().getLogger().error("Failed to find Material " + temp);
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

	public static ItemStack createItem(Material m, String name, int amount, byte b, String... lore) {
		ItemStack item = new ItemStack(m, amount, b);
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack hideAttributes(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		// All ItemFlags are used to hide attributes, their javadoc says so too.
		meta.addItemFlags(ItemFlag.values());
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static boolean isItemBypass(ItemUseBypass use, ItemStack item) {
		if(use == null || item == null || !item.getType().name().equalsIgnoreCase(use.getItem()))
			return false;
		ConfigAdapter c = use.getConfig();
		ItemMeta meta = item.getItemMeta();
		if(c.contains("name")) {
			if(meta == null || !(meta.hasDisplayName() && meta.getDisplayName().equalsIgnoreCase(c.getString("name")))) // wrong name
				return false;
		}
		if(c.contains("unbreakable")) {
			if(meta.isUnbreakable() != c.getBoolean("unbreakable"))
				return false;
		}
		if(c.contains("enchants")) {
			for(String enchant : c.getStringList("enchants")) {
				if(!meta.hasEnchant(Enchantment.getByName(enchant.toUpperCase())))
					return false;
			}
		}
		return true;
	}
}
