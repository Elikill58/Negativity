package com.elikill58.negativity.common.inventories.hook.admin.ban;

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
import com.elikill58.negativity.common.inventories.holders.admin.BanManagerHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.ban.BanManager;

public class BanManagerInventory extends AbstractInventory<BanManagerHolder> {

	public BanManagerInventory() {
		super(NegativityInventory.BAN_MANAGER, BanManagerHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Inventory.createInventory(Inventory.BAN_MANAGER_MENU, 27, new BanManagerHolder());
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);

		inv.set(0, Inventory.EMPTY_RED);
		inv.set(8, Inventory.EMPTY_RED);

		Configuration conf = BanManager.getBanConfig();
		Configuration cmdConf = BanManager.getBanConfig().getSection("commands");
		inv.set(9,
				createItem(p, Materials.BOOK, "state", "%enabled%", Messages.getStateName(p, BanManager.banActive),
						"%auto%", Messages.getStateName(p, BanManager.autoBan), "%reliability_amount%",
						conf.getInt("reliability_need"), "%alert_amount%", conf.getInt("alert_need"), "%processor%",
						BanManager.getProcessorId()));
		inv.set(10, createItem(p, Materials.ANVIL, BanManager.banActive, "on", "off"));
		inv.set(11, createItem(p, Materials.REDSTONE_LAMP_OFF, BanManager.autoBan, "auto.on", "auto.off"));
		inv.set(12, createItem(p, Materials.COMPASS, "reliability", "%amount%", conf.getInt("reliability_need")));
		inv.set(13, createItem(p, Materials.REDSTONE_TORCH_ON, "alerts", "%amount%", conf.getInt("alert_need")));
		inv.set(14, createItem(p, cmdConf.getBoolean("ban", false), Inventory.DYE_GREEN, Inventory.DYE_GRAY,
				"command.ban"));
		inv.set(15, createItem(p, cmdConf.getBoolean("unban", false), Inventory.DYE_GREEN, Inventory.DYE_GRAY,
				"command.unban"));
		inv.set(16, createItem(p, Materials.NAME_TAG, "processor"));
		inv.set(17, Inventory.getCloseItem(p));

		inv.set(18, Inventory.EMPTY_RED);
		inv.set(26, Inventory.EMPTY_RED);
		p.openInventory(inv);
	}

	private ItemStack createItem(Player p, Material type, String itemName, Object... placeholders) {
		return ItemBuilder.Builder(type)
				.displayName(Messages.getMessage(p, "inventory.bans." + itemName + ".name", placeholders))
				.lore(Messages.getMessageList(p, "inventory.bans." + itemName + ".lore", placeholders)).build();
	}

	private ItemStack createItem(Player p, Material type, boolean b, String on, String off, Object... placeholders) {
		String beginDir = "inventory.bans." + (b ? on : off);
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
		String beginDir = "inventory.bans." + itemDir;
		return ItemBuilder.Builder((b ? on : off).clone())
				.displayName(Messages.getMessage(p, beginDir + ".name", placeholders))
				.lore(Messages.getMessageList(p, beginDir + ".lore", placeholders)).build();
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, BanManagerHolder nh) {
		boolean change = false;
		Configuration conf = BanManager.getBanConfig();
		Configuration cmdConf = conf.getSection("commands");
		if (m.equals(Materials.ANVIL)) {
			BanManager.setBanActive(!BanManager.banActive);
			change = true;
		} else if (m.equals(Materials.REDSTONE_LAMP_OFF)) {
			BanManager.setAutoBan(!BanManager.autoBan);
			change = true;
		} else if (m.equals(Materials.NAME_TAG)) {
			InventoryManager.open(NegativityInventory.BAN_PROCESSOR_MANAGER, p);
		} else if (m.equals(Materials.COMPASS)) {
			conf.set("reliability_need", conf.getInt("reliability_need") + (e.getAction().name().contains("RIGHT") ? 1 : -1));
			change = true;
		} else if (m.equals(Materials.REDSTONE_TORCH_ON)) {
			conf.set("alert_need", conf.getInt("alert_need") + (e.getAction().name().contains("RIGHT") ? 1 : -1));
			change = true;
		} else if(e.getSlot() == 14) {
			cmdConf.set("ban", !cmdConf.getBoolean("ban", false));
			change = true;
		} else if(e.getSlot() == 15) {
			cmdConf.set("unban", !cmdConf.getBoolean("unban", false));
			change = true;
		}
		if (change) {
			conf.save();
			openInventory(p); // update inv
		}
	}
}
