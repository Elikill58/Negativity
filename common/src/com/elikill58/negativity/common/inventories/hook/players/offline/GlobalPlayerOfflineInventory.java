package com.elikill58.negativity.common.inventories.hook.players.offline;

import static com.elikill58.negativity.universal.Messages.getMessage;
import static com.elikill58.negativity.common.inventories.hook.players.GlobalPlayerInventory.getClickItem;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.players.offline.CheckMenuOfflineHolder;
import com.elikill58.negativity.common.inventories.hook.players.GlobalPlayerInventory;
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
		Inventory inv = Inventory.createInventory(Inventory.NAME_CHECK_MENU, 27, new CheckMenuOfflineHolder(cible));
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		NegativityAccount account = NegativityAccount.get(cible.getUniqueId());
		Minerate minerate = account.getMinerate();
		/*int betterClick = account.getMostClicksPerSecond();
		
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
		p.openInventory(inv);*/

		inv.set(0, ItemBuilder.getSkullItem(cible, p));

		List<ItemStack> sanctionItems = new ArrayList<>();
		if(!BanManager.getSanctions().isEmpty() && Perm.hasPerm(p, Perm.BAN) && BanManager.banActive)
			sanctionItems.add(ItemBuilder.Builder(Materials.ANVIL).displayName("Ban").build());
		if(!WarnManager.getSanctions().isEmpty() && Perm.hasPerm(p, Perm.WARN) && WarnManager.warnActive)
			sanctionItems.add(ItemBuilder.Builder(Materials.COMPASS).displayName("Warn").build());

        GlobalPlayerInventory.sanctionSize1(p, inv, sanctionItems);

        inv.set(9, ItemBuilder.Builder(Materials.DIAMOND_PICKAXE).displayName("Minerate").lore(minerate.getInventoryLoreString()).build());
		Object[] clickPlaceholders = new Object[] {"%last_clicks%", 0, "%max_clicks%", account.getMostClicksPerSecond()};
		inv.set(10, getClickItem(getMessage(p, "inventory.main.clicks.name", clickPlaceholders), account.getMostClicksPerSecond()).lore(getMessage(p, "inventory.main.clicks.lore", clickPlaceholders)).build());
		inv.set(11, ItemBuilder.Builder(Materials.BONE).displayName(ChatColor.GRAY + "Clear clicks").build());

		inv.set(16, ItemBuilder.Builder(Materials.BOOK).displayName(getMessage(p, "inventory.main.see_proofs", "%name%", cible.getName())).build());
		inv.set(17, ItemBuilder.Builder(Materials.PAPER).displayName(getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())).build());
		
		inv.set(25, ItemBuilder.Builder(Materials.APPLE).displayName(getMessage(p, "inventory.main.active_report", "%name%", cible.getName())).build());
		inv.set(26, ItemBuilder.Builder(Materials.BEACON).displayName(getMessage(p, "inventory.main.active_warn", "%name%", cible.getName())).build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, CheckMenuOfflineHolder nh) {
		OfflinePlayer cible = nh.getCible();
		if(m.equals(Materials.APPLE)) {
			InventoryManager.open(NegativityInventory.SEE_REPORT, p, cible);
		} else if(m.equals(Materials.BEACON)) {
			InventoryManager.open(NegativityInventory.WARN_SEE, p, cible);
		} else if(m.equals(Materials.BOOK)) {
			InventoryManager.open(NegativityInventory.SEE_PROOF, p, cible);
		} else if(m.equals(Materials.PAPER)) {
			InventoryManager.open(NegativityInventory.ALERT_OFFLINE, p, cible);
		} else if(m.equals(Materials.ANVIL)) {
			InventoryManager.open(NegativityInventory.BAN, p, cible);
		}
	}
}
