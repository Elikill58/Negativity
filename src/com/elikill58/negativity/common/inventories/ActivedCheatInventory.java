package com.elikill58.negativity.common.inventories;

import com.elikill58.negativity.api.ChatColor;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.ActivedCheatHolder;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ActivedCheatInventory extends AbstractInventory {

	public ActivedCheatInventory() {
		super(NegativityInventory.ACTIVED_CHEAT);
	}
	
	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ActivedCheatHolder;
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		Inventory inv = Inventory.createInventory(Inv.NAME_ACTIVED_CHEAT_MENU, UniversalUtils.getMultipleOf(np.ACTIVE_CHEAT.size() + 3, 9, 1, 54), new ActivedCheatHolder(cible));
		if (np.ACTIVE_CHEAT.size() > 0) {
			int slot = 0;
			for (Cheat c : np.ACTIVE_CHEAT)
				if(inv.getSize() > slot)
					inv.set(slot++, ItemBuilder.Builder(c.getMaterial()).displayName(ChatColor.RESET + c.getName()).build());
		} else
			inv.set(4, ItemBuilder.Builder(Materials.REDSTONE_BLOCK).displayName(Messages.getMessage(p, "inventory.detection.no_active", "%name%", cible.getName())).build());
		inv.set(inv.getSize() - 2, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if (m.equals(Materials.ARROW))
			InventoryManager.open(NegativityInventory.CHECK_MENU, p, ((ActivedCheatHolder) nh).getCible());
	}
}
