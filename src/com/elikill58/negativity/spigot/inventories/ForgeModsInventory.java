package com.elikill58.negativity.spigot.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.ForgeModsHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.Utils;

public class ForgeModsInventory extends AbstractInventory {

	public ForgeModsInventory() {
		super(InventoryType.FORGE_MODS);
	}
	
	public int slot = 0;
	
	@Override
	public void openInventory(Player mod, Object... args) {
		Player p = (Player) args[0];
		slot = 0;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		Inventory inv = Bukkit.createInventory(new ForgeModsHolder(), Utils.getMultipleOf(np.MODS.size() + 1, 9, 1, 54), Inv.NAME_FORGE_MOD_MENU);
		if(np.MODS.size() == 0) {
			inv.setItem(4, Utils.createItem(Material.DIAMOND, "No mods"));
			inv.setItem(inv.getSize() - 1, Utils.createItem(Material.ARROW, Messages.getMessage(mod, "inventory.back")));
		} else {
			np.MODS.forEach((name, version) -> {
				inv.setItem(slot++, Utils.createItem(Material.GRASS, name, ChatColor.GRAY + "Version: " + version));
			});
		}
		Utils.fillInventory(inv, Inv.EMPTY);
		mod.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if(m.equals(Material.ARROW))
			AbstractInventory.open(InventoryType.CHECK_MENU, p, Inv.CHECKING.get(p));
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ForgeModsHolder;
	}
}
