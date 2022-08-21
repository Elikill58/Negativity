package com.elikill58.negativity.common.inventories.hook.players.offline;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.players.offline.CheckMenuOfflineHolder;
import com.elikill58.negativity.common.inventories.hook.players.GlobalPlayerInventory;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.warn.WarnManager;

public class GlobalPlayerOfflineInventory extends AbstractInventory<CheckMenuOfflineHolder> {
	
	public GlobalPlayerOfflineInventory() {
		super(NegativityInventory.GLOBAL_PLAYER_OFFLINE, CheckMenuOfflineHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		Inventory inv = Inventory.createInventory(Inventory.NAME_CHECK_MENU, 18, new CheckMenuOfflineHolder(cible));
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		NegativityAccount account = NegativityAccount.get(cible.getUniqueId());
		Minerate minerate = account.getMinerate();
		int betterClick = account.getMostClicksPerSecond();
		
		inv.set(0, GlobalPlayerInventory.getClickItem(Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(betterClick)), betterClick).build());
		
		inv.set(3, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())).build());
		inv.set(5, ItemBuilder.Builder(Materials.APPLE).displayName(Messages.getMessage(p, "inventory.main.active_report", "%name%", cible.getName())).build());
		
		inv.set(8, ItemBuilder.getSkullItem(cible, p));
		inv.set(9, ItemBuilder.Builder(Materials.DIAMOND_PICKAXE).displayName("Minerate").lore(minerate.getInventoryLoreString()).build());

		if(!BanManager.getSanctions().isEmpty() && Perm.hasPerm(p, Perm.BAN)) {
			inv.set(13, ItemBuilder.Builder(Materials.ANVIL).displayName("Ban").build());
		}
		if(!WarnManager.getSanctions().isEmpty() && Perm.hasPerm(p, Perm.WARN)) {
			inv.set(14, ItemBuilder.Builder(Materials.COMPASS).displayName("Warn").build());
		}

		inv.set(17, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, CheckMenuOfflineHolder nh) {
		OfflinePlayer cible = nh.getCible();
		if(m.equals(Materials.APPLE)) {
			InventoryManager.open(NegativityInventory.SEE_REPORT, p, cible);
		} else if(m.equals(Materials.PAPER)) {
			InventoryManager.open(NegativityInventory.ALERT_OFFLINE, p, cible);
		} else if(m.equals(Materials.ANVIL)) {
			InventoryManager.open(NegativityInventory.BAN, p, cible);
		}
	}
}
