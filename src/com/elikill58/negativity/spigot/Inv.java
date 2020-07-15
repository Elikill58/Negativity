package com.elikill58.negativity.spigot;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Version;

@SuppressWarnings("deprecation")
public class Inv {

	public static final String NAME_CHECK_MENU = "Check", ADMIN_MENU = "Admin",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", CHEAT_MANAGER = "Cheat Manager", NAME_FORGE_MOD_MENU = "Mods";
	public static final HashMap<Player, Player> CHECKING = new HashMap<>();
	public static final ItemStack EMPTY;

	static {
		if (Version.isNewerOrEquals(Version.getVersion(), Version.V1_13)) {
			EMPTY = new ItemStack(ItemUtils.GRAY_STAINED_GLASS_PANE);
		} else {
			EMPTY = new ItemStack(ItemUtils.GRAY_STAINED_GLASS_PANE, 1, (byte) 7);
		}
		ItemMeta emptyItemMeta = EMPTY.getItemMeta();
		emptyItemMeta.setDisplayName(ChatColor.RESET.toString() + " - ");
		EMPTY.setItemMeta(emptyItemMeta);
	}
}
