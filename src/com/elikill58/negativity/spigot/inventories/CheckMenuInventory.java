package com.elikill58.negativity.spigot.inventories;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.CheckMenuHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class CheckMenuInventory extends AbstractInventory {
	
	public CheckMenuInventory() {
		super(InventoryType.CHECK_MENU);
	}
	
	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof CheckMenuHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Bukkit.createInventory(new CheckMenuHolder(cible), 27, Inv.NAME_CHECK_MENU);
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		NegativityAccount account = np.getAccount();
		Minerate minerate = account.getMinerate();
		actualizeInventory(p, cible, inv);
		
		inv.setItem(8, Utils.createSkull(cible.getName(), 1, cible.getName(), ChatColor.GOLD + "UUID: " + cible.getUniqueId(), ChatColor.GREEN + "Version: " + np.getPlayerVersion().getName(), ChatColor.GREEN + "Platform: " + (np.isBedrockPlayer() ? "Bedrock" : "Java")));

		inv.setItem(10, ItemUtils.hideAttributes(createItem(Material.DIAMOND_PICKAXE, "Minerate", minerate.getInventoryLoreString())));
		inv.setItem(11, createItem(ItemUtils.GRASS, ChatColor.RESET + "Mods", ChatColor.GRAY + "Launcher: " + ChatColor.YELLOW + np.clientName, ChatColor.GRAY + "Forge: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled"))));
		inv.setItem(12, getWoolItem(p, np.isMcLeaks()));
		inv.setItem(13, createItem(ItemUtils.SKELETON_SKULL, Messages.getMessage(p, "fake_entities")));
		//inv.setItem(16, Utils.createItem(Utils.getMaterialWith1_13_Compatibility("DIAMOND_SPADE", "LEGACY_DIAMOND_SPADE"), "Kick"));
		//inv.setItem(17, Utils.createItem(Material.ANVIL, "Ban"));
		if(!Perm.hasPerm(np, Perm.MOD))
			inv.setItem(18, createItem(Material.SPIDER_EYE, Messages.getMessage(p, "inventory.main.see_inv", "%name%", cible.getName())));
		inv.setItem(19, createItem(ItemUtils.EYE_OF_ENDER, Messages.getMessage(p, "inventory.main.teleportation_to", "%name%", cible.getName())));
		if(!p.getUniqueId().equals(cible.getUniqueId()) && !(Perm.hasPerm(np, Perm.MOD) || Perm.hasPerm(np, Perm.ADMIN)))
			inv.setItem(20, createItem(Material.PACKED_ICE, Messages.getMessage(p, "inventory.main." + (np.isFreeze ? "un" : "") + "freezing", "%name%", cible.getName())));
		inv.setItem(21, createItem(Material.PAPER, Messages.getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())));
		inv.setItem(22, createItem(Material.TNT, Messages.getMessage(p, "inventory.main.active_detection", "%name%", cible.getName())));
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, Inv.EMPTY);
		inv.setItem(inv.getSize() - 1, createItem(ItemUtils.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = args.length == 1 ? p.getOpenInventory().getTopInventory() : (Inventory) args[1];
		if(inv == null || !inv.getType().equals(org.bukkit.event.inventory.InventoryType.CHEST)) return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		int betterClick = np.getAccount().getMostClicksPerSecond();
		try {
			inv.setItem(0, getClickItem(Messages.getMessage(p, "inventory.main.actual_click", "%clicks%", String.valueOf(np.ACTUAL_CLICK)), np.ACTUAL_CLICK));
			inv.setItem(1, getClickItem(Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(betterClick)), betterClick));
			inv.setItem(2, getClickItem(Messages.getMessage(p, "inventory.main.last_click", "%clicks%", String.valueOf(np.LAST_CLICK)), np.LAST_CLICK));

			inv.setItem(7, createItem(Material.ARROW, Messages.getMessage(p, "inventory.main.ping", "%name%", cible.getName(), "%ping%", np.ping)));
			inv.setItem(9, ItemUtils.hideAttributes(createItem(Material.DIAMOND_SWORD, "Fight: " + Messages.getMessage(p, "inventory.manager." + (np.MODS.size() > 0 ? "enabled" : "disabled")))));
			p.updateInventory();
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}
	
	private static ItemStack getClickItem(String name, int clicks) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_13)) {
			return createItem(getMaterialFromClick(clicks), name);
		} else {
			// we can use all *_STAINED_CLAY because they will be default STAINED_CLAY
			return createItem(ItemUtils.LIME_STAINED_CLAY, name, 1, getByteFromClick(clicks));
		}
	}

	private static Material getMaterialFromClick(int click) {
		if (click > 25)
			return ItemUtils.RED_STAINED_CLAY;
		else if (click < 25 && click > 15)
			return ItemUtils.ORANGE_STAINED_CLAY;
		else
			return ItemUtils.LIME_STAINED_CLAY;
	}

	private static byte getByteFromClick(int click) {
		if (click > 25)
			return 14;
		else if (click < 25 && click > 15)
			return 4;
		else
			return 5;
	}

	@SuppressWarnings("deprecation")
	private static ItemStack getWoolItem(Player player, boolean b) {
		ItemStack item = new ItemStack(b ? ItemUtils.RED_WOOL : ItemUtils.LIME_WOOL);
		if(item.getType().name().equals("WOOL"))
			item.setDurability((short) (b ? 14 : 5));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Messages.getMessage(player, "inventory.main.mcleaks_indicator." + (b ? "positive" : "negative")));
		meta.setLore(Collections.singletonList(Messages.getMessage(player, "inventory.main.mcleaks_indicator.description")));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		CheckMenuHolder cm = (CheckMenuHolder) nh;
		Player cible = cm.getCible();
		if (m == ItemUtils.EYE_OF_ENDER) {
			p.teleport(cible);
			p.closeInventory();
		} else if (m == ItemUtils.SKELETON_SKULL) {
			if(e.getRawSlot() == 12) {
				p.closeInventory();
				SpigotNegativityPlayer.getNegativityPlayer(cible).makeAppearEntities();
			}
		} else if(m == ItemUtils.DIAMOND_SPADE) {
			// kick
		} else {
			switch (m) {
			case SPIDER_EYE:
				p.openInventory(cible.getInventory());
				break;
			case TNT:
				AbstractInventory.open(InventoryType.ACTIVED_CHEAT, p, cible);
				break;
			case PACKED_ICE:
				p.closeInventory();
				SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
				np.isFreeze = !np.isFreeze;
				if (np.isFreeze) {
					if (Adapter.getAdapter().getConfig().getBoolean("inventory.inv_freeze_active"))
						AbstractInventory.open(InventoryType.FREEZE, cible);
					Messages.sendMessage(cible, "inventory.main.freeze", "%name%", p.getName());
				} else
					Messages.sendMessage(cible, "inventory.main.unfreeze", "%name%", p.getName());
				break;
			case PAPER:
				AbstractInventory.open(InventoryType.ALERT, p, cible);
				break;
			case GRASS:
				AbstractInventory.open(InventoryType.FORGE_MODS, p, cible);
				//ForgeModsInventory.openForgeModsMenu(p, cible);
				break;
			case ANVIL:
				// ban
				break;
			default:
				break;
			}
		}
	}
}
