package com.elikill58.negativity.common.inventories.mod;

import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.mod.ModHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.permissions.Perm;

public class ModInventory extends AbstractInventory<ModHolder> {

	public ModInventory() {
		super(NegativityInventory.MOD, ModHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Inventory.createInventory(Inventory.NAME_MOD_MENU, 27, new ModHolder());
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);

		inv.set(10, ItemBuilder.Builder(Materials.GHAST_TEAR).displayName(Messages.getMessage(p, "inventory.mod.night_vision")).build());
		inv.set(11, ItemBuilder.Builder(Materials.PUMPKIN_PIE).displayName(Messages.getMessage(p, "inventory.mod.invisible")).build());
		inv.set(12, ItemBuilder.Builder(Materials.FEATHER).displayName(Messages.getMessage(p, "inventory.mod.fly", "%state%", Messages.getMessage(p, "inventory.manager." + (p.isFlying() ? "enabled" : "disabled")))).build());
		if(Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), Perm.MANAGE_CHEAT))
			inv.set(13, ItemBuilder.Builder(Materials.TNT).displayName(Messages.getMessage(p, "inventory.mod.cheat_manage")).build());
		inv.set(14, ItemBuilder.Builder(Materials.APPLE).displayName(Messages.getMessage(p, "inventory.mod.heal")).build());
		inv.set(15, ItemBuilder.Builder(Materials.LEASH).displayName(Messages.getMessage(p, "inventory.mod.random_tp")).build());
		inv.set(16, ItemBuilder.Builder(Materials.IRON_SHOVEL).displayName(Messages.getMessage(p, "inventory.mod.clear_inv")).build());
		
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));

		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, ModHolder nh) {
		if (m.equals(Materials.GHAST_TEAR)) {
			if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
				Messages.sendMessage(p, "inventory.mod.vision_removed");
			} else {
				p.addPotionEffect(PotionEffectType.NIGHT_VISION, 20000000, 0);
				Messages.sendMessage(p, "inventory.mod.vision_added");
			}
		} else if (m.equals(Materials.IRON_SHOVEL)) {
			p.closeInventory();
			p.getInventory().clear();
			p.getInventory().setArmorContent(null);
			Messages.sendMessage(p, "inventory.mod.inv_cleared");
		} else if (m.equals(Materials.LEASH)) {
			p.closeInventory();
			List<Player> list = Adapter.getAdapter().getOnlinePlayers();
			if(list.size() == 1) {
				Messages.sendMessage(p, "inventory.mod.random_tp_no_target");
			} else {
				Player randomPlayer = (Player) list.toArray()[list.size() - 1];
				p.teleport(randomPlayer);
			}
		} else if (m.equals(Materials.PUMPKIN_PIE)) {
			p.closeInventory();
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
			np.isInvisible = !np.isInvisible;
			p.setVanished(np.isInvisible);
			Messages.sendMessage(p, np.isInvisible ? "inventory.mod.now_invisible" : "inventory.mod.no_longer_invisible");
		} else if (m.equals(Materials.TNT)) {
			InventoryManager.open(NegativityInventory.ADMIN, p);
		} else if (m.equals(Materials.FEATHER)) {
			p.closeInventory();
			p.setAllowFlight(!p.getAllowFlight());
			Messages.sendMessage(p, "inventory.mod.fly_changed", "%state%", Messages.getMessage(p, "inventory.manager." + (p.getAllowFlight() ? "enabled" : "disabled")));
		} else if (m.equals(Materials.APPLE)) {
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			Messages.sendMessage(p, "inventory.mod.healed");
		}
	}
}
