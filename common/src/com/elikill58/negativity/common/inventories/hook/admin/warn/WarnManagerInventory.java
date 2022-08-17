package com.elikill58.negativity.common.inventories.hook.admin.warn;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.common.inventories.holders.admin.warn.WarnManagerHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.warn.WarnManager;

public class WarnManagerInventory extends AbstractInventory<WarnManagerHolder> {

	public WarnManagerInventory() {
		super(NegativityInventory.WARN_MANAGER, WarnManagerHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Inventory.createInventory(Inventory.BAN_MANAGER_MENU, 27, new WarnManagerHolder());
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);

		inv.set(0, Inventory.EMPTY_RED);
		inv.set(8, Inventory.EMPTY_RED);

		Configuration conf = WarnManager.getWarnConfig();
		Configuration cmdConf = conf.getSection("commands");
		inv.set(9, createItem(p, Materials.BOOK, "state", "%enabled%", Messages.getStateName(p, WarnManager.warnActive),
				"%processor%", WarnManager.getProcessorId()));
		inv.set(11, createItem(p, Materials.COMPASS, WarnManager.warnActive, "on", "off"));
		inv.set(12, createItem(p, cmdConf.getBoolean("warn", false), Inventory.DYE_GREEN, Inventory.DYE_GRAY,
				"command.warn"));
		inv.set(13, createItem(p, Materials.NAME_TAG, "processor"));
		inv.set(17, Inventory.getCloseItem(p));

		inv.set(18, Inventory.EMPTY_RED);
		inv.set(26, Inventory.EMPTY_RED);
		p.openInventory(inv);
	}

	private ItemStack createItem(Player p, Material type, String itemName, Object... placeholders) {
		return ItemBuilder.Builder(type)
				.displayName(Messages.getMessage(p, "inventory.warns." + itemName + ".name", placeholders))
				.lore(Messages.getMessageList(p, "inventory.warns." + itemName + ".lore", placeholders)).build();
	}

	private ItemStack createItem(Player p, Material type, boolean b, String on, String off, Object... placeholders) {
		String beginDir = "inventory.warns." + (b ? on : off);
		ItemBuilder builder = ItemBuilder.Builder(type)
				.displayName(Messages.getMessage(p, beginDir + ".name", placeholders))
				.lore(Messages.getMessageList(p, beginDir + ".lore", placeholders));
		if (b) {
			builder.enchant(Enchantment.UNBREAKING, 1).itemFlag(ItemFlag.HIDE_ENCHANTS);
		}
		return builder.build();
	}

	private ItemStack createItem(Player p, boolean b, ItemStack on, ItemStack off, String itemDir,
			Object... placeholders) {
		String beginDir = "inventory.warns." + itemDir;
		return ItemBuilder.Builder((b ? on : off).clone())
				.displayName(Messages.getMessage(p, beginDir + ".name", placeholders))
				.lore(Messages.getMessageList(p, beginDir + ".lore", placeholders)).build();
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, WarnManagerHolder nh) {
		if (m.equals(Materials.COMPASS)) {
			WarnManager.setWarnActive(!WarnManager.warnActive);
			WarnManager.getWarnConfig().save();
			openInventory(p); // update inv
		} else if (m.equals(Materials.NAME_TAG)) {
			InventoryManager.open(NegativityInventory.WARN_PROCESSOR_MANAGER, p);
		} else if (e.getSlot() == 12) {
			Configuration conf = WarnManager.getWarnConfig();
			conf.set("commands.warn", !conf.getBoolean("commands.warn", false));
			conf.save();
			openInventory(p); // update inv
		}
	}
}
