package com.elikill58.negativity.api.inventory;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.item.Material;

public abstract class AbstractInventory {

	public static final List<AbstractInventory> INVENTORIES = new ArrayList<>();
	
	private final NegativityInventory type;
	
	public AbstractInventory(NegativityInventory type) {
		this.type = type;
		INVENTORIES.add(this);
	}
	
	public NegativityInventory getType() {
		return type;
	}

	public abstract boolean isInstance(NegativityHolder nh);
	public abstract void openInventory(Player p, Object... args);
	public void closeInventory(Player p, InventoryCloseEvent e) {}
	public abstract void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh);
	public void actualizeInventory(Player p, Object... args) {}
	
	public static enum NegativityInventory {
		ACTIVED_CHEAT,
		ADMIN,
		ALERT,
		CHECK_MENU,
		CHEAT_MANAGER,
		FREEZE,
		MOD,
		ONE_CHEAT,
		FORGE_MODS,
		LANG,
		REPORT;
	}
}
