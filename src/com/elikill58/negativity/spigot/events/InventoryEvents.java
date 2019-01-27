package com.elikill58.negativity.spigot.events;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;

@SuppressWarnings("deprecation")
public class InventoryEvents implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getClickedInventory() == null || !(e.getWhoClicked() instanceof Player))
			return;
		Player p = (Player) e.getWhoClicked();
		Material m = e.getCurrentItem().getType();
		String invName = e.getClickedInventory().getName();
		if (invName.equals(Inv.NAME_CHECK_MENU)) {
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
			} else {
				switch (m) {
				case SPIDER_EYE:
					p.openInventory(cible.getInventory());
					Inv.CHECKING.remove(p);
					break;
				case TNT:
					Inv.openActivedCheat(p, cible);
					break;
				case PACKED_ICE:
					p.closeInventory();
					SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
					np.isFreeze = !np.isFreeze;
					if (np.isFreeze) {
						if (Adapter.getAdapter().getBooleanInConfig("inventory.inv_freeze_active"))
							Inv.openFreezeMenu(cible);
						Messages.sendMessage(p, "inventory.main.freeze", "%name%", cible.getName());
					} else
						Messages.sendMessage(p, "inventory.main.unfreeze", "%name%", cible.getName());
					break;
				case ANVIL:
					Inv.openAlertMenu(p, cible);
					break;
				case GRASS:
					Inv.openForgeModsMenu(cible);
					break;
				default:
					break;
				}
			}
		} else if (invName.equals(Inv.NAME_ACTIVED_CHEAT_MENU)) {
			e.setCancelled(true);
			if (m.equals(SpigotNegativity.MATERIAL_CLOSE)) {
				p.closeInventory();
			} else if (m.equals(Material.ARROW))
				Inv.openCheckMenu(p, Inv.CHECKING.get(p));
		} else if (invName.equals(Inv.NAME_FREEZE_MENU))
			e.setCancelled(true);
		else if (invName.equals(Inv.NAME_MOD_MENU)) {
			e.setCancelled(true);
			if (m.equals(SpigotNegativity.MATERIAL_CLOSE)) {
				p.closeInventory();
			} else if (m.equals(Material.GHAST_TEAR)) {
				if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
					p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					Messages.sendMessage(p, "inventory.mod.vision_removed");
				} else {
					p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000, 0));
					Messages.sendMessage(p, "inventory.mod.vision_added");
				}
			} else if (m.equals(Utils.getMaterialWith1_13_Compatibility("IRON_SPADE", "LEGACY_IRON_SPADE"))) {
				p.closeInventory();
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				Messages.sendMessage(p, "inventory.mod.inv_cleared");
			} else if (m.equals(Utils.getMaterialWith1_13_Compatibility("LEASH", "LEGACY_LEASH"))) {
				p.closeInventory();
				Player randomPlayer = (Player) Utils.getOnlinePlayers().toArray()[Utils.getOnlinePlayers().size() - 1];
				p.teleport(randomPlayer);
			} else if (m.equals(Material.PUMPKIN_PIE)) {
				p.closeInventory();
				SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
				np.isInvisible = !np.isInvisible;
				if (np.isInvisible) {
					for (Player pls : Utils.getOnlinePlayers())
						pls.hidePlayer(p);
					Messages.sendMessage(p, "inventory.mod.now_invisible");
				} else {
					for (Player pls : Utils.getOnlinePlayers())
						pls.showPlayer(p);
					Messages.sendMessage(p, "inventory.mod.no_longer_invisible");
				}
			} else if (m.equals(Material.TNT)) {
				p.closeInventory();
				Inv.openCheatManagerMenu(p);
			} else if (m.equals(Material.FEATHER)) {
				p.closeInventory();
				p.setAllowFlight(!p.getAllowFlight());
				p.sendMessage("Flying: "
						+ Messages.getMessage(p, "inventory.manager." + (p.getAllowFlight() ? "enabled" : "disabled")));
			}
		} else if (invName.equals(Inv.NAME_ALERT_MENU)) {
			e.setCancelled(true);
			if (m.equals(SpigotNegativity.MATERIAL_CLOSE))
				p.closeInventory();
			else if (m.equals(Material.ARROW))
				Inv.openCheckMenu(p, Inv.CHECKING.get(p));
			else if (m.equals(Material.BONE)) {
				for (Cheat c : Cheat.values())
					SpigotNegativityPlayer.getNegativityPlayer(Inv.CHECKING.get(p)).setWarn(c, 0);
				Inv.actualizeAlertMenu(p, Inv.CHECKING.get(p));
			}
		} else if (invName.equals(Inv.CHEAT_MANAGER)) {
			e.setCancelled(true);
			if (m.equals(SpigotNegativity.MATERIAL_CLOSE))
				p.closeInventory();
			else if (m.equals(Material.ARROW))
				Inv.openModMenu(p);
			else {
				Optional<Cheat> c = Utils.getCheatFromItem(m);
				if (c.isPresent())
					Inv.openOneCheatMenu(p, c.get());
			}
		} else if (invName.equals(Inv.NAME_FORGE_MOD_MENU)) {
			e.setCancelled(true);
			if(m.equals(Material.ARROW))
				Inv.openCheckMenu(p, Inv.CHECKING.get(p));
		} else if (Utils.getCheatFromName(invName).isPresent()) {
			e.setCancelled(true);
			if (m.equals(SpigotNegativity.MATERIAL_CLOSE)) {
				p.closeInventory();
				return;
			} else if (m.equals(Material.ARROW)) {
				Inv.openCheatManagerMenu(p);
				return;
			}
			Cheat c = Utils.getCheatFromName(invName).get();
			if (m.equals(c.getMaterial()))
				return;
			Inventory inv = e.getClickedInventory();
			int slot = e.getRawSlot();
			if (m.equals(Material.TNT))
				inv.setItem(slot,
						Utils.createItem(m,
								Messages.getMessage(p, "inventory.manager.setBack", "%back%", Messages.getMessage(p,
										"inventory.manager." + (c.setBack(!c.isSetBack()) ? "enabled" : "disabled")))));
			else if (m.equals(Utils.getMaterialWith1_13_Compatibility("EYE_OF_ENDER", "LEGACY_EYE_OF_ENDER")))
				inv.setItem(slot, Utils.createItem(m,
						Messages.getMessage(p, "inventory.manager.autoVerif", "%auto%", Messages.getMessage(p,
								"inventory.manager." + (c.setAutoVerif(!c.isAutoVerif()) ? "enabled" : "disabled")))));
			else if (m.equals(Material.BLAZE_ROD))
				inv.setItem(slot, Utils.createItem(m,
						Messages.getMessage(p, "inventory.manager.allowKick", "%allow%", Messages.getMessage(p,
								"inventory.manager." + (c.setAllowKick(!c.allowKick()) ? "enabled" : "disabled")))));
			else if (m.equals(Material.DIAMOND))
				inv.setItem(slot, Utils.createItem(m,
						Messages.getMessage(p, "inventory.manager.setActive", "%active%", Messages.getMessage(p,
								"inventory.manager." + (c.setActive(!c.isActive()) ? "enabled" : "disabled")))));
			p.updateInventory();
		}
	}
}
