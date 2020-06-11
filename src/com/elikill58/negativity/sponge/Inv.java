package com.elikill58.negativity.sponge;

import java.util.HashMap;
import java.util.UUID;

import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.Identifiable;

import com.elikill58.negativity.sponge.utils.ItemUtils;

public class Inv {

	public static final String NAME_CHECK_MENU = "Check", NAME_ADMIN_MENU = "Admin", NAME_LANG_MENU = "Lang",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", NAME_FORGE_MOD_MENU = "Mods";
	public static final ItemStack EMPTY = ItemUtils.createItem(ItemTypes.STAINED_GLASS_PANE, "-", 1, DyeColors.GRAY);;
	public static final HashMap<Player, Player> CHECKING = new HashMap<>();

	public static final String INV_ID_KEY = "negativity_inv";
	public static final Identifiable CHECK_INV_ID = new Identifiable(UUID.fromString("aac9b596-8431-4d0b-87f5-b6623abfae00"));
	public static final Identifiable ALERT_INV_ID = new Identifiable(UUID.fromString("76085a1b-e78c-4622-a15e-a784bfed1adb"));
	public static final Identifiable ACTIVE_CHEAT_INV_ID = new Identifiable(UUID.fromString("50fc079c-8ffa-4f5f-8311-584f5d4a9710"));
	public static final Identifiable FREEZE_INV_ID = new Identifiable(UUID.fromString("5aa52d98-388a-43cb-b5a0-f3cedf2ad963"));
	public static final Identifiable MOD_INV_ID = new Identifiable(UUID.fromString("ce6c13aa-38e5-4eef-ad75-0517b4ec475b"));
	public static final Identifiable CHEAT_MANAGER_INV_ID = new Identifiable(UUID.fromString("7f361be4-6cbe-448d-b3bd-b61eaf577fa4"));
	public static final Identifiable ONE_CHEAT_INV_ID = new Identifiable(UUID.fromString("fc82df15-68f4-4a6f-ac5b-1bf38d662eca"));
	public static final Identifiable FORGE_MODS_INV_ID = new Identifiable(UUID.fromString("258670f4-d66c-4ad4-8fc6-4f3914026d54"));
	public static final Identifiable ADMIN_INV_ID = new Identifiable(UUID.fromString("1f4bd048-2e4f-4143-8703-2ce8d46f19d0"));
	public static final Identifiable LANG_INV_ID = new Identifiable(UUID.fromString("68f4d048-43cb-a15e-8a1b-2ce8d4a1baf5"));
}
