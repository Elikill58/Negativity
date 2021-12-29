package com.elikill58.negativity.common.inventories.admin;

import java.util.HashMap;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.admin.AdminAlertHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.alerts.AlertSender;
import com.elikill58.negativity.universal.alerts.hook.AmountAlertSender;
import com.elikill58.negativity.universal.alerts.hook.InstantAlertSender;
import com.elikill58.negativity.universal.alerts.hook.TimeAlertSender;

public class AdminAlertInventory extends AbstractInventory<AdminAlertHolder> {

	private HashMap<Integer, AlertSender> slotPerAlertShower = new HashMap<>();
	
	public AdminAlertInventory() {
		super(NegativityInventory.ADMIN_ALERT, AdminAlertHolder.class);
		slotPerAlertShower.put(10, new InstantAlertSender());
		slotPerAlertShower.put(11, new TimeAlertSender());
		slotPerAlertShower.put(12, new AmountAlertSender());
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Inventory inv = Inventory.createInventory(Inventory.ADMIN_MENU, 27, new AdminAlertHolder());
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		
		slotPerAlertShower.forEach((slot, shower) -> {
			inv.set(slot, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "inventory.alerts.shower." + shower.getName())).build());
			if(shower.canChangeDefaultValue()) {
				inv.set(slot - 9, ItemBuilder.Builder(Materials.SLIME_BALL).displayName("+").build());
				inv.set(slot + 9, ItemBuilder.Builder(Materials.REDSTONE).displayName("-").build());
			}
		});

		setShowerItem(inv, p, Negativity.getAlertShower());
		
		inv.set(8, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(26, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	private void setShowerItem(Inventory inv, Player p, AlertSender shower) {
		inv.set(17, ItemBuilder.Builder(Materials.EMPTY_MAP).displayName(Messages.getMessage(p, "inventory.alerts.shower." + shower.getName()))
					.lore(ChatColor.GRAY + "Value: " + shower.getShowedValue()).build());
	}
	
	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, AdminAlertHolder nh) {
		if(m.equals(Materials.ARROW)) {
			InventoryManager.open(NegativityInventory.ADMIN, p);
		} else if(m.equals(Materials.PAPER)) {
			int slot = e.getSlot();
			Negativity.setAlertShower(slotPerAlertShower.get(slot));
			setShowerItem(e.getClickedInventory(), p, Negativity.getAlertShower());
		} else if(m.equals(Materials.SLIME_BALL) || m.equals(Materials.REDSTONE)) {
			int slot = e.getSlot();
			int more = slot > 9 ? -1 : 1; // for slot in last line of inv = remove one
			AlertSender shower = Negativity.getAlertShower();
			AlertSender otherShower = slotPerAlertShower.get(slot + (9 * more)); // retrieve good alert shower
			if(!shower.getName().equalsIgnoreCase(otherShower.getName())) { // not same alert shower, so we have to change
				shower = otherShower;
			}
			if(more > 0)
				shower.addOne();
			else
				shower.removeOne();
			setShowerItem(e.getClickedInventory(), p, shower);
		}
	}
}
