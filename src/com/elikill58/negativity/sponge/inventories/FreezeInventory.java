package com.elikill58.negativity.sponge.inventories;

import static com.elikill58.negativity.sponge.utils.ItemUtils.createItem;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
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
import com.elikill58.negativity.sponge.inventories.holders.FreezeHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;

public class FreezeInventory extends AbstractInventory {

	public FreezeInventory() {
		super(InventoryType.FREEZE);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof FreezeHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Inventory inv = Inventory.builder().withCarrier(new FreezeHolder())
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_FREEZE_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 3))
				.property(Inv.INV_ID_KEY, Inv.FREEZE_INV_ID)
				.build(SpongeNegativity.getInstance());
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(4, 0, createItem(ItemTypes.PAPER, Messages.getStringMessage(p, "inventory.mod.you_are_freeze")));
		p.openInventory(inv);
	}
	
	@Override
	public void closeInventory(Player p, InteractInventoryEvent.Close e) {
		if(SpongeNegativityPlayer.getNegativityPlayer(p).isFreeze)
			e.setCancelled(true);
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, NegativityHolder nh) {
		
	}
}
