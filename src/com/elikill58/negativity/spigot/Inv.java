package com.elikill58.negativity.spigot;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Version;

@SuppressWarnings("deprecation")
public class Inv {

	public static final String NAME_CHECK_MENU = "Check",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", CHEAT_MANAGER = "Cheat Manager", NAME_FORGE_MOD_MENU = "Mods";
	public static final HashMap<Player, Player> CHECKING = new HashMap<>();
	public static final ItemStack EMPTY = (Version.isNewerOrEquals(Version.getVersion(), Version.V1_13) ? new ItemStack(Utils.getMaterialWith1_13_Compatibility("STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE"), 1, (byte) 7) : new ItemStack(Utils.getMaterialWith1_13_Compatibility("STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE")));

	public static void openFreezeMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, NAME_FREEZE_MENU);
		inv.setItem(13, Utils.createItem(Material.PAPER, Messages.getMessage(p, "inventory.mod.you_are_freeze")));
		p.openInventory(inv);
	}
}
