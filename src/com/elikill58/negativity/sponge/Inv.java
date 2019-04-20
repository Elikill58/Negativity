package com.elikill58.negativity.sponge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.Identifiable;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class Inv {

	public static final String NAME_CHECK_MENU = "Check",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", NAME_FORGE_MOD_MENU = "Mods";
	public static final ItemStack VIDE_ITEM = Utils.createItem(ItemTypes.STAINED_GLASS_PANE, "", 1, DyeColors.GRAY);;
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

	public static void openCheckMenu(Player p, Player cible) {
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_CHECK_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.property(INV_ID_KEY, CHECK_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(VIDE_ITEM, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		invGrid.set(0, 0, Utils
				.createItem(ItemTypes.STAINED_HARDENED_CLAY,
						Messages.getStringMessage(p, "inventory.main.actual_click",
								"%clicks%", String.valueOf(np.ACTUAL_CLICK)),
						1, getByteFromClick(np.ACTUAL_CLICK)));
		invGrid.set(1, 0, Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY,
				Messages.getStringMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)),
				1, getByteFromClick(np.BETTER_CLICK)));
		invGrid.set(2, 0, Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY,
				Messages.getStringMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1,
				getByteFromClick(np.LAST_CLICK)));
		invGrid.set(7, 0, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p,
				"inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		invGrid.set(8, 0, Utils.createSkull(cible.getName(), 1, cible, "&6UUID: " + cible.getUniqueId()));

		invGrid.set(0, 1, Utils.createItem(ItemTypes.DIAMOND_SWORD, "&rFight: "
				+ Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		invGrid.set(1, 1, Utils.createItem(ItemTypes.DIAMOND_PICKAXE, "&rMinerate", np.mineRate.getInventoryLoreString()));
		invGrid.set(2, 1, Utils.createItem(ItemTypes.GRASS, "&rMods", "&7Forge: "
				+ Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));

		invGrid.set(0, 2, Utils.createItem(ItemTypes.SPIDER_EYE,
				Messages.getStringMessage(p, "inventory.main.see_inv", "%name%", cible.getName())));
		invGrid.set(1, 2, Utils.createItem(ItemTypes.ENDER_EYE,
				Messages.getStringMessage(p, "inventory.main.teleportation_to", "%name%", cible.getName())));
		invGrid.set(2, 2, Utils.createItem(ItemTypes.PACKED_ICE,
				Messages.getStringMessage(p, "inventory.main.freezing", "%name%", cible.getName())));
		invGrid.set(3, 2, Utils.createItem(ItemTypes.ANVIL,
				Messages.getStringMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())));
		invGrid.set(4, 2, Utils.createItem(ItemTypes.TNT,
				Messages.getStringMessage(p, "inventory.main.active_detection", "%name%", cible.getName())));
		invGrid.set(8, 2, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
		CHECKING.put(p, cible);
	}

	public static void actualizeCheckMenu(Player p, Player cible) {
		Inventory inv = p.getOpenInventory().get();
		GridInventory invGrid = inv.first().query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);

		invGrid.set(0, 0, Utils
				.createItem(
						ItemTypes.STAINED_HARDENED_CLAY, Messages.getStringMessage(p,
								"inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)),
						1, getByteFromClick(np.ACTUAL_CLICK)));
		invGrid.set(1, 0, Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY,
				Messages.getStringMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)),
				1, getByteFromClick(np.BETTER_CLICK)));
		invGrid.set(2, 0, Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY,
				Messages.getStringMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1,
				getByteFromClick(np.LAST_CLICK)));
		invGrid.set(7, 0, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p,
				"inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		invGrid.set(0, 1, Utils.createItem(ItemTypes.DIAMOND_SWORD, "&rFight: "
				+ Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
	}

	public static void openAlertMenu(Player p, Player cible) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values())
			if ((Adapter.getAdapter().getBooleanInConfig("inventory.alerts.only_cheat_active")
					&& np.hasDetectionActive(c))
					|| (!np.hasDetectionActive(c)
							&& Adapter.getAdapter().getBooleanInConfig("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		int size = Utils.getMultipleOf(TO_SEE.size() + 4, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_ALERT_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(INV_ID_KEY, ALERT_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(VIDE_ITEM, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		int x = 0, y = 0;
		for (Cheat c : TO_SEE) {
			invGrid.set(x, y, Utils.createItem((ItemType)
							c.getMaterial(), Messages.getStringMessage(p, "inventory.alerts.item_name", "%exact_name%",
					c.getName(), "%warn%", String.valueOf(np.getWarn(c))),
					np.getWarn(c) < 1 ? 1 : np.getWarn(c)));
			x++;
			if (x > 8) {
				x = 0;
				y++;
			}
		}
		invGrid.set(6, y, Utils.createItem(ItemTypes.BONE, "&7Clear"));
		invGrid.set(7, y, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		invGrid.set(8, y, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void actualizeAlertMenu(Player p, Player cible) {
		Inventory inv = p.getOpenInventory().get();
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values())
			if ((c.isActive() && Adapter.getAdapter().getBooleanInConfig("inventory.alerts.see.only_cheat_active")
					&& np.hasDetectionActive(c))
					|| (!np.hasDetectionActive(c)
							&& Adapter.getAdapter().getBooleanInConfig("inventory.alerts.see.no_started_verif_cheat")))
				TO_SEE.add(c);
		for (Cheat c : TO_SEE)
			if (c.getMaterial() != null)
				inv.offer(Utils.createItem(
						(ItemType) c.getMaterial(), Messages.getStringMessage(p, "inventory.alerts.item_name",
								"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))),
						np.getWarn(c) == 0 ? 1 : np.getWarn(c)));
	}

	private static DyeColor getByteFromClick(int click) {
		if (click > 25)
			return DyeColors.RED;
		else if (click < 25 && click > 15)
			return DyeColors.GRAY;
		else
			return DyeColors.LIME;
	}

	public static void openActivedCheat(Player p, Player cible) {
		int size = Utils.getMultipleOf(Cheat.values().size() + 3, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_ACTIVED_CHEAT_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(INV_ID_KEY, ACTIVE_CHEAT_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(VIDE_ITEM, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		int x = 0, y = 0;
		if (np.getActiveCheat().size() > 0) {
			for (Cheat c : np.getActiveCheat()) {
				if (c.equals(Cheat.ALL))
					continue;
				invGrid.set(x, y, Utils.createItem((ItemType) c.getMaterial(), "&r" + c.getName()));
				x++;
				if (x > 8) {
					x = 0;
					y++;
				}
			}
		} else
			invGrid.set(5, 1, Utils.createItem(ItemTypes.REDSTONE_BLOCK,
					Messages.getStringMessage(p, "inventory.detection.no_active", "%name%", cible.getName())));
		invGrid.set(7, y, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		invGrid.set(8, y, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void openFreezeMenu(Player p) {
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_FREEZE_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.property(INV_ID_KEY, FREEZE_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(4, 1, Utils.createItem(ItemTypes.PAPER, Messages.getStringMessage(p, "inventory.mod.you_are_freeze")));
		p.openInventory(inv);
	}

	public static void openModMenu(Player p) {
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_MOD_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.property(INV_ID_KEY, MOD_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(VIDE_ITEM, inv);

		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(1, 1, Utils.createItem(ItemTypes.GHAST_TEAR, Messages.getStringMessage(p, "inventory.mod.night_vision")));
		invGrid.set(2, 1, Utils.createItem(ItemTypes.PUMPKIN_PIE, Messages.getStringMessage(p, "inventory.mod.invisible")));
		invGrid.set(3, 1, Utils.createItem(ItemTypes.FEATHER, "&rFly: " + Messages.getStringMessage(p,
				"inventory.manager." + (p.get(Keys.CAN_FLY).get() ? "enabled" : "disabled"))));
		if (Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "manageCheat"))
			invGrid.set(4, 1, Utils.createItem(ItemTypes.TNT, Messages.getStringMessage(p, "inventory.mod.cheat_manage")));
		invGrid.set(6, 1, Utils.createItem(ItemTypes.LEAD, Messages.getStringMessage(p, "inventory.mod.random_tp")));
		invGrid.set(7, 1, Utils.createItem(ItemTypes.IRON_SHOVEL, Messages.getStringMessage(p, "inventory.mod.clear_inv")));
		invGrid.set(8, 2, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void openCheatManagerMenu(Player p) {
		int size = Utils.getMultipleOf(Cheat.values().size() + 3, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of("Cheat manager")))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(INV_ID_KEY, CHEAT_MANAGER_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(VIDE_ITEM, inv);
		for (Cheat c : Cheat.values())
			if (c.getMaterial() != null)
				inv.offer(Utils.createItem((ItemType) c.getMaterial(), c.getName()));
		Iterator<Inventory> slots = inv.slots().iterator();
        Iterator<Cheat> cheats = Cheat.values().iterator();
        while (slots.hasNext() && cheats.hasNext()) {
            Cheat cheat = cheats.next();
            if(cheat.equals(Cheat.ALL))
            	continue;
            slots.next().set(Utils.createItem((ItemType) cheat.getMaterial(), cheat.getName()));
        }
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(7, nbLine - 1, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		invGrid.set(8, nbLine - 1, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void openOneCheatMenu(Player p, Cheat c) {
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(c.getName())))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.property(INV_ID_KEY, ONE_CHEAT_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(VIDE_ITEM, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(2, 0, Utils.createItem(ItemTypes.TNT, Messages.getStringMessage(p,
				"inventory.manager.setBack", "%back%",
				Messages.getStringMessage(p, "inventory.manager." + (c.isSetBack() ? "enabled" : "disabled")))));
		invGrid.set(5, 0, Utils.createItem(ItemTypes.ENDER_EYE, Messages.getStringMessage(p,
				"inventory.manager.autoVerif", "%auto%",
				Messages.getStringMessage(p, "inventory.manager." + (c.isAutoVerif() ? "enabled" : "disabled")))));
		invGrid.set(0, 1, Utils.createItem((ItemType) c.getMaterial(), c.getName()));
		invGrid.set(2, 2, Utils.createItem(ItemTypes.BLAZE_ROD, Messages.getStringMessage(p,
				"inventory.manager.allowKick", "%allow%",
				Messages.getStringMessage(p, "inventory.manager." + (c.allowKick() ? "enabled" : "disabled")))));
		invGrid.set(5, 2, Utils.createItem(ItemTypes.DIAMOND, Messages.getStringMessage(p, "inventory.manager.setActive",
				"%active%", Messages.getStringMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")))));

		invGrid.set(8, 0, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		invGrid.set(8, 2, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void openForgeModsMenu(Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		int size = Utils.getMultipleOf(np.MODS.size() + 1, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_FORGE_MOD_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(INV_ID_KEY, FORGE_MODS_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(VIDE_ITEM, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		if (np.MODS.size() == 0) {
			invGrid.set(4, 0, Utils.createItem(ItemTypes.DIAMOND, "&rNo mods"));
		} else {
			int x = 0, y = 0;
			for (Entry<String, String> entry : np.MODS.entrySet()) {
				invGrid.set(x, y, Utils.createItem(ItemTypes.GRASS, "&r" + entry.getKey(), "&7Version: " + entry.getValue()));
				x++;
				if (x > 8) {
					x = 0;
					y++;
				}
			}
		}
		invGrid.set(8, nbLine--, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}
}
