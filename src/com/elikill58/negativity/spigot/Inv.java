package com.elikill58.negativity.spigot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class Inv {

	public static final String NAME_CHECK_MENU = "Check",
			NAME_ACTIVED_CHEAT_MENU = Messages.getMessage("inventory.detection.name_inv"), NAME_FREEZE_MENU = "Freeze",
			NAME_MOD_MENU = "Mod", NAME_ALERT_MENU = "Alerts", CHEAT_MANAGER = "Cheat Manager", NAME_FORGE_MOD_MENU = "Mods";
	public static final HashMap<Player, Player> CHECKING = new HashMap<>();
	public static final ItemStack EMPTY = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);

	public static void openCheckMenu(Player p, Player cible) {
		Inventory inv = Bukkit.createInventory(null, 18, NAME_CHECK_MENU);
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		inv.setItem(0, Utils.createItem(Material.STAINED_CLAY,
				Messages.getMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), 1,
				getByteFromClick(np.ACTUAL_CLICK)));
		inv.setItem(1,
				Utils.createItem(Material.STAINED_CLAY,
						Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)),
						1, getByteFromClick(np.BETTER_CLICK)));
		inv.setItem(2,
				Utils.createItem(Material.STAINED_CLAY,
						Messages.getMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1,
						getByteFromClick(np.LAST_CLICK)));

		inv.setItem(6, Utils.createItem(Material.DIAMOND_SWORD, "Fight: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		inv.setItem(7, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		inv.setItem(8, Utils.createSkull(cible.getName(), 1, cible.getName(),
				ChatColor.GOLD + "UUID: " + cible.getUniqueId()));

		inv.setItem(9, Utils.createItem(Material.SPIDER_EYE,
				Messages.getMessage(p, "inventory.main.see_inv", "%name%", cible.getName())));
		inv.setItem(10, Utils.createItem(Material.EYE_OF_ENDER,
				Messages.getMessage(p, "inventory.main.teleportation_to", "%name%", cible.getName())));
		inv.setItem(11, Utils.createItem(Material.PACKED_ICE,
				Messages.getMessage(p, "inventory.main.freezing", "%name%", cible.getName())));
		inv.setItem(12, Utils.createItem(Material.GRASS, ChatColor.RESET + "Mods", ChatColor.GRAY + "Forge: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		inv.setItem(13, Utils.createItem(Material.ANVIL,
				Messages.getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())));
		inv.setItem(14, Utils.createItem(Material.TNT,
				Messages.getMessage(p, "inventory.main.active_detection", "%name%", cible.getName())));
		inv.setItem(15, Utils.createItem(Material.DIAMOND_PICKAXE, "Minerate", np.mineRate.getInventoryLoreString()));
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, EMPTY);
		inv.setItem(inv.getSize() - 1,
				Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void actualizeCheckMenu(Player p, Player cible) {
		Inventory inv = p.getOpenInventory().getTopInventory();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);

		inv.setItem(0, Utils.createItem(Material.STAINED_CLAY,
				Messages.getMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), 1,
				getByteFromClick(np.ACTUAL_CLICK)));
		inv.setItem(1,
				Utils.createItem(Material.STAINED_CLAY,
						Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)),
						1, getByteFromClick(np.BETTER_CLICK)));
		inv.setItem(2,
				Utils.createItem(Material.STAINED_CLAY,
						Messages.getMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1,
						getByteFromClick(np.LAST_CLICK)));
		
		inv.setItem(6, Utils.createItem(Material.DIAMOND_SWORD, "Fight: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		inv.setItem(7, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		p.updateInventory();
	}

	public static void openAlertMenu(Player p, Player cible) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			if((Adapter.getAdapter().getBooleanInConfig("inventory.alerts.only_cheat_active") && np.ACTIVE_CHEAT.contains(c))
					|| (!np.ACTIVE_CHEAT.contains(c) && Adapter.getAdapter().getBooleanInConfig("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		}
		Inventory inv = Bukkit.createInventory(null, Utils.getMultipleOf(TO_SEE.size() + 3, 9, 1), NAME_ALERT_MENU);
		int slot = 0;
		for (Cheat c : TO_SEE) {
			if (c.getMaterial() != null && c.getProtocolClass() != null){
				inv.setItem(slot++, Utils.createItem(c.getMaterial(), Messages.getMessage(p, "inventory.alerts.item_name",
						"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))), np.getWarn(c) == 0 ? 1 : np.getWarn(c)));
			}
		}
		inv.setItem(inv.getSize() - 3, Utils.createItem(Material.BONE, Messages.getMessage(p, "inventory.reset_alert")));
		inv.setItem(inv.getSize() - 2, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1,
				Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void actualizeAlertMenu(Player p, Player cible) {
		Inventory inv = p.getOpenInventory().getTopInventory();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values())
			if ((c.isActive()
					&& Adapter.getAdapter().getBooleanInConfig("inventory.alerts.see.only_cheat_active") && np.ACTIVE_CHEAT.contains(c))
					|| (!np.ACTIVE_CHEAT.contains(c) && Adapter.getAdapter().getBooleanInConfig("inventory.alerts.see.no_started_verif_cheat")))
				TO_SEE.add(c);
		int slot = 0;
		for (Cheat c : TO_SEE)
			if (c.getMaterial() != null && c.getProtocolClass() != null)
				inv.setItem(slot++, Utils.createItem(c.getMaterial(), Messages.getMessage(p, "inventory.alerts.item_name",
						"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))), np.getWarn(c) == 0 ? 1 : np.getWarn(c)));
		//p.updateInventory();
	}

	private static byte getByteFromClick(int click) {
		if (click > 25)
			return 14;
		else if (click < 25 && click > 15)
			return 4;
		else
			return 5;
	}

	public static void openActivedCheat(Player p, Player cible) {
		Inventory inv = Bukkit.createInventory(null, Utils.getMultipleOf(Cheat.values().length + 3, 9, 1), NAME_ACTIVED_CHEAT_MENU);
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		if (np.ACTIVE_CHEAT.size() > 0) {
			int slot = 0;
			for (Cheat c : np.ACTIVE_CHEAT) {
				if (c.equals(Cheat.ALL))
					continue;
				inv.setItem(slot, Utils.createItem(c.getMaterial(), ChatColor.RESET + c.getName()));
				slot++;
			}
		} else
			inv.setItem(13, Utils.createItem(Material.REDSTONE_BLOCK, Messages.getMessage(p, "inventory.detection.no_active", "%name%", cible.getName())));
		inv.setItem(inv.getSize() - 2, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1,
				Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void openFreezeMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, NAME_FREEZE_MENU);
		inv.setItem(13, Utils.createItem(Material.PAPER, Messages.getMessage(p, "inventory.mod.you_are_freeze")));
		p.openInventory(inv);
	}

	public static void openModMenu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, NAME_MOD_MENU);

		inv.setItem(10, Utils.createItem(Material.GHAST_TEAR, Messages.getMessage(p, "inventory.mod.night_vision")));
		inv.setItem(11, Utils.createItem(Material.PUMPKIN_PIE, Messages.getMessage(p, "inventory.mod.invisible")));
		if(Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "manageCheat"))
			inv.setItem(13, Utils.createItem(Material.TNT, Messages.getMessage(p, "inventory.mod.cheat_manage")));
		inv.setItem(15, Utils.createItem(Material.LEASH, Messages.getMessage(p, "inventory.mod.random_tp")));
		inv.setItem(16, Utils.createItem(Material.IRON_SPADE, Messages.getMessage(p, "inventory.mod.clear_inv")));

		inv.setItem(inv.getSize() - 1,
				Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));

		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, EMPTY);
		p.openInventory(inv);
	}
	
	public static void openCheatManagerMenu(Player p){
		Inventory inv = Bukkit.createInventory(null, Utils.getMultipleOf(Cheat.values().length + 3, 9, 1), CHEAT_MANAGER);
		int slot = 0;
		for(Cheat c : Cheat.values())
			if(c.getMaterial() != null && c.getProtocolClass() != null)
				inv.setItem(slot++, Utils.createItem(c.getMaterial(), c.getName()));

		inv.setItem(inv.getSize() - 2, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1, Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}
	
	public static void openOneCheatMenu(Player p, Cheat c){
		Inventory inv = Bukkit.createInventory(null, 27, c.getName());
		inv.setItem(2, Utils.createItem(Material.TNT, Messages.getMessage(p, "inventory.manager.setBack", "%back%", Messages.getMessage(p, "inventory.manager." + (c.isSetBack() ? "enabled" : "disabled")))));
		inv.setItem(5, Utils.createItem(Material.EYE_OF_ENDER, Messages.getMessage(p, "inventory.manager.autoVerif", "%auto%", Messages.getMessage(p, "inventory.manager." + (c.isAutoVerif() ? "enabled" : "disabled")))));
		inv.setItem(9, Utils.createItem(c.getMaterial(), c.getName()));
		inv.setItem(20, Utils.createItem(Material.BLAZE_ROD, Messages.getMessage(p, "inventory.manager.allowKick", "%allow%", Messages.getMessage(p, "inventory.manager." + (c.allowKick() ? "enabled" : "disabled")))));
		inv.setItem(23, Utils.createItem(Material.DIAMOND, Messages.getMessage(p, "inventory.manager.setActive", "%active%", Messages.getMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")))));
		
		inv.setItem(8, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1, Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, EMPTY);
		p.openInventory(inv);
	}
	
	public static int slot = 0;
	
	public static void openForgeModsMenu(Player p) {
		slot = 0;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		Inventory inv = Bukkit.createInventory(null, Utils.getMultipleOf(np.MODS.size() + 1, 9, 1), NAME_FORGE_MOD_MENU);
		if(np.MODS.size() == 0) {
			inv.setItem(4, Utils.createItem(Material.DIAMOND, "No mods"));
		} else {
			np.MODS.forEach((name, version) -> {
				inv.setItem(slot++, Utils.createItem(Material.GRASS, name, ChatColor.GRAY + "Version: " + version));
			});
		}
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, EMPTY);
		p.openInventory(inv);
	}
}
