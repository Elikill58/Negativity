package com.elikill58.negativity.common.inventories.hook.players;

import static com.elikill58.negativity.universal.Messages.getMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.colors.DyeColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.players.CheckMenuHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.warn.WarnManager;

public class GlobalPlayerInventory extends AbstractInventory<CheckMenuHolder> {
	
	public GlobalPlayerInventory() {
		super(NegativityInventory.GLOBAL_PLAYER, CheckMenuHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Inventory.createInventory(Inventory.NAME_CHECK_MENU, 36, new CheckMenuHolder(cible));
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		NegativityAccount account = np.getAccount();
		Minerate minerate = account.getMinerate();
		actualizeInventory(p, cible, inv);
		inv.set(0, ItemBuilder.getSkullItem(cible, p));
		
		List<ItemStack> sanctionItems = new ArrayList<>();
		if(!BanManager.getSanctions().isEmpty() && Perm.hasPerm(p, Perm.BAN) && BanManager.banActive && !Perm.hasPerm(np, Perm.BAN))
			sanctionItems.add(ItemBuilder.Builder(Materials.ANVIL).displayName("Ban").build());
		if(!WarnManager.getSanctions().isEmpty() && Perm.hasPerm(p, Perm.WARN) && WarnManager.warnActive && !Perm.hasPerm(np, Perm.WARN))
			sanctionItems.add(ItemBuilder.Builder(Materials.COMPASS).displayName("Warn").build());
		if(!cible.isOp()) // prevent for OP players
			sanctionItems.add(ItemBuilder.Builder(Materials.DIAMOND_SHOVEL).displayName("Kick").build());

		if(sanctionItems.size() == 1)
			inv.set(4, sanctionItems.get(0));
		else if(!sanctionItems.isEmpty()) { // can only be upper than 1
			inv.set(3, sanctionItems.remove(0)); // get first
			inv.set(5, sanctionItems.remove(0)); // get second
			if(!sanctionItems.isEmpty()) // if stay one
				inv.set(4, sanctionItems.remove(0));
		}

		inv.set(8, Inventory.getCloseItem(p));
		inv.set(11, ItemBuilder.Builder(Materials.BONE).displayName(ChatColor.GRAY + "Clear clicks").build());

		inv.set(15, ItemBuilder.Builder(Materials.TNT).displayName(getMessage(p, "inventory.main.active_detection", "%name%", cible.getName())).build());
		inv.set(16, ItemBuilder.Builder(Materials.BOOK).displayName(getMessage(p, "inventory.main.see_proofs", "%name%", cible.getName())).build());
		inv.set(17, ItemBuilder.Builder(Materials.PAPER).displayName(getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())).build());
				
		inv.set(19, ItemBuilder.Builder(Materials.DIAMOND_PICKAXE).displayName("Minerate").lore(minerate.getInventoryLoreString()).build());
		inv.set(20, getWoolItem(p, np.getAccount().isMcLeaks()));
		
		inv.set(27, ItemBuilder.Builder(Materials.SPIDER_EYE).displayName(getMessage(p, "inventory.main.see_inv", "%name%", cible.getName())).build());
		inv.set(28, ItemBuilder.Builder(Materials.EYE_OF_ENDER).displayName(getMessage(p, "inventory.main.teleportation_to", "%name%", cible.getName())).build());
		if(!p.getUniqueId().equals(cible.getUniqueId()) && !(cible.isOp() || Perm.hasPerm(np, Perm.MOD) )) {
			inv.set(29, ItemBuilder.Builder(Materials.PACKED_ICE).displayName(getMessage(p, "inventory.main." + (np.isFreeze ? "un" : "") + "freezing", "%name%", cible.getName())).build());
		}
		
		inv.set(34, ItemBuilder.Builder(Materials.APPLE).displayName(getMessage(p, "inventory.main.active_report", "%name%", cible.getName())).build());
		if(WarnManager.warnActive)
			inv.set(35, ItemBuilder.Builder(Materials.BEACON).displayName(getMessage(p, "inventory.main.active_warn", "%name%", cible.getName())).build());
		p.openInventory(inv);
	}

	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = args.length > 1 ? (Inventory) args[1] : p.getOpenInventory();
		if(inv == null) return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		int betterClick = np.getAccount().getMostClicksPerSecond(), click = np.getClick();
		try {
			inv.set(9, getClickItem(getMessage(p, "inventory.main.last_click", "%clicks%", click), click).build());
			Object[] clickPlaceholders = new Object[] {"%last_clicks%", np.lastClick, "%max_clicks%", betterClick};
			inv.set(10, getClickItem(getMessage(p, "inventory.main.clicks.name", clickPlaceholders), betterClick).lore(getMessage(p, "inventory.main.clicks.lore", clickPlaceholders)).build());

			inv.set(1, ItemBuilder.Builder(Materials.ARROW).displayName(getMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", cible.getPing() + "")).build());
			inv.set(18, ItemBuilder.Builder(Materials.DIAMOND_SWORD).displayName("Fight: " + getMessage(p, "inventory.manager." + (np.isInFight ? "enabled" : "disabled"))).lore(ChatColor.GRAY + "Player sensitivity: " + ChatColor.YELLOW + ((int) np.sensitivity)).build());
			p.updateInventory();
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}
	
	public static ItemBuilder getClickItem(String name, int clicks) {
		if(Materials.LIME_STAINED_CLAY.getId().contains("lime")) {
			return ItemBuilder.Builder(getMaterialFromClick(clicks)).displayName(name);
		} else {
			// we can use all *_STAINED_CLAY because they will be default STAINED_CLAY
			return ItemBuilder.Builder(Materials.LIME_STAINED_CLAY).displayName(name).color(getColorFromClick(clicks));
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
		builder.displayName(getMessage(player, "inventory.main.mcleaks_indicator." + (b ? "positive" : "negative")));
		builder.lore(Collections.singletonList(getMessage(player, "inventory.main.mcleaks_indicator.description")));
		return builder.build();
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, CheckMenuHolder nh) {
		Player cible = nh.getCible();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		if (m.equals(Materials.EYE_OF_ENDER)) {
			p.teleport(cible.getLocation());
			p.closeInventory();
		} else if(m.equals(Materials.SPIDER_EYE)){
			p.openInventory(cible.getInventory());
		} else if(m.equals(Materials.TNT)) {
			InventoryManager.open(NegativityInventory.ACTIVED_CHEAT, p, cible);
		} else if(m.equals(Materials.PACKED_ICE)) {
			p.closeInventory();
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
		} else if(m.equals(Materials.BOOK)) {
			InventoryManager.open(NegativityInventory.SEE_PROOF, p, cible);
		} else if(m.equals(Materials.APPLE)) {
			InventoryManager.open(NegativityInventory.SEE_REPORT, p, cible);
		} else if(m.equals(Materials.BEACON)) {
			InventoryManager.open(NegativityInventory.WARN_SEE, p, cible);
		} else if(m.equals(Materials.COMPASS)) {
			InventoryManager.open(NegativityInventory.WARN, p, cible);
		} else if(m.equals(Materials.DIAMOND_SHOVEL)) {
			InventoryManager.open(NegativityInventory.KICK, p, cible);
		} else if(m.equals(Materials.ANVIL)) {
			InventoryManager.open(NegativityInventory.BAN, p, cible);
		} else if(m.equals(Materials.BONE)) {
			np.lastClick = 0;
			np.clearClick();
			np.getAccount().setMostClicksPerSecond(0);
			Adapter.getAdapter().getAccountManager().save(np.getUUID());
		}
	}
}
