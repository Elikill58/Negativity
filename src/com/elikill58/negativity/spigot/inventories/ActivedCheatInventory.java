package com.elikill58.negativity.spigot.inventories;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.ActivedCheatHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ActivedCheatInventory extends AbstractInventory {

	public ActivedCheatInventory() {
		super(InventoryType.ACTIVED_CHEAT);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		Inventory inv = Bukkit.createInventory(new ActivedCheatHolder(cible), UniversalUtils.getMultipleOf(np.ACTIVE_CHEAT.size() + 3, 9, 1, 54), Inv.NAME_ACTIVED_CHEAT_MENU);
		if (np.ACTIVE_CHEAT.size() > 0) {
			int slot = 0;
			for (Cheat c : np.ACTIVE_CHEAT) {
				if(inv.getSize() > slot) {
					String lore = np.hasDetectionActive(c) ? ChatColor.GREEN + "You can be detected." : ChatColor.RED + "Cannot be detected.\n&7Reason: &c" + np.getWhyDetectionNotActive(c);
					inv.setItem(slot++, ItemUtils.hideAttributes(createItem((Material) c.getMaterial(), ChatColor.RESET + c.getName(), lore.split("\n"))));
				}
			}
		} else
			inv.setItem(4, createItem(Material.REDSTONE_BLOCK, Messages.getMessage(p, "inventory.detection.no_active", "%name%", cible.getName())));
		inv.setItem(inv.getSize() - 2, createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1, createItem(ItemUtils.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if (m.equals(Material.ARROW))
			AbstractInventory.open(InventoryType.CHECK_MENU, p, ((ActivedCheatHolder) nh).getCible());
	}
	
	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ActivedCheatHolder;
	}
}
