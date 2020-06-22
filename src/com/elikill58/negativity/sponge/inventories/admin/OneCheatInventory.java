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
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.inventories.holders.OneCheatHolder;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

import static com.elikill58.negativity.sponge.utils.ItemUtils.createItem;

public class OneCheatInventory extends AbstractInventory {

	public OneCheatInventory() {
		super(InventoryType.ONE_CHEAT);
	}
	
	private String getMessage(Player p, boolean b) {
		return Messages.getStringMessage(p, "inventory.manager." + (b ? "enabled" : "disabled"));
	}

	@Override
	public void openInventory(Player p, Object... args){
		Cheat c = (Cheat) args[0];
		Inventory inv = Inventory.builder().withCarrier(new OneCheatHolder(c))
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(c.getName())))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, 1))
				.property(Inv.INV_ID_KEY, Inv.ONE_CHEAT_INV_ID)
				.build(SpongeNegativity.getInstance());
		Utils.fillInventoryWith(Inv.EMPTY, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(0, 0, createItem((ItemType) c.getMaterial(), c.getName()));
		invGrid.set(3, 0, createItem(ItemTypes.DIAMOND, Messages.getStringMessage(p,
				"inventory.manager.setActive", "%active%", getMessage(p, c.isActive()))));
		invGrid.set(4, 0, createItem(ItemTypes.TNT, Messages.getStringMessage(p,
				"inventory.manager.setBack", "%back%", getMessage(p, c.isSetBack()))));
		invGrid.set(5, 0, createItem(ItemTypes.BLAZE_ROD, Messages.getStringMessage(p,
				"inventory.manager.allowKick", "%allow%", getMessage(p, c.allowKick()))));

		invGrid.set(7, 0, createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		invGrid.set(8, 0, createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		Cheat c = (Cheat) args[0];
		Inventory inv = (Inventory) args[1];
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(3, 0, createItem(ItemTypes.DIAMOND, Messages.getStringMessage(p,
				"inventory.manager.setActive", "%active%", getMessage(p, c.isActive()))));
		invGrid.set(4, 0, createItem(ItemTypes.TNT, Messages.getStringMessage(p,
				"inventory.manager.setBack", "%back%", getMessage(p, c.isSetBack()))));
		invGrid.set(5, 0, createItem(ItemTypes.BLAZE_ROD, Messages.getStringMessage(p,
				"inventory.manager.allowKick", "%allow%", getMessage(p, c.allowKick()))));
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, NegativityHolder nh) {
		if (m.equals(ItemTypes.ARROW)) {
			AbstractInventory.open(InventoryType.CHEAT_MANAGER, p, false);
			return;
		}
		Cheat c = ((OneCheatHolder) nh).getCheat();
		if (m.equals(c.getMaterial()))
			return;
		if(m.equals(ItemTypes.TNT))
			c.setBack(!c.isSetBack());
		else if(m.equals(ItemTypes.BLAZE_ROD))
			c.setAllowKick(!c.allowKick());
		else if(m.equals(ItemTypes.DIAMOND))
			c.setActive(!c.isActive());
		
		actualizeInventory(p, c, e.getTargetInventory());
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof OneCheatHolder;
	}
}
