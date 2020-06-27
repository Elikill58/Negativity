package com.elikill58.negativity.sponge.inventories.admin;

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
import com.elikill58.negativity.sponge.inventories.AbstractInventory;
import com.elikill58.negativity.sponge.inventories.holders.AdminHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.utils.Utils;

import static com.elikill58.negativity.sponge.utils.ItemUtils.createItem;

public class AdminInventory extends AbstractInventory {

	public AdminInventory() {
		super(InventoryType.ADMIN);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Inventory.builder().withCarrier(new AdminHolder())
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_ADMIN_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 1))
				.property(Inv.INV_ID_KEY, Inv.ADMIN_INV_ID)
				.build(SpongeNegativity.getInstance());
		
		Utils.fillInventoryWith(Inv.EMPTY, inv);

		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(0, 0, createItem(ItemTypes.TNT, Messages.getStringMessage(p, "inventory.mod.cheat_manage")));
		invGrid.set(1, 0, createItem(ItemTypes.PAPER, Messages.getStringMessage(p, "lang.edit")));
		invGrid.set(8, 0, createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof AdminHolder;
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, NegativityHolder nh) {
		if(m.equals(ItemTypes.PAPER)) {
			delayed(() -> AbstractInventory.open(InventoryType.LANG, p));
		} else if (m.equals(ItemTypes.TNT))
			delayed(() -> AbstractInventory.open(InventoryType.CHEAT_MANAGER, p, true));
	}
}
