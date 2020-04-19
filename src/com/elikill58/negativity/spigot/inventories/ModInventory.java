package com.elikill58.negativity.spigot.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.ModHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.permissions.Perm;

public class ModInventory extends AbstractInventory {

	public ModInventory() {
		super(InventoryType.MOD);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Bukkit.createInventory(new ModHolder(), 27, Inv.NAME_MOD_MENU);

		inv.setItem(10, Utils.createItem(Material.GHAST_TEAR, Messages.getMessage(p, "inventory.mod.night_vision")));
		inv.setItem(11, Utils.createItem(Material.PUMPKIN_PIE, Messages.getMessage(p, "inventory.mod.invisible")));
		inv.setItem(12, Utils.createItem(Material.FEATHER, "Fly: " + Messages.getMessage(p, "inventory.manager." + (p.isFlying() ? "enabled" : "disabled"))));
		if(Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "manageCheat"))
			inv.setItem(14, Utils.createItem(Material.TNT, Messages.getMessage(p, "inventory.mod.cheat_manage")));
		inv.setItem(15, Utils.createItem(Utils.getMaterialWith1_15_Compatibility("LEASH", "LEGACY_LEASH"), Messages.getMessage(p, "inventory.mod.random_tp")));
		inv.setItem(16, Utils.hideAttributes(Utils.createItem(Utils.getMaterialWith1_15_Compatibility("IRON_SPADE", "LEGACY_IRON_SPADE"), Messages.getMessage(p, "inventory.mod.clear_inv"))));
		
		inv.setItem(inv.getSize() - 1, Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));

		Utils.fillInventory(inv, Inv.EMPTY);
		p.openInventory(inv);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if (m.equals(Material.GHAST_TEAR)) {
			if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
				Messages.sendMessage(p, "inventory.mod.vision_removed");
			} else {
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000, 0));
				Messages.sendMessage(p, "inventory.mod.vision_added");
			}
		} else if (m.name().contains("IRON_SPADE")) {
			p.closeInventory();
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			Messages.sendMessage(p, "inventory.mod.inv_cleared");
		} else if (m.name().contains("LEASH")) {
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
			AbstractInventory.open(InventoryType.CHEAT_MANAGER, p, false);
			/*p.closeInventory();
			CheatManagerInventory.openCheatManagerMenu(p);*/
		} else if (m.name().contains("FEATHER")) {
			p.closeInventory();
			p.setAllowFlight(!p.getAllowFlight());
			p.sendMessage("Flying: "
					+ Messages.getMessage(p, "inventory.manager." + (p.getAllowFlight() ? "enabled" : "disabled")));
		}
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ModHolder;
	}
}
