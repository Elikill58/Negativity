package com.elikill58.negativity.sponge.inventories;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.inventories.holders.ActivedCheatHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class ActivedCheatInventory extends AbstractInventory {

	public ActivedCheatInventory() {
		super(InventoryType.ACTIVED_CHEAT);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		int size = Utils.getMultipleOf(Cheat.values().size() + 3, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder().withCarrier(new ActivedCheatHolder())
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_ACTIVED_CHEAT_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(Inv.INV_ID_KEY, Inv.ACTIVE_CHEAT_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(Inv.EMPTY, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		int i = 0;
		if (np.getActiveCheat().size() > 0) {
			for (Cheat c : np.getActiveCheat()) {
				invGrid.set(SlotIndex.of(i), Utils.hideAttributes(Utils.createItem((ItemType) c.getMaterial(), "&r" + c.getName())));
				i++;
			}
		} else
			invGrid.set(5, 1, Utils.createItem(ItemTypes.REDSTONE_BLOCK,
					Messages.getStringMessage(p, "inventory.detection.no_active", "%name%", cible.getName())));
		int lastRow = invGrid.getRows() - 1;
		invGrid.set(7, lastRow, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		invGrid.set(8, lastRow, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, NegativityHolder nh) {
		if (m.equals(ItemTypes.ARROW))
			AbstractInventory.open(InventoryType.CHECK_MENU, p, Inv.CHECKING.get(p));
	}
	
	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ActivedCheatHolder;
	}
}
