package com.elikill58.negativity.sponge.inventories;

import static com.elikill58.negativity.sponge.utils.ItemUtils.hideAttributes;
import static com.elikill58.negativity.sponge.utils.ItemUtils.createItem;
import static com.elikill58.negativity.sponge.Messages.getStringMessage;
import static org.spongepowered.api.item.ItemTypes.STAINED_HARDENED_CLAY;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.inventories.holders.CheckMenuOfflineHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.utils.ItemUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.NegativityAccount;

public class CheckMenuOfflineInventory extends AbstractInventory<CheckMenuOfflineHolder> {

	public CheckMenuOfflineInventory() {
		super(InventoryType.CHECK_MENU_OFFLINE);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof CheckMenuOfflineHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		User cible = (User) args[0];
		Inventory inv = Inventory.builder().withCarrier(new CheckMenuOfflineHolder(cible))
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_CHECK_MENU_OFFLINE)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.property(Inv.INV_ID_KEY, Inv.CHECK_INV_ID).build(SpongeNegativity.getInstance());
		Utils.fillInventoryWith(Inv.EMPTY, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		NegativityAccount account = np.getAccount();
		invGrid.set(0, 0,
				createItem(STAINED_HARDENED_CLAY,
						getStringMessage(p, "inventory.main.actual_click", "%clicks%", np.ACTUAL_CLICK), 1,
						Utils.getByteFromClick(np.ACTUAL_CLICK)));
		invGrid.set(1, 0,
				createItem(STAINED_HARDENED_CLAY,
						getStringMessage(p, "inventory.main.max_click", "%clicks%", account.getMostClicksPerSecond()),
						1, Utils.getByteFromClick(account.getMostClicksPerSecond())));
		invGrid.set(2, 0,
				createItem(STAINED_HARDENED_CLAY,
						getStringMessage(p, "inventory.main.last_click", "%clicks%", np.LAST_CLICK), 1,
						Utils.getByteFromClick(np.LAST_CLICK)));
		invGrid.set(8, 0, ItemUtils.createSkull(cible.getName(), 1, cible, "&6UUID: " + cible.getUniqueId()));

		invGrid.set(0, 1, hideAttributes(createItem(ItemTypes.DIAMOND_SWORD, "&rFight: "
				+ getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled")))));
		invGrid.set(1, 1, hideAttributes(
				createItem(ItemTypes.DIAMOND_PICKAXE, "&rMinerate", account.getMinerate().getInventoryLoreString())));
		invGrid.set(3, 1, Utils.getMcLeaksIndicator(p, np));

		invGrid.set(0, 2, createItem(ItemTypes.SPIDER_EYE,
				getStringMessage(p, "inventory.main.see_inv", "%name%", cible.getName())));

		invGrid.set(3, 2, createItem(ItemTypes.ANVIL,
				getStringMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())));
		invGrid.set(4, 2, createItem(ItemTypes.TNT,
				getStringMessage(p, "inventory.main.active_detection", "%name%", cible.getName())));
		invGrid.set(8, 2, createItem(ItemTypes.BARRIER, getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, CheckMenuOfflineHolder nh) {
		User cible = nh.getUser();
		if (m.equals(ItemTypes.SPIDER_EYE)) {
			delayed(() -> p.openInventory(cible.getInventory()));
		} else if (m.equals(ItemTypes.TNT)) {
			delayed(() -> AbstractInventory.open(InventoryType.ACTIVED_CHEAT, p, cible));
		} else if (m.equals(ItemTypes.ANVIL)) {
			delayed(() -> AbstractInventory.open(InventoryType.ALERT, p, cible));
		} else if (m.equals(ItemTypes.SKULL)) {
			delayedInvClose(p);
			SpongeNegativityPlayer.getNegativityPlayer(cible).makeAppearEntities();
		}
	}

	@Override
	public void actualizeInventory(Player p, Object... args) {
		Inventory inv = p.getOpenInventory().get();
		User cible = (User) ((CheckMenuOfflineHolder) ((CarriedInventory<?>) inv).getCarrier().get()).getUser();
		GridInventory grid = inv.first().query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		NegativityAccount account = np.getAccount();

		grid.set(0, 0,
				createItem(STAINED_HARDENED_CLAY,
						getStringMessage(p, "inventory.main.actual_click", "%clicks%", np.ACTUAL_CLICK), 1,
						Utils.getByteFromClick(np.ACTUAL_CLICK)));
		grid.set(1, 0,
				createItem(STAINED_HARDENED_CLAY,
						getStringMessage(p, "inventory.main.max_click", "%clicks%", account.getMostClicksPerSecond()),
						1, Utils.getByteFromClick(account.getMostClicksPerSecond())));
		grid.set(2, 0,
				createItem(STAINED_HARDENED_CLAY,
						getStringMessage(p, "inventory.main.last_click", "%clicks%", np.LAST_CLICK), 1,
						Utils.getByteFromClick(np.LAST_CLICK)));
		grid.set(0, 1, hideAttributes(createItem(ItemTypes.DIAMOND_SWORD, "&rFight: "
				+ getStringMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled")))));
	}
}
