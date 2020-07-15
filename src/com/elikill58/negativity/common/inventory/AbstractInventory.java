package com.elikill58.negativity.common.inventory;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.EventManager;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.common.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.inventories.ActivedCheatInventory;
import com.elikill58.negativity.inventories.AlertInventory;
import com.elikill58.negativity.inventories.CheckMenuInventory;
import com.elikill58.negativity.inventories.ForgeModsInventory;
import com.elikill58.negativity.inventories.FreezeInventory;
import com.elikill58.negativity.inventories.ModInventory;
import com.elikill58.negativity.inventories.admin.AdminInventory;
import com.elikill58.negativity.inventories.admin.CheatManagerInventory;
import com.elikill58.negativity.inventories.admin.LangInventory;
import com.elikill58.negativity.inventories.admin.OneCheatInventory;

public abstract class AbstractInventory implements Listeners {

	public static final List<AbstractInventory> INVENTORIES = new ArrayList<>();
	
	static {
		EventManager.registerEvent(new ActivedCheatInventory());
		EventManager.registerEvent(new AlertInventory());
		EventManager.registerEvent(new ModInventory());
		EventManager.registerEvent(new CheckMenuInventory());
		EventManager.registerEvent(new CheatManagerInventory());
		EventManager.registerEvent(new ForgeModsInventory());
		EventManager.registerEvent(new OneCheatInventory());
		EventManager.registerEvent(new AdminInventory());
		EventManager.registerEvent(new LangInventory());
		EventManager.registerEvent(new FreezeInventory());
	}
	
	private final NegativityInventory type;
	
	public AbstractInventory(NegativityInventory type) {
		this.type = type;
		INVENTORIES.add(this);
	}
	
	public NegativityInventory getType() {
		return type;
	}
	
	@EventListener
	public void onInventoryClick(InventoryClickEvent e) {
		InventoryHolder holder = e.getClickedInventory().getHolder();
		if(!(holder instanceof NegativityHolder))
			return;
		if(isInstance((NegativityHolder) holder)) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			Material m = e.getCurrentItem().getType();
			if (m.equals(Materials.BARRIER))
				p.closeInventory();
			else
				manageInventory(e, m, p, (NegativityHolder) holder);
		}
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
		LANG;
	}
}
