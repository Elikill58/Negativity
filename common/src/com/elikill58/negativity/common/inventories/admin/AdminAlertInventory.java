package com.elikill58.negativity.common.inventories.admin;

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
import com.elikill58.negativity.universal.alerts.AlertSender;

public class AdminAlertInventory extends AbstractInventory<AdminAlertHolder> {
	
	public AdminAlertInventory() {
		super(NegativityInventory.ADMIN_ALERT, AdminAlertHolder.class);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		AdminAlertHolder holder = new AdminAlertHolder();
		Inventory inv = Inventory.createInventory(Messages.getMessage(p, "inventory.alerts.shower.manage"), 27, holder);
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		
		int slot = 10;
		for(AlertSender sender : AlertSender.getAllAlertSender()) {
			holder.add(slot, sender);
			inv.set(slot, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "inventory.alerts.shower." + sender.getName())).build());
			if(sender.canChangeDefaultValue()) {
				inv.set(slot - 9, ItemBuilder.Builder(Materials.SLIME_BALL).displayName("+").build());
				inv.set(slot + 9, ItemBuilder.Builder(Materials.REDSTONE).displayName("-").build());
			}
			slot++;
		}

		setShowerItem(inv, p, AlertSender.getAlertShower());
		
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
			AlertSender.setAlertShower(nh.getAlertSender(slot));
			setShowerItem(e.getClickedInventory(), p, AlertSender.getAlertShower());
		} else if(m.equals(Materials.SLIME_BALL) || m.equals(Materials.REDSTONE)) {
			int slot = e.getSlot();
			int more = slot > 9 ? -1 : 1; // for slot in last line of inv = remove one
			AlertSender shower = AlertSender.getAlertShower();
			AlertSender otherShower = nh.getAlertSender(slot + (9 * more)); // retrieve good alert shower
			if(!shower.getName().equalsIgnoreCase(otherShower.getName())) { // not same alert shower, so we have to change
				shower = otherShower;
			}
			if(more > 0)
				shower.addOne();
			else
				shower.removeOne();
			shower.save();
			setShowerItem(e.getClickedInventory(), p, shower);
		}
	}
}
