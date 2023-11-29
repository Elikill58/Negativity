package com.elikill58.negativity.api.inventory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.hook.ReportInventory;
import com.elikill58.negativity.common.inventories.hook.admin.AdminAlertInventory;
import com.elikill58.negativity.common.inventories.hook.admin.AdminInventory;
import com.elikill58.negativity.common.inventories.hook.admin.LangInventory;
import com.elikill58.negativity.common.inventories.hook.admin.ban.BanManagerInventory;
import com.elikill58.negativity.common.inventories.hook.admin.ban.BanProcessorManagerInventory;
import com.elikill58.negativity.common.inventories.hook.admin.detections.CheatChecksInventory;
import com.elikill58.negativity.common.inventories.hook.admin.detections.CheatDescriptionInventory;
import com.elikill58.negativity.common.inventories.hook.admin.detections.CheatManagerInventory;
import com.elikill58.negativity.common.inventories.hook.admin.detections.OneCheatInventory;
import com.elikill58.negativity.common.inventories.hook.admin.detections.OneSpecialInventory;
import com.elikill58.negativity.common.inventories.hook.admin.detections.SpecialManagerInventory;
import com.elikill58.negativity.common.inventories.hook.admin.warn.WarnManagerInventory;
import com.elikill58.negativity.common.inventories.hook.admin.warn.WarnProcessorManagerInventory;
import com.elikill58.negativity.common.inventories.hook.mod.FreezeInventory;
import com.elikill58.negativity.common.inventories.hook.mod.ModInventory;
import com.elikill58.negativity.common.inventories.hook.players.ActivedCheatInventory;
import com.elikill58.negativity.common.inventories.hook.players.AlertInventory;
import com.elikill58.negativity.common.inventories.hook.players.BanInventory;
import com.elikill58.negativity.common.inventories.hook.players.ForgeModsInventory;
import com.elikill58.negativity.common.inventories.hook.players.GlobalPlayerInventory;
import com.elikill58.negativity.common.inventories.hook.players.KickInventory;
import com.elikill58.negativity.common.inventories.hook.players.SeeProofInventory;
import com.elikill58.negativity.common.inventories.hook.players.SeeReportInventory;
import com.elikill58.negativity.common.inventories.hook.players.SeeWarnInventory;
import com.elikill58.negativity.common.inventories.hook.players.WarnInventory;
import com.elikill58.negativity.common.inventories.hook.players.offline.AlertOfflineInventory;
import com.elikill58.negativity.common.inventories.hook.players.offline.GlobalPlayerOfflineInventory;
import com.elikill58.negativity.universal.Adapter;

public class InventoryManager implements Listeners {
	
	public InventoryManager() {
		new ActivedCheatInventory();
		new AdminInventory();
		new AdminAlertInventory();
		new AlertInventory();
		new AlertOfflineInventory();
		new BanInventory();
		new BanManagerInventory();
		new BanProcessorManagerInventory();
		new GlobalPlayerInventory();
		new GlobalPlayerOfflineInventory();
		new CheatManagerInventory();
		new CheatDescriptionInventory();
		new SpecialManagerInventory();
		new ForgeModsInventory();
		new FreezeInventory();
		new LangInventory();
		new ModInventory();
		new OneCheatInventory();
		new CheatChecksInventory();
		new OneSpecialInventory();
		new SeeReportInventory();
		new ReportInventory();
		new KickInventory();
		new WarnInventory();
		new WarnManagerInventory();
		new WarnProcessorManagerInventory();
		new SeeWarnInventory();
		new SeeProofInventory();
		AbstractInventory.getInventories().forEach(AbstractInventory::load);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@EventListener
	public void onInventoryClick(InventoryClickEvent e) {
		PlatformHolder holder = e.getClickedInventory().getHolder();
		if(!(holder instanceof NegativityHolder)) 
			return;
		NegativityHolder nh = ((NegativityHolder) holder).getBasicHolder();
		for(AbstractInventory inv : AbstractInventory.getInventories()) {
			if(inv.isInstance(nh)) {
				e.setCancelled(true);
				Player p = e.getPlayer();
				Material m = e.getCurrentItem().getType();
				if (m.equals(Materials.BARRIER)) {
					p.closeInventory();
				} else {
					CompletableFuture.runAsync(() -> inv.manageInventory(e, m, p, nh)); // do all inventory things async
				}
				return;
			}
		}
	}
	
	public static Optional<AbstractInventory<?>> getInventory(NegativityInventory type) {
		for(AbstractInventory<?> inv : AbstractInventory.getInventories())
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
		CompletableFuture.runAsync(() -> getInventory(type).ifPresent((inv) -> {
			try {
				inv.openInventory(p, args);
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().printError("Failed to open inventory " + type + " for " + p.getName(), e);
			}
		}));
	}
}
