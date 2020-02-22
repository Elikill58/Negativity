package com.elikill58.negativity.sponge.inventories;

import java.util.ArrayList;
import java.util.List;

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
import com.elikill58.negativity.sponge.inventories.holders.AlertHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;

public class AlertInventory extends AbstractInventory {

	public AlertInventory() {
		super(InventoryType.ALERT);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		ConfigAdapter config = Adapter.getAdapter().getConfig();
		for (Cheat c : Cheat.values())
			if ((config.getBoolean("inventory.alerts.only_cheat_active")
					&& np.hasDetectionActive(c))
					|| (!np.hasDetectionActive(c)
							&& config.getBoolean("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		int size = Utils.getMultipleOf(TO_SEE.size() + 4, 9, 1), nbLine = size / 9;
		Inventory inv = Inventory.builder().withCarrier(new AlertHolder())
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_ALERT_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(Inv.INV_ID_KEY, Inv.ALERT_INV_ID)
				.build(SpongeNegativity.INSTANCE);
		Utils.fillInventoryWith(Inv.EMPTY, inv);
		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		int i = 0;
		for (Cheat c : TO_SEE) {
			ItemType itemType = (ItemType) c.getMaterial();
			int alertCount = Math.min(Math.max(np.getWarn(c), 1), itemType.getMaxStackQuantity()); // Avoid quantity overflow
			invGrid.set(SlotIndex.of(i), Utils.hideAttributes(Utils.createItem(itemType,
					Messages.getStringMessage(p, "inventory.alerts.item_name", "%exact_name%",
					c.getName(), "%warn%", String.valueOf(np.getWarn(c))),
					alertCount)));
			i++;
		}

		int lastRow = invGrid.getRows() - 1;
		invGrid.set(6, lastRow, Utils.createItem(ItemTypes.BONE, "&7Clear"));
		invGrid.set(7, lastRow, Utils.createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		invGrid.set(8, lastRow, Utils.createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));
		p.openInventory(inv);
	}
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = p.getOpenInventory().get();
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		ConfigAdapter config = Adapter.getAdapter().getConfig();
		for (Cheat c : Cheat.values())
			if ((c.isActive() && config.getBoolean("inventory.alerts.only_cheat_active")
					&& np.hasDetectionActive(c))
					|| (!np.hasDetectionActive(c)
							&& config.getBoolean("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		int i = 0;
		for (Cheat c : TO_SEE) {
			if (c.getMaterial() != null) {
				Inventory slot = inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(i)));
				ItemType itemType = (ItemType) c.getMaterial();
				int alertCount = Math.min(Math.max(np.getWarn(c), 1), itemType.getMaxStackQuantity()); // Avoid quantity overflow
				slot.set(Utils.hideAttributes(Utils.createItem(
						itemType, Messages.getStringMessage(p, "inventory.alerts.item_name",
								"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))),
						alertCount)));
			}
			i++;
		}
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, NegativityHolder nh) {
		if (m.equals(ItemTypes.ARROW))
			delayed(() -> AbstractInventory.open(InventoryType.CHECK_MENU, p, Inv.CHECKING.get(p)));
		else if (m.equals(ItemTypes.BONE)) {
			NegativityAccount account = Adapter.getAdapter().getNegativityAccount(Inv.CHECKING.get(p).getUniqueId());
			for (Cheat c : Cheat.values())
				account.setWarnCount(c, 0);
		}
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof AlertHolder;
	}
}
