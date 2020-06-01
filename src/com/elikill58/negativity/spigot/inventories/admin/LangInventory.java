package com.elikill58.negativity.spigot.inventories.admin;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.holders.LangHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class LangInventory extends AbstractInventory {

	public LangInventory() {
		super(InventoryType.LANG);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Inventory inv = Bukkit.createInventory(new LangHolder(), UniversalUtils.getMultipleOf((int) (TranslatedMessages.LANGS.size() * 1.5), 9, 1, 54), Inv.ADMIN_MENU);

		update(inv, p);
		inv.setItem(inv.getSize() - 3, Inv.EMPTY);
		
		int slot = 0;
		for(String s : TranslatedMessages.LANGS) {
			boolean searchSlot = true;
			while (searchSlot) {
				if(inv.getItem(slot) == null || inv.getItem(slot).getType().equals(Material.AIR)) {
					if((slot + 3) % 9 == 0 || (slot + 2) % 9 == 0 || (slot + 1) % 9 == 0) // 3 last colums of inventory
						inv.setItem(slot, Inv.EMPTY);
					else {
						inv.setItem(slot, createItem(ItemUtils.PAPER, s));
						searchSlot = false;
					}
				}
				slot++;
				if(slot >= inv.getSize()) {
					p.openInventory(inv);
					return;
				}
			}
		}
		
		p.openInventory(inv);
	}
	
	private void update(Inventory inv, Player p) {
		inv.setItem(8, createItem(ItemUtils.EMPTY_MAP, Messages.getMessage(p, "lang.current", "%lang%", TranslatedMessages.getDefaultLang())));
		inv.setItem(inv.getSize() - 2, createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1, createItem(ItemUtils.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));	
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if(m.equals(Material.ARROW)) {
			AbstractInventory.open(InventoryType.ADMIN, p);
		} else if(m.name().contains("PAPER")) {
			String lang = "";
			String name = e.getCurrentItem().getItemMeta().getDisplayName();
			for(String s : TranslatedMessages.LANGS) {
				if(name.equalsIgnoreCase(ChatColor.RESET + s))
					lang = s;
			}
			if(lang != "") {
				SpigotNegativity.getInstance().getConfig().set("Translation.default", lang);
				SpigotNegativity.getInstance().saveConfig();
				TranslatedMessages.DEFAULT_LANG = lang;
				TranslatedMessages.loadMessages();
				update(e.getClickedInventory(), p);
				p.updateInventory();
			}
		}
	}


	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof LangHolder;
	}
}
