package com.elikill58.negativity.sponge.inventories;

import java.util.Map.Entry;

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
import com.elikill58.negativity.sponge.inventories.holders.ForgeModsHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import static com.elikill58.negativity.sponge.utils.ItemUtils.createItem;

public class ForgeModsInventory extends AbstractInventory {

	public ForgeModsInventory() {
		super(InventoryType.FORGE_MODS);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ForgeModsHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer((Player) args[0]);
		int size = UniversalUtils.getMultipleOf(np.MODS.size() + 1, 9, 1, 54), nbLine = size / 9;
		Inventory inv = Inventory.builder().withCarrier(new ForgeModsHolder())
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_FORGE_MOD_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(Inv.INV_ID_KEY, Inv.FORGE_MODS_INV_ID)
				.build(SpongeNegativity.getInstance());
		Utils.fillInventoryWith(Inv.EMPTY, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		if (np.MODS.size() == 0) {
			invGrid.set(4, 0, createItem(ItemTypes.DIAMOND, "&rNo mods"));
		} else {
			int x = 0, y = 0;
			for (Entry<String, String> entry : np.MODS.entrySet()) {
				invGrid.set(x, y, createItem(ItemTypes.GRASS, "&r" + entry.getKey(), "&7Version: " + entry.getValue()));
				x++;
				if (x > 8) {
					x = 0;
					y++;
				}
			}
		}
		invGrid.set(8, nbLine - 1, createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, NegativityHolder nh) {
		
	}
}
