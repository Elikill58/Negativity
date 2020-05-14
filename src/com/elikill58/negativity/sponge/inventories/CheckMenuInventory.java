package com.elikill58.negativity.sponge.inventories;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.inventories.holders.CheckMenuHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.timers.ActualizerTimer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.NegativityAccount;

public class CheckMenuInventory extends AbstractInventory {
	
	public CheckMenuInventory() {
		super(InventoryType.CHECK_MENU);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof CheckMenuHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Inventory.builder().withCarrier(new CheckMenuHolder())
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_CHECK_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.property(Inv.INV_ID_KEY, Inv.CHECK_INV_ID)
				.build(SpongeNegativity.getInstance());
		Utils.fillInventoryWith(Inv.EMPTY, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		NegativityAccount account = np.getAccount();
		invGrid.set(0, 0, Utils
				.createItem(ItemTypes.STAINED_HARDENED_CLAY,
						Messages.getStringMessage(p, "inventory.main.actual_click",
								"%clicks%", String.valueOf(np.ACTUAL_CLICK)),
						1, Utils.getByteFromClick(np.ACTUAL_CLICK)));
		invGrid.set(1, 0, Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY,
				Messages.getStringMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)),
				1, Utils.getByteFromClick(np.BETTER_CLICK)));
		invGrid.set(2, 0, Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY,
				Messages.getStringMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1,
				Utils.getByteFromClick(np.LAST_CLICK)));
		invGrid.set(7, 0, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p,
				"inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		invGrid.set(8, 0, Utils.createSkull(cible.getName(), 1, cible, "&6UUID: " + cible.getUniqueId()));

		invGrid.set(0, 1, Utils.hideAttributes(Utils.createItem(ItemTypes.DIAMOND_SWORD, "&rFight: "
				+ Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled")))));
		invGrid.set(1, 1, Utils.hideAttributes(Utils.createItem(ItemTypes.DIAMOND_PICKAXE, "&rMinerate", account.getMinerate().getInventoryLoreString())));
		invGrid.set(2, 1, Utils.createItem(ItemTypes.GRASS, "&rMods", "&7Forge: "
				+ Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		invGrid.set(3, 1, Utils.getMcLeaksIndicator(p, np));
		/*if(Cheat.forKey("FORCEFIELD").get().isActive() && !SpongeNegativity.getConfig().getNode("cheats").getNode("forcefield").getNode("ghost_disabled").getBoolean())
			invGrid.set(3, 1, Utils.createItem(ItemTypes.SKULL, "&rFake entities"));*/

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
		Inv.CHECKING.put(p, cible);
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, NegativityHolder nh) {
		Player cible = Inv.CHECKING.get(p);
		if (m.equals(ItemTypes.ENDER_EYE)) {
			p.setLocation(cible.getLocation());
			delayedInvClose(p);
			Inv.CHECKING.remove(p);
		} else if (m.equals(ItemTypes.SPIDER_EYE)) {
			delayed(() -> p.openInventory(cible.getInventory()));
			Inv.CHECKING.remove(p);
		} else if (m.equals(ItemTypes.TNT)) {
			delayed(() -> AbstractInventory.open(InventoryType.ACTIVED_CHEAT, p, cible));
		} else if (m.equals(ItemTypes.PACKED_ICE) && p != cible) {
			SpongeNegativityPlayer npCible = SpongeNegativityPlayer.getNegativityPlayer(cible);
			npCible.isFreeze = !npCible.isFreeze;
			if (npCible.isFreeze) {
				if (ActualizerTimer.INV_FREEZE_ACTIVE)
					delayed(() -> AbstractInventory.open(InventoryType.FREEZE, cible));
				Messages.sendMessage(p, "inventory.main.freeze", "%name%", cible.getName());
			} else {
				delayedInvClose(p);
				Messages.sendMessage(p, "inventory.main.unfreeze", "%name%", cible.getName());
			}
		} else if (m.equals(ItemTypes.ANVIL)) {
			delayed(() -> AbstractInventory.open(InventoryType.ALERT, p, cible));
		} else if (m.equals(ItemTypes.GRASS)) {
			delayed(() -> AbstractInventory.open(InventoryType.FORGE_MODS, p, cible));
		} else if (m.equals(ItemTypes.SKULL)) {
			delayedInvClose(p);
			SpongeNegativityPlayer.getNegativityPlayer(cible).makeAppearEntities();
		}
	}
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = p.getOpenInventory().get();
		GridInventory invGrid = inv.first().query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);

		invGrid.set(0, 0, Utils
				.createItem(
						ItemTypes.STAINED_HARDENED_CLAY, Messages.getStringMessage(p,
								"inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)),
						1, Utils.getByteFromClick(np.ACTUAL_CLICK)));
		invGrid.set(1, 0, Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY,
				Messages.getStringMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.BETTER_CLICK)),
				1, Utils.getByteFromClick(np.BETTER_CLICK)));
		invGrid.set(2, 0, Utils.createItem(ItemTypes.STAINED_HARDENED_CLAY,
				Messages.getStringMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), 1,
				Utils.getByteFromClick(np.LAST_CLICK)));
		invGrid.set(7, 0, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p,
				"inventory.main.ping", "%name%", cible.getName(), "%ping%", Utils.getPing(cible) + "")));
		invGrid.set(0, 1, Utils.hideAttributes(Utils.createItem(ItemTypes.DIAMOND_SWORD, "&rFight: "
				+ Messages.getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled")))));
	}
}
