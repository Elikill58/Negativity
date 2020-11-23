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
import com.elikill58.negativity.common.inventories.holders.admin.LangHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class LangInventory extends AbstractInventory<LangHolder> {

	public LangInventory() {
		super(NegativityInventory.LANG, LangHolder.class);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		LangHolder holder = new LangHolder();
		Inventory inv = Inventory.createInventory(Inventory.ADMIN_MENU, UniversalUtils.getMultipleOf((int) (TranslatedMessages.LANGS.size() * 1.5), 9, 1, 54), holder);

		update(inv, p);
		inv.set(inv.getSize() - 3, Inventory.EMPTY);
		
		int slot = 0;
		for(String langKey : TranslatedMessages.LANGS) {
			boolean searchSlot = true;
			while (searchSlot) {
				if(inv.get(slot) == null || inv.get(slot).getType().equals(Materials.AIR)) {
					if((slot + 3) % 9 == 0 || (slot + 2) % 9 == 0 || (slot + 1) % 9 == 0) // 3 last colums of inventory
						inv.set(slot, Inventory.EMPTY);
					else {
						holder.addLang(langKey, slot);
						String name = TranslatedMessages.getStringFromLang(langKey, "lang.name");
						inv.set(slot, ItemBuilder.Builder(Materials.PAPER).displayName(ChatColor.RESET + name).lore(ChatColor.RESET + "" +ChatColor.GRAY + langKey).build());
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
		inv.set(8, ItemBuilder.Builder(Materials.EMPTY_MAP).displayName(Messages.getMessage(p, "lang.current", "%lang%", TranslatedMessages.getDefaultLang())).build());
		inv.set(inv.getSize() - 2, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());	
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, LangHolder nh) {
		if(m.equals(Materials.ARROW)) {
			InventoryManager.open(NegativityInventory.ADMIN, p);
		} else if(m.getId().contains("PAPER")) {
			String lang = nh.getLangBySlot().get(e.getSlot());
			if(lang != null) {
				Adapter.getAdapter().getConfig().set("Translation.default", lang);
				Adapter.getAdapter().getConfig().save();
				TranslatedMessages.DEFAULT_LANG = lang;
				TranslatedMessages.loadMessages();
				update(e.getClickedInventory(), p);
				p.updateInventory();
			}
		}
	}
}
