package com.elikill58.negativity.common.inventories.negativity.players;

import java.util.Collections;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.colors.DyeColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.negativity.players.CheckMenuHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.permissions.Perm;

public class CheckMenuInventory extends AbstractInventory<CheckMenuHolder> {
	
	public CheckMenuInventory() {
		super(NegativityInventory.CHECK_MENU, CheckMenuHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Inventory.createInventory(Inventory.NAME_CHECK_MENU, 27, new CheckMenuHolder(cible));
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		NegativityAccount account = np.getAccount();
		Minerate minerate = account.getMinerate();
		actualizeInventory(p, cible, inv);
		
		inv.set(8, ItemBuilder.getSkullItem(cible));
				
		inv.set(10, ItemBuilder.Builder(Materials.DIAMOND_PICKAXE).displayName("Minerate").lore(minerate.getInventoryLoreString()).build());
		inv.set(11, ItemBuilder.Builder(Materials.GRASS).displayName(ChatColor.RESET + "Mods").lore(ChatColor.GRAY + "Launcher: " + ChatColor.YELLOW + np.getClientName(), ChatColor.GRAY + "Forge: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))).build());
		inv.set(12, getWoolItem(p, np.getAccount().isMcLeaks()));
		// TODO add again fake players
		//inv.set(13, ItemBuilder.Builder(Materials.SKELETON_SKULL).displayName(Messages.getMessage(p, "fake_entities")).build());
		
		if(!BanManager.getSanctions().isEmpty() && Perm.hasPerm(p, Perm.BAN) && BanManager.banActive) {
			inv.set(16, ItemBuilder.Builder(Materials.DIAMOND_SHOVEL).displayName("Kick").build());
			inv.set(17, ItemBuilder.Builder(Materials.ANVIL).displayName("Ban").build());
		} else {
			inv.set(17, ItemBuilder.Builder(Materials.DIAMOND_SHOVEL).displayName("Kick").build());
		}

		inv.set(18, ItemBuilder.Builder(Materials.SPIDER_EYE).displayName(Messages.getMessage(p, "inventory.main.see_inv", "%name%", cible.getName())).build());
		inv.set(19, ItemBuilder.Builder(Materials.EYE_OF_ENDER).displayName(Messages.getMessage(p, "inventory.main.teleportation_to", "%name%", cible.getName())).build());
		if(!p.getUniqueId().equals(cible.getUniqueId())) {
			inv.set(20, ItemBuilder.Builder(Materials.PACKED_ICE).displayName(Messages.getMessage(p, "inventory.main." + (np.isFreeze ? "un" : "") + "freezing", "%name%", cible.getName())).build());
		}
		inv.set(21, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())).build());
		inv.set(22, ItemBuilder.Builder(Materials.TNT).displayName(Messages.getMessage(p, "inventory.main.active_detection", "%name%", cible.getName())).build());
		inv.set(23, ItemBuilder.Builder(Materials.APPLE).displayName(Messages.getMessage(p, "inventory.main.active_report", "%name%", cible.getName())).build());
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = args.length > 1 ? (Inventory) args[1] : p.getOpenInventory();
		if(inv == null || !inv.getType().equals(InventoryType.CHEST)) return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		int betterClick = np.getAccount().getMostClicksPerSecond();
		try {
			inv.set(0, getClickItem(Messages.getMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), np.ACTUAL_CLICK));
			inv.set(1, getClickItem(Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(betterClick)), betterClick));
			inv.set(2, getClickItem(Messages.getMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), np.LAST_CLICK));

			inv.set(7, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", cible.getPing() + "")).build());
			inv.set(9, ItemBuilder.Builder(Materials.DIAMOND_SWORD).displayName("Fight: " + Messages.getMessage(p, "inventory.manager." + (np.isInFight ? "enabled" : "disabled"))).lore(ChatColor.GRAY + "Player sensitivity: " + ChatColor.YELLOW + ((int) np.sensitivity)).build());
			p.updateInventory();
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}
	
	public static ItemStack getClickItem(String name, int clicks) {
		if(Materials.LIME_STAINED_CLAY.getId().contains("lime")) {
			return ItemBuilder.Builder(getMaterialFromClick(clicks)).displayName(name).build();
		} else {
			// we can use all *_STAINED_CLAY because they will be default STAINED_CLAY
			return ItemBuilder.Builder(Materials.LIME_STAINED_CLAY).displayName(name).color(getColorFromClick(clicks)).build();
		}
	}

	public static Material getMaterialFromClick(int click) {
		if (click > 25)
			return Materials.RED_STAINED_CLAY;
		else if (click < 25 && click > 15)
			return Materials.ORANGE_STAINED_CLAY;
		else
			return Materials.LIME_STAINED_CLAY;
	}

	public static DyeColor getColorFromClick(int click) {
		if (click > 25)
			return DyeColor.RED;
		else if (click < 25 && click > 15)
			return DyeColor.YELLOW;
		else
			return DyeColor.LIME;
	}
	
	public static ItemStack getWoolItem(Player player, boolean b) {
		Material type = (b ? Materials.RED_WOOL : Materials.LIME_WOOL);
		ItemBuilder builder = ItemBuilder.Builder(type);
		if(!type.getId().contains("_"))
			builder.color(b ? DyeColor.RED : DyeColor.LIME);
		builder.displayName(Messages.getMessage(player, "inventory.main.mcleaks_indicator." + (b ? "positive" : "negative")));
		builder.lore(Collections.singletonList(Messages.getMessage(player, "inventory.main.mcleaks_indicator.description")));
		return builder.build();
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, CheckMenuHolder nh) {
		Player cible = nh.getCible();
		if (m.equals(Materials.EYE_OF_ENDER)) {
			p.teleport(cible.getLocation());
			p.closeInventory();
		} else if (m.equals(Materials.SKELETON_SKULL)) {
			if(e.getSlot() == 12) {
				p.closeInventory();
				NegativityPlayer.getNegativityPlayer(cible).makeAppearEntities();
			}
		} else if(m.equals(Materials.SPIDER_EYE)){
			p.openInventory(cible.getInventory());
		} else if(m.equals(Materials.TNT)) {
			InventoryManager.open(NegativityInventory.ACTIVED_CHEAT, p, cible);
		} else if(m.equals(Materials.PACKED_ICE)) {
			p.closeInventory();
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
			np.isFreeze = !np.isFreeze;
			if (np.isFreeze) {
				if (Adapter.getAdapter().getConfig().getBoolean("inventory.inv_freeze_active"))
					InventoryManager.open(NegativityInventory.FREEZE, cible);
				Messages.sendMessage(cible, "inventory.main.freeze", "%name%", p.getName());
			} else
				Messages.sendMessage(cible, "inventory.main.unfreeze", "%name%", p.getName());
		} else if(m.equals(Materials.PAPER)) {
			InventoryManager.open(NegativityInventory.ALERT, p, cible);
		} else if(m.equals(Materials.GRASS)) {
			InventoryManager.open(NegativityInventory.FORGE_MODS, p, cible);
		} else if(m.equals(Materials.APPLE)) {
			InventoryManager.open(NegativityInventory.SEE_REPORT, p, cible);
		} else if(m.equals(Materials.DIAMOND_SHOVEL)) {
			InventoryManager.open(NegativityInventory.KICK, p, cible);
		} else if(m.equals(Materials.ANVIL)) {
			InventoryManager.open(NegativityInventory.BAN, p, cible);
		}
	}
}
