package com.elikill58.negativity.api.inventory;

import com.elikill58.negativity.api.NegativityObject;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.colors.DyeColor;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class Inventory extends NegativityObject {
	
	public abstract InventoryType getType();
	
	public abstract ItemStack get(int slot);

	public abstract void set(int slot, ItemStack item);
	
	public abstract void remove(int slot);
	
	public abstract void clear();

	public abstract void addItem(ItemStack build);
	
	public abstract int getSize();
	
	public abstract String getInventoryName();
	
	public abstract PlatformHolder getHolder();
	
	public static Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return Adapter.getAdapter().createInventory(inventoryName, size, holder);
	}
	
	public static final String NAME_CHECK_MENU = "Check", ADMIN_MENU = "Admin",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", CHEAT_MANAGER = "Cheat Manager", NAME_FORGE_MOD_MENU = "Mods";
	public static final ItemStack EMPTY;

	static {
		ItemBuilder builder = ItemBuilder.Builder(Materials.GRAY_STAINED_GLASS_PANE);
		if (Materials.GRAY_STAINED_GLASS_PANE.getId().contains("gray")) {
			builder.color(DyeColor.GRAY);
		}
		builder.displayName(ChatColor.RESET.toString() + " - ");
		EMPTY = builder.build();
	}
}
