package com.elikill58.negativity.spigot.inventories;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.CheckMenuOfflineHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.Version;

public class CheckMenuOfflineInventory extends AbstractInventory {

	public CheckMenuOfflineInventory() {
		super(InventoryType.CHECK_MENU_OFFLINE);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof CheckMenuOfflineHolder;
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		Inventory inv = Bukkit.createInventory(new CheckMenuOfflineHolder(cible), 18, Inv.NAME_CHECK_MENU);
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		NegativityAccount account = np.getAccount();
		Minerate minerate = account.getMinerate();
		actualizeInventory(p, cible);

		inv.setItem(0, getClickItem(Messages.getMessage(p, "inventory.main.max_click", "%clicks%", String.valueOf(np.getAccount().getMostClicksPerSecond())), np.getAccount().getMostClicksPerSecond()));
		inv.setItem(1, ItemUtils.hideAttributes(createItem(Material.DIAMOND_PICKAXE, "Minerate", minerate.getInventoryLoreString())));
		inv.setItem(2, getWoolItem(p, np.isMcLeaks()));
		inv.setItem(3, createItem(Material.PAPER, Messages.getMessage(p, "inventory.main.see_alerts", "%name%", cible.getName())));

		inv.setItem(8, Utils.createSkull(cible.getName(), 1, cible.getName(), ChatColor.GOLD + "UUID: " + cible.getUniqueId(), ChatColor.GREEN + "Version: " + np.getPlayerVersion().getName(), ChatColor.GREEN + "Platform: " + (np.isBedrockPlayer() ? "Bedrock" : "Java")));

		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, Inv.EMPTY);
		inv.setItem(inv.getSize() - 1, createItem(ItemUtils.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
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

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		CheckMenuOfflineHolder cm = (CheckMenuOfflineHolder) nh;
		OfflinePlayer cible = cm.getCible();
		switch (m) {
		case TNT:
			AbstractInventory.open(InventoryType.ACTIVED_CHEAT, p, cible);
			break;
		case PAPER:
			AbstractInventory.open(InventoryType.ALERT, p, cible);
			break;
		case GRASS:
			AbstractInventory.open(InventoryType.FORGE_MODS, p, cible);
			break;
		default:
			break;
		}
	}
}
