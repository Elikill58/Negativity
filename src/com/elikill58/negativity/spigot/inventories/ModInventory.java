package com.elikill58.negativity.spigot.inventories;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.ModHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.InventoryUtils;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.permissions.Perm;

public class ModInventory extends AbstractInventory {

	public ModInventory() {
		super(InventoryType.MOD);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Bukkit.createInventory(new ModHolder(), 27, Inv.NAME_MOD_MENU);

		inv.setItem(10, createItem(Material.GHAST_TEAR, Messages.getMessage(p, "inventory.mod.night_vision")));
		inv.setItem(11, createItem(Material.PUMPKIN_PIE, Messages.getMessage(p, "inventory.mod.invisible")));
		inv.setItem(12, createItem(Material.FEATHER, "Fly: " + Messages.getMessage(p, "inventory.manager." + (p.isFlying() ? "enabled" : "disabled"))));
		if(Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), Perm.MANAGE_CHEAT))
			inv.setItem(13, createItem(Material.TNT, Messages.getMessage(p, "inventory.mod.cheat_manage")));
		inv.setItem(14, createItem(Material.APPLE, "Heal"));
		inv.setItem(15, createItem(ItemUtils.LEASH, Messages.getMessage(p, "inventory.mod.random_tp")));
		inv.setItem(16, ItemUtils.hideAttributes(createItem(ItemUtils.IRON_SPADE, Messages.getMessage(p, "inventory.mod.clear_inv"))));
		
		inv.setItem(inv.getSize() - 1, createItem(ItemUtils.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));

		InventoryUtils.fillInventory(inv, Inv.EMPTY);
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
		} else if (m.equals(ItemUtils.IRON_SPADE)) {
			p.closeInventory();
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			Messages.sendMessage(p, "inventory.mod.inv_cleared");
		} else if (m.equals(ItemUtils.LEASH)) {
			p.closeInventory();
			Player randomPlayer = (Player) Utils.getOnlinePlayers().toArray()[Utils.getOnlinePlayers().size() - 1];
			p.teleport(randomPlayer);
		} else if (m.equals(Material.PUMPKIN_PIE)) {
			p.closeInventory();
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			np.isInvisible = !np.isInvisible;
			if (np.isInvisible) {
				for (Player pls : Utils.getOnlinePlayers())
					if(!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(pls), Perm.ADMIN))
						Utils.hidePlayer(pls, p);
				Messages.sendMessage(p, "inventory.mod.now_invisible");
			} else {
				for (Player pls : Utils.getOnlinePlayers())
					if(!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(pls), Perm.ADMIN))
						Utils.hidePlayer(pls, p);
				Messages.sendMessage(p, "inventory.mod.no_longer_invisible");
			}
		} else if (m.equals(Material.TNT)) {
			AbstractInventory.open(InventoryType.CHEAT_MANAGER, p, false);
		} else if (m.name().contains("FEATHER")) {
			p.closeInventory();
			p.setAllowFlight(!p.getAllowFlight());
			p.sendMessage("Flying: "
					+ Messages.getMessage(p, "inventory.manager." + (p.getAllowFlight() ? "enabled" : "disabled")));
		} else if (m.equals(Material.APPLE)) {
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			p.sendMessage(ChatColor.GOLD + "Healed !");
		}
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ModHolder;
	}
}
