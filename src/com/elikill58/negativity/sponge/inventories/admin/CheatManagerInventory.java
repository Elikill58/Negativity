package com.elikill58.negativity.sponge.inventories.admin;

import static com.elikill58.negativity.sponge.utils.ItemUtils.createItem;

import java.util.Iterator;

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
import com.elikill58.negativity.sponge.inventories.holders.CheatManagerHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.utils.ItemUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class CheatManagerInventory extends AbstractInventory<CheatManagerHolder> {

	public CheatManagerInventory() {
		super(InventoryType.CHEAT_MANAGER);
	}
	
	@Override
	public void openInventory(Player p, Object... args){
		int size = UniversalUtils.getMultipleOf(Cheat.values().size() + 3, 9, 1, 54), nbLine = size / 9;
		Inventory inv = Inventory.builder().withCarrier(new CheatManagerHolder((boolean) args[0]))
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of("Cheat manager")))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(Inv.INV_ID_KEY, Inv.CHEAT_MANAGER_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(Inv.EMPTY, inv);
		for (Cheat c : Cheat.values())
			if (c.getMaterial() != null)
				inv.offer(ItemUtils.hideAttributes(createItem((ItemType) c.getMaterial(), c.getName())));
		Iterator<Inventory> slots = inv.slots().iterator();
        Iterator<Cheat> cheats = Cheat.values().iterator();
        while (slots.hasNext() && cheats.hasNext()) {
            Cheat cheat = cheats.next();
            slots.next().set(ItemUtils.hideAttributes(createItem((ItemType) cheat.getMaterial(), cheat.getName())));
        }
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		invGrid.set(7, nbLine - 1, createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		invGrid.set(8, nbLine - 1, createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, CheatManagerHolder nh) {
		if (m.equals(ItemTypes.ARROW))
			delayed(() -> AbstractInventory.getInventory(nh.isFromAdmin() ? InventoryType.ADMIN : InventoryType.MOD).ifPresent((inv) -> inv.openInventory(p)));
		else {
			UniversalUtils.getCheatFromItem(m).ifPresent((c) -> delayed(() -> AbstractInventory.open(InventoryType.ONE_CHEAT, p, c)));
		}
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof CheatManagerHolder;
	}
}
