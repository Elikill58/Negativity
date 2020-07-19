package com.elikill58.negativity.spigot;

import com.elikill58.negativity.api.ChatColor;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Version;

public class Inv {

	public static final String NAME_CHECK_MENU = "Check", ADMIN_MENU = "Admin",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", CHEAT_MANAGER = "Cheat Manager", NAME_FORGE_MOD_MENU = "Mods";
	public static final ItemStack EMPTY;

	static {
		ItemBuilder builder = ItemBuilder.Builder(Materials.GRAY_STAINED_GLASS_PANE);
		if (!Version.isNewerOrEquals(Version.getVersion(), Version.V1_13)) {
			builder.durability((byte) 7);
		}
		builder.displayName(ChatColor.RESET.toString() + " - ");
		EMPTY = builder.build();
	}
}
