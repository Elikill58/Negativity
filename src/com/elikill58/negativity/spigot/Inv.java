package com.elikill58.negativity.spigot;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Version;

@SuppressWarnings("deprecation")
public class Inv {

	public static final String NAME_CHECK_MENU = "Check", ADMIN_MENU = "Admin",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", CHEAT_MANAGER = "Cheat Manager", NAME_FORGE_MOD_MENU = "Mods";
	public static final HashMap<Player, Player> CHECKING = new HashMap<>();
	public static final ItemStack EMPTY;

	static {
		Material paneMaterial = Utils.getMaterialWith1_15_Compatibility("STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE");
		if (Version.isNewerOrEquals(Version.getVersion(), Version.V1_13)) {
			EMPTY = new ItemStack(paneMaterial);
		} else {
			EMPTY = new ItemStack(paneMaterial, 1, (byte) 7);
		}
		ItemMeta emptyItemMeta = EMPTY.getItemMeta();
		emptyItemMeta.setDisplayName(ChatColor.RESET.toString());
		EMPTY.setItemMeta(emptyItemMeta);
	}

	public static void openFreezeMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, NAME_FREEZE_MENU);
		inv.setItem(13, Utils.createItem(Material.PAPER, Messages.getMessage(p, "inventory.mod.you_are_freeze")));
		p.openInventory(inv);
	}
}
