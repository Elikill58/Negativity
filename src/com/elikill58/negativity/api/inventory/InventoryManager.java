package com.elikill58.negativity.api.inventory;

import java.util.Optional;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.ActivedCheatInventory;
import com.elikill58.negativity.common.inventories.AlertInventory;
import com.elikill58.negativity.common.inventories.BanInventory;
import com.elikill58.negativity.common.inventories.CheckMenuInventory;
import com.elikill58.negativity.common.inventories.ForgeModsInventory;
import com.elikill58.negativity.common.inventories.FreezeInventory;
import com.elikill58.negativity.common.inventories.ModInventory;
import com.elikill58.negativity.common.inventories.ReportInventory;
import com.elikill58.negativity.common.inventories.admin.AdminInventory;
import com.elikill58.negativity.common.inventories.admin.CheatManagerInventory;
import com.elikill58.negativity.common.inventories.admin.LangInventory;
import com.elikill58.negativity.common.inventories.admin.OneCheatInventory;

public class InventoryManager implements Listeners {
	
	public InventoryManager() {
		new ActivedCheatInventory();
		new AdminInventory();
		new AlertInventory();
		new BanInventory();
		new CheckMenuInventory();
		new CheatManagerInventory();
		new ForgeModsInventory();
		new FreezeInventory();
		new LangInventory();
		new ModInventory();
		new OneCheatInventory();
		new ReportInventory();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@EventListener
	public void onInventoryClick(InventoryClickEvent e) {
		PlatformHolder holder = e.getClickedInventory().getHolder();
		if(!(holder instanceof NegativityHolder)) 
			return;
		NegativityHolder nh = ((NegativityHolder) holder).getBasicHolder();
		for(AbstractInventory inv : AbstractInventory.INVENTORIES) {
			if(inv.isInstance(nh)) {
				e.setCancelled(true);
				Player p = e.getPlayer();
				Material m = e.getCurrentItem().getType();
				if (m.equals(Materials.BARRIER)) {
					p.closeInventory();
				} else {
					inv.manageInventory(e, m, p, nh);
				}
				return;
			}
		}
	}
	
	public static Optional<AbstractInventory<?>> getInventory(NegativityInventory type) {
		for(AbstractInventory<?> inv : AbstractInventory.INVENTORIES)
			if(inv.getType().equals(type))
				return Optional.of(inv);
		return Optional.empty();
	}
	
	/**
	 * Open the negativity inventory of the given type
	 * Does nothing if the inventory is not found
	 * 
	 * @param type the type of the ivnetnory which have to be showed
	 * @param p the player that have to see the inventory
	 * @param args the arguments to open the inventory
	 */
	public static void open(NegativityInventory type, Player p, Object... args) {
		getInventory(type).ifPresent((inv) -> inv.openInventory(p, args));
	}
}
