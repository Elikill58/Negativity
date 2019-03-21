package com.elikill58.negativity.sponge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.utils.Cheat;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

@SuppressWarnings("deprecation")
public class Inv {

	public static final String NAME_CHECK_MENU = "Check",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", NAME_FORGE_MOD_MENU = "Mods";
	public static final ItemStack VIDE_ITEM = Utils.createItem(ItemTypes.STAINED_GLASS_PANE, "", 1, DyeColors.GRAY);;
	public static final HashMap<Player, Player> CHECKING = new HashMap<>();

	public static void openCheckMenu(Player p, Player cible) {
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_CHECK_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.build(SpongeNegativity.INSTANCE);
		inv = Utils.rempliInvWith(VIDE_ITEM, inv);
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		inv.query(new SlotPos(0, 0)).set(Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY, Messages.getStringMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), 1, getByteFromClick(np.ACTUAL_CLICK)));
		inv.query(new SlotPos(1, 0)).set(Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY, Messages.getStringMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)), 1, getByteFromClick(np.BETTER_CLICK)));
		inv.query(new SlotPos(2, 0)).set(Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY, Messages.getStringMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1, getByteFromClick(np.LAST_CLICK)));
		inv.query(new SlotPos(7, 0)).set(Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		inv.query(new SlotPos(8, 0)).set(Utils.createSkull(cible.getName(), 1, cible, "&6UUID: " + cible.getUniqueId()));
		
		inv.query(new SlotPos(0, 1)).set(Utils.createItem(ItemTypes.DIAMOND_SWORD, "&rFight: " + Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		inv.query(new SlotPos(1, 1)).set(Utils.createItem(ItemTypes.DIAMOND_PICKAXE, "&rMinerate", np.mineRate.getInventoryLoreString()));
		inv.query(new SlotPos(2, 1)).set(Utils.createItem(ItemTypes.GRASS, "&rMods", "&7Forge: " + Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		
		inv.query(new SlotPos(0, 2)).set(Utils.createItem(ItemTypes.SPIDER_EYE, Messages.getStringMessage(p, "inventory.main.see_inv", "%name%", cible.getName())));
		inv.query(new SlotPos(1, 2)).set(Utils.createItem(ItemTypes.ENDER_EYE, Messages.getStringMessage(p, "inventory.main.teleportation_to", "%name%", cible.getName())));
		inv.query(new SlotPos(2, 2)).set(Utils.createItem(ItemTypes.PACKED_ICE, Messages.getStringMessage(p, "inventory.main.freezing", "%name%", cible.getName())));
		inv.query(new SlotPos(3, 2)).set(Utils.createItem(ItemTypes.ANVIL, Messages.getStringMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())));
		inv.query(new SlotPos(4, 2)).set(Utils.createItem(ItemTypes.TNT, Messages.getStringMessage(p, "inventory.main.active_detection", "%name%", cible.getName())));
		inv.query(new SlotPos(8, 2)).set(Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
		CHECKING.put(p, cible);
	}

	public static void actualizeCheckMenu(Player p, Player cible) {
		Inventory inv = p.getOpenInventory().get();
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);

		inv.query(new SlotPos(0, 0)).set(Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY, Messages.getStringMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), 1, getByteFromClick(np.ACTUAL_CLICK)));
		inv.query(new SlotPos(1, 0)).set(Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY, Messages.getStringMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)), 1, getByteFromClick(np.BETTER_CLICK)));
		inv.query(new SlotPos(2, 0)).set(Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY, Messages.getStringMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1, getByteFromClick(np.LAST_CLICK)));
		inv.query(new SlotPos(7, 0)).set(Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		inv.query(new SlotPos(0, 1)).set(Utils.createItem(ItemTypes.DIAMOND_SWORD, "&rFight: " + Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
	}

	public static void openAlertMenu(Player p, Player cible) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values())
			if((Adapter.getAdapter().getBooleanInConfig("inventory.alerts.only_cheat_active") && np.hasDetectionActive(c))
					|| (!np.hasDetectionActive(c) && Adapter.getAdapter().getBooleanInConfig("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		int size = Utils.getMultipleOf(TO_SEE.size() + 4, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_ALERT_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.build(SpongeNegativity.INSTANCE);
		inv = Utils.rempliInvWith(VIDE_ITEM, inv);
		int x = 0, y = 0;
		for (Cheat c : TO_SEE) {
			if (c.getProtocolClass() != null) {
				inv.query(new SlotPos(x, y))
						.set(Utils.createItem(c.getMaterial(),
								Messages.getStringMessage(p, "inventory.alerts.item_name", "%exact_name%", c.getName(),
										"%warn%", String.valueOf(np.getWarn(c))),
								np.getWarn(c) < 1 ? 1 : np.getWarn(c)));
				x++;
				if (x > 8) {
					x = 0;
					y++;
				}
			}
		}
		inv.query(new SlotPos(6, y)).set(Utils.createItem(ItemTypes.BONE, "&7Clear"));
		inv.query(new SlotPos(7, y))
				.set(Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		inv.query(new SlotPos(8, y))
				.set(Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void actualizeAlertMenu(Player p, Player cible) {
		Inventory inv = p.getOpenInventory().get();
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values())
			if ((c.isActive() && Adapter.getAdapter().getBooleanInConfig("inventory.alerts.see.only_cheat_active")
					&& np.hasDetectionActive(c))
					|| (!np.hasDetectionActive(c) && Adapter.getAdapter().getBooleanInConfig("inventory.alerts.see.no_started_verif_cheat")))
				TO_SEE.add(c);
		for (Cheat c : TO_SEE)
			if (c.getMaterial() != null && c.getProtocolClass() != null)
				inv.offer(Utils.createItem(
						c.getMaterial(), Messages.getStringMessage(p, "inventory.alerts.item_name", "%exact_name%",
								c.getName(), "%warn%", String.valueOf(np.getWarn(c))),
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
		int size = Utils.getMultipleOf(Cheat.values().length + 3, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_ACTIVED_CHEAT_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.build(SpongeNegativity.INSTANCE);
		Utils.rempliInvWith(VIDE_ITEM, inv);
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		int x = 0, y = 0;
		if (np.getActiveCheat().size() > 0) {
			for (Cheat c : np.getActiveCheat()) {
				if (c.equals(Cheat.ALL))
					continue;
				inv.query(new SlotPos(x, y)).set(Utils.createItem(c.getMaterial(), "&r" + c.getName()));
				x++;
				if (x > 8) {
					x = 0;
					y++;
				}
			}
		} else
			inv.query(new SlotPos(5, 1)).set(Utils.createItem(ItemTypes.REDSTONE_BLOCK,
					Messages.getStringMessage(p, "inventory.detection.no_active", "%name%", cible.getName())));
		inv.query(new SlotPos(7, y))
				.set(Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		inv.query(new SlotPos(8, y))
				.set(Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void openFreezeMenu(Player p) {
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_FREEZE_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.build(SpongeNegativity.INSTANCE);
		inv.query(new SlotPos(5, 1))
				.set(Utils.createItem(ItemTypes.PAPER, Messages.getStringMessage(p, "inventory.mod.you_are_freeze")));
		p.openInventory(inv);
	}

	public static void openModMenu(Player p) {
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_MOD_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.build(SpongeNegativity.INSTANCE);
		inv = Utils.rempliInvWith(VIDE_ITEM, inv);
		inv.query(new SlotPos(1, 1)).set(Utils.createItem(ItemTypes.GHAST_TEAR, Messages.getStringMessage(p, "inventory.mod.night_vision")));
		inv.query(new SlotPos(2, 1)).set(Utils.createItem(ItemTypes.PUMPKIN_PIE, Messages.getStringMessage(p, "inventory.mod.invisible")));
		inv.query(new SlotPos(3, 1)).set(Utils.createItem(ItemTypes.FEATHER, "&rFly: " + Messages.getStringMessage(p, "inventory.manager." + (p.get(Keys.CAN_FLY).get() ? "enabled" : "disabled"))));
		if (Perm.hasPerm(SpongeNegativityPlayer.getNegativityPlayer(p), "manageCheat"))
			inv.query(new SlotPos(4, 1)).set(Utils.createItem(ItemTypes.TNT, Messages.getStringMessage(p, "inventory.mod.cheat_manage")));
		inv.query(new SlotPos(6, 1)).set(Utils.createItem(ItemTypes.LEAD, Messages.getStringMessage(p, "inventory.mod.random_tp")));
		inv.query(new SlotPos(7, 1)).set(Utils.createItem(ItemTypes.IRON_SHOVEL, Messages.getStringMessage(p, "inventory.mod.clear_inv")));
		inv.query(new SlotPos(8, 2)).set(Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	private static int i = 0;

	public static void openCheatManagerMenu(Player p) {
		int size = Utils.getMultipleOf(Cheat.values().length + 3, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of("Cheat manager")))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.build(SpongeNegativity.INSTANCE);
		inv = Utils.rempliInvWith(VIDE_ITEM, inv);
		for (Cheat c : Cheat.values())
			if (c.getMaterial() != null && c.getProtocolClass() != null)
				inv.offer(Utils.createItem(c.getMaterial(), c.getName()));
		Cheat[] cheats = Cheat.values();
		inv.slots().forEach((slot) -> {
			if (cheats.length <= i)
				return;
			Cheat c = cheats[i++];
			if (!c.equals(Cheat.ALL))
				slot.set(Utils.createItem(c.getMaterial(), c.getName()));
		});
		i = 0;
		inv.query(new SlotPos(7, nbLine--))
				.set(Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		inv.query(new SlotPos(8, nbLine--))
				.set(Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void openOneCheatMenu(Player p, Cheat c) {
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(c.getName())))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.build(SpongeNegativity.INSTANCE);
		inv = Utils.rempliInvWith(VIDE_ITEM, inv);
		inv.query(new SlotPos(2, 0)).set(Utils.createItem(ItemTypes.TNT, Messages.getStringMessage(p,
				"inventory.manager.setBack", "%back%",
				Messages.getStringMessage(p, "inventory.manager." + (c.isSetBack() ? "enabled" : "disabled")))));
		inv.query(new SlotPos(5, 0)).set(Utils.createItem(ItemTypes.ENDER_EYE, Messages.getStringMessage(p,
				"inventory.manager.autoVerif", "%auto%",
				Messages.getStringMessage(p, "inventory.manager." + (c.isAutoVerif() ? "enabled" : "disabled")))));
		inv.query(new SlotPos(0, 1)).set(Utils.createItem(c.getMaterial(), c.getName()));
		inv.query(new SlotPos(2, 2)).set(Utils.createItem(ItemTypes.BLAZE_ROD, Messages.getStringMessage(p,
				"inventory.manager.allowKick", "%allow%",
				Messages.getStringMessage(p, "inventory.manager." + (c.allowKick() ? "enabled" : "disabled")))));
		inv.query(new SlotPos(5, 2))
				.set(Utils.createItem(ItemTypes.DIAMOND, Messages.getStringMessage(p, "inventory.manager.setActive",
						"%active%",
						Messages.getStringMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")))));

		inv.query(new SlotPos(8, 0))
				.set(Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		inv.query(new SlotPos(8, 2))
				.set(Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void openForgeModsMenu(Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		int size = Utils.getMultipleOf(np.MODS.size() + 1, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder()
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(NAME_FORGE_MOD_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.build(SpongeNegativity.INSTANCE);
		inv = Utils.rempliInvWith(VIDE_ITEM, inv);
		if (np.MODS.size() == 0) {
			inv.query(new SlotPos(4, 0)).set(Utils.createItem(ItemTypes.DIAMOND, "&rNo mods"));
		} else {
			int x = 0, y = 0;
			for(Entry<String, String> entry : np.MODS.entrySet()) {
				inv.query(new SlotPos(x, y)).set(Utils.createItem(ItemTypes.GRASS, "&r" + entry.getKey(), "&7Version: " + entry.getValue()));
				x++;
				if (x > 8) {
					x = 0;
					y++;
				}
			}
		}
		inv.query(new SlotPos(8, nbLine--))
			.set(Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}
}
