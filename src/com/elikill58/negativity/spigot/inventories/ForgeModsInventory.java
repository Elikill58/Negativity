package com.elikill58.negativity.spigot.inventories;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.inventories.holders.ForgeModsHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.InventoryUtils;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ForgeModsInventory extends AbstractInventory {

	public ForgeModsInventory() {
		super(InventoryType.FORGE_MODS);
	}
	
	public int slot = 0;
	
	@Override
	public void openInventory(Player mod, Object... args) {
		Player p = (Player) args[0];
		slot = 0;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		Inventory inv = Bukkit.createInventory(new ForgeModsHolder(), UniversalUtils.getMultipleOf(np.MODS.size() + 1, 9, 1, 54), Inv.NAME_FORGE_MOD_MENU);
		if(np.MODS.size() == 0) {
			inv.setItem(4, createItem(Material.DIAMOND, "No mods"));
			inv.setItem(inv.getSize() - 1, createItem(Material.ARROW, Messages.getMessage(mod, "inventory.back")));
		} else {
			np.MODS.forEach((name, version) -> {
				inv.setItem(slot++, createItem(Material.GRASS, name, ChatColor.GRAY + "Version: " + version));
			});
		}
		InventoryUtils.fillInventory(inv, Inv.EMPTY);
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
