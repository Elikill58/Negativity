package com.elikill58.negativity.common.inventories;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.ModHolder;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class ModInventory extends AbstractInventory {

	public ModInventory() {
		super(NegativityInventory.MOD);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Inventory.createInventory(Inv.NAME_MOD_MENU, 27, new ModHolder());

		inv.set(10, ItemBuilder.Builder(Materials.GHAST_TEAR).displayName(Messages.getMessage(p, "inventory.mod.night_vision")).build());
		inv.set(11, ItemBuilder.Builder(Materials.PUMPKIN_PIE).displayName(Messages.getMessage(p, "inventory.mod.invisible")).build());
		inv.set(12, ItemBuilder.Builder(Materials.FEATHER).displayName("Fly: " + Messages.getMessage(p, "inventory.manager." + (p.isFlying() ? "enabled" : "disabled"))).build());
		if(Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), Perm.MANAGE_CHEAT))
			inv.set(14, ItemBuilder.Builder(Materials.TNT).displayName(Messages.getMessage(p, "inventory.mod.cheat_manage")).build());
		inv.set(15, ItemBuilder.Builder(Materials.LEASH).displayName(Messages.getMessage(p, "inventory.mod.random_tp")).build());
		inv.set(16, ItemBuilder.Builder(Materials.IRON_SHOVEL).displayName(Messages.getMessage(p, "inventory.mod.clear_inv")).build());
		
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());

		InventoryUtils.fillInventory(inv, Inv.EMPTY);
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if (m.equals(Materials.GHAST_TEAR)) {
			if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
				Messages.sendMessage(p, "inventory.mod.vision_removed");
			} else {
				p.addPotionEffect(PotionEffectType.NIGHT_VISION, 10000, 0);
				Messages.sendMessage(p, "inventory.mod.vision_added");
			}
		} else if (m.equals(Materials.IRON_SHOVEL)) {
			p.closeInventory();
			p.getInventory().clear();
			p.getInventory().setArmorContent(null);
			Messages.sendMessage(p, "inventory.mod.inv_cleared");
		} else if (m.equals(Materials.LEASH)) {
			p.closeInventory();
			Player randomPlayer = (Player) Utils.getOnlinePlayers().toArray()[Utils.getOnlinePlayers().size() - 1];
			p.teleport(randomPlayer);
		} else if (m.equals(Materials.PUMPKIN_PIE)) {
			p.closeInventory();
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
			np.isInvisible = !np.isInvisible;
			if (np.isInvisible) {
				for (Player pls : Adapter.getAdapter().getOnlinePlayers())
					pls.hidePlayer(p);
				Messages.sendMessage(p, "inventory.mod.now_invisible");
			} else {
				for (Player pls : Adapter.getAdapter().getOnlinePlayers())
					pls.showPlayer(p);
				Messages.sendMessage(p, "inventory.mod.no_longer_invisible");
			}
		} else if (m.equals(Materials.TNT)) {
			InventoryManager.open(NegativityInventory.CHEAT_MANAGER, p, false);
			/*p.closeInventory();
			CheatManagerInventory.openCheatManagerMenu(p);*/
		} else if (m.getId().contains("FEATHER")) {
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
