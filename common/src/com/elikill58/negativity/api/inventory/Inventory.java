package com.elikill58.negativity.api.inventory;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityObject;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.colors.DyeColor;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;

public abstract class Inventory implements NegativityObject {
	
	/**
	 * Get the inventory type
	 * 
	 * @return the inventory type
	 */
	public abstract InventoryType getType();
	
	/**
	 * Get the item in the inventory at the given slot
	 * 
	 * @param slot the slot where we will search the item
	 * @return the item at the given slot
	 */
	public abstract @Nullable ItemStack get(int slot);

	/**
	 * Set a new item to the given slot
	 * 
	 * @param slot the slot of the item
	 * @param item the new item
	 */
	public abstract void set(int slot, ItemStack item);
	
	/**
	 * Remove item at the given slot
	 * (Can also be used if you set a null item at the given slot)
	 * 
	 * @param slot the removed item slot
	 */
	public abstract void remove(int slot);
	
	/**
	 * Remove all content of the inventory
	 */
	public abstract void clear();

	/**
	 * Add an item at the first available slot
	 * 
	 * @param build the new item
	 */
	public abstract void addItem(ItemStack build);
	
	/**
	 * Get the size of the inventory
	 * 
	 * @return the inventory size
	 */
	public abstract int getSize();
	
	/**
	 * Get the inventory name
	 * 
	 * @return the inventory name
	 */
	public abstract String getInventoryName();
	
	/**
	 * Get the platform holder.
	 * The platform holder can be just correspond to a platform one
	 * Or can be an {@link NegativityHolder} if it's a Negativity inventory.
	 * 
	 * @return the inventory holder
	 */
	public abstract @Nullable PlatformHolder getHolder();
	
	/**
	 * Create an inventory according to the specific platform
	 * 
	 * @param inventoryName the name of the inventory
	 * @param size the size of the inventory
	 * @param holder the negativity holder that will be applied to the inventory
	 * @return the new inventory
	 */
	public static Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return Adapter.getAdapter().createInventory(inventoryName, size, holder);
	}
	
	public static final String NAME_CHECK_MENU = "Check", ADMIN_MENU = "Admin", ADMIN_ALERT = Messages.getMessage("inventory.alerts.shower.manage"),
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", CHEAT_MANAGER = "Cheat Manager", NAME_FORGE_MOD_MENU = "Mods";
	public static final ItemStack EMPTY;

	static {
		ItemBuilder builder = ItemBuilder.Builder(Materials.GRAY_STAINED_GLASS_PANE);
		if (Materials.GRAY_STAINED_GLASS_PANE.getId().contains("gray")) {
			builder.color(DyeColor.GRAY);
		}
		builder.displayName(ChatColor.RESET + " - ");
		EMPTY = builder.build();
	}
}
