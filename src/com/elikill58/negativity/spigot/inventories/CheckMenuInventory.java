package com.elikill58.negativity.spigot.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;

public class CheckMenuInventory {

	public static void openCheckMenu(Player p, Player cible) {
		Inventory inv = Bukkit.createInventory(null, 27, Inv.NAME_CHECK_MENU);
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		inv.setItem(0, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("STAINED_CLAY", "LEGACY_STAINED_CLAY"), Messages.getMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), 1, getByteFromClick(np.ACTUAL_CLICK)));
		inv.setItem(1, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("STAINED_CLAY", "LEGACY_STAINED_CLAY"), Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)), 1, getByteFromClick(np.BETTER_CLICK)));
		inv.setItem(2, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("STAINED_CLAY", "LEGACY_STAINED_CLAY"), Messages.getMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1, getByteFromClick(np.LAST_CLICK)));
		
		inv.setItem(7, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		inv.setItem(8, Utils.createSkull(cible.getName(), 1, cible.getName(), ChatColor.GOLD + "UUID: " + cible.getUniqueId()));

		inv.setItem(9, Utils.createItem(Material.DIAMOND_SWORD, "Fight: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		inv.setItem(10, Utils.createItem(Material.DIAMOND_PICKAXE, "Minerate", np.mineRate.getInventoryLoreString()));
		inv.setItem(11, Utils.createItem(Material.GRASS, ChatColor.RESET + "Mods", ChatColor.GRAY + "Forge: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		inv.setItem(12, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("SKULL_ITEM", "LEGACY_SKULL_ITEM"), "Fake entities"));
		//inv.setItem(16, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("DIAMOND_SPADE", "LEGACY_DIAMOND_SPADE"), "Kick"));
		//inv.setItem(17, Utils.createItem(Material.ANVIL, "Ban"));
		
		inv.setItem(18, Utils.createItem(Material.SPIDER_EYE, Messages.getMessage(p, "inventory.main.see_inv", "%name%", cible.getName())));
		inv.setItem(19, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("EYE_OF_ENDER", "LEGACY_EYE_OF_ENDER"), Messages.getMessage(p, "inventory.main.teleportation_to", "%name%", cible.getName())));
		inv.setItem(20, Utils.createItem(Material.PACKED_ICE, Messages.getMessage(p, "inventory.main.freezing", "%name%", cible.getName())));
		inv.setItem(21, Utils.createItem(Material.PAPER, Messages.getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())));
		inv.setItem(22, Utils.createItem(Material.TNT, Messages.getMessage(p, "inventory.main.active_detection", "%name%", cible.getName())));
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, Inv.EMPTY);
		inv.setItem(inv.getSize() - 1, Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void actualizeCheckMenu(Player p, Player cible) {
		Inventory inv = p.getOpenInventory().getTopInventory();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		try {
			inv.setItem(0, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("STAINED_CLAY", "LEGACY_STAINED_CLAY"), Messages.getMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), 1, getByteFromClick(np.ACTUAL_CLICK)));
			inv.setItem(1, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("STAINED_CLAY", "LEGACY_STAINED_CLAY"), Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)), 1, getByteFromClick(np.BETTER_CLICK)));
			inv.setItem(2, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("STAINED_CLAY", "LEGACY_STAINED_CLAY"), Messages.getMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1, getByteFromClick(np.LAST_CLICK)));
			
			inv.setItem(7, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
			inv.setItem(9, Utils.createItem(Material.DIAMOND_SWORD, "Fight: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
			//p.updateInventory();
		} catch (ArrayIndexOutOfBoundsException e) {
			
		}
	}
	
	private static byte getByteFromClick(int click) {
		if (click > 25)
			return 14;
		else if (click < 25 && click > 15)
			return 4;
		else
			return 5;
	}
	
	public static void manageCheckMenu(InventoryClickEvent e, Material m, Player p) {
		e.setCancelled(true);
		if (m.equals(SpigotNegativity.MATERIAL_CLOSE)) {
			p.closeInventory();
			return;
		}
		Player cible = Inv.CHECKING.get(p);
		if (m == Utils.getMaterialWith1_13_Compatibility("EYE_OF_ENDER", "LEGACY_EYE_OF_ENDER")) {
			p.teleport(cible);
			p.closeInventory();
			Inv.CHECKING.remove(p);
		} else if (m == Utils.getMaterialWith1_13_Compatibility("SKULL_ITEM", "LEGACY_SKULL_ITEM")) {
			SkullMeta skullmeta = (SkullMeta) e.getCurrentItem().getItemMeta();
			if(skullmeta.hasOwner())
				return;
			p.closeInventory();
			SpigotNegativityPlayer.getNegativityPlayer(cible).makeAppearEntities();
		} else if(m == Utils.getMaterialWith1_13_Compatibility("DIAMOND_SPADE", "LEGACY_DIAMOND_SPADE")) {
			// kick
		} else {
			switch (m) {
			case SPIDER_EYE:
				p.openInventory(cible.getInventory());
				Inv.CHECKING.remove(p);
				break;
			case TNT:
				ActivedCheatInventory.openActivedCheat(p, cible);
				break;
			case PACKED_ICE:
				p.closeInventory();
				SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
				np.isFreeze = !np.isFreeze;
				if (np.isFreeze) {
					if (Adapter.getAdapter().getBooleanInConfig("inventory.inv_freeze_active"))
						Inv.openFreezeMenu(cible);
					Messages.sendMessage(cible, "inventory.main.freeze", "%name%", p.getName());
				} else
					Messages.sendMessage(cible, "inventory.main.unfreeze", "%name%", p.getName());
				break;
			case PAPER:
				AlertInventory.openAlertMenu(p, cible);
				break;
			case GRASS:
				ForgeModsInventory.openForgeModsMenu(cible);
				break;
			case ANVIL:
				// ban
				break;
			default:
				break;
			}
		}		
	}
}
