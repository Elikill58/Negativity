package com.elikill58.negativity.spigot.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.ActivedCheatHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class ActivedCheatInventory extends AbstractInventory {

	public ActivedCheatInventory() {
		super(InventoryType.ACTIVED_CHEAT);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		Inventory inv = Bukkit.createInventory(new ActivedCheatHolder(), Utils.getMultipleOf(np.ACTIVE_CHEAT.size() + 3, 9, 1, 54), Inv.NAME_ACTIVED_CHEAT_MENU);
		if (np.ACTIVE_CHEAT.size() > 0) {
			int slot = 0;
			for (Cheat c : np.ACTIVE_CHEAT)
				if(inv.getSize() > slot)
					inv.setItem(slot++, Utils.hideAttributes(Utils.createItem((Material) c.getMaterial(), ChatColor.RESET + c.getName())));
		} else
			inv.setItem(4, Utils.createItem(Material.REDSTONE_BLOCK, Messages.getMessage(p, "inventory.detection.no_active", "%name%", cible.getName())));
		inv.setItem(inv.getSize() - 2, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1,
				Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if (m.equals(Material.ARROW))
			AbstractInventory.open(InventoryType.CHECK_MENU, p, Inv.CHECKING.get(p));
	}
	
	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ActivedCheatHolder;
	}
}
