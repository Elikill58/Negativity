package com.elikill58.negativity.sponge.inventories.admin;

import static com.elikill58.negativity.sponge.utils.ItemUtils.createItem;

import java.io.IOException;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.inventories.AbstractInventory;
import com.elikill58.negativity.sponge.inventories.holders.LangHolder;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class LangInventory extends AbstractInventory<LangHolder> {

	public LangInventory() {
		super(InventoryType.LANG);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {

		int size = UniversalUtils.getMultipleOf((int) (TranslatedMessages.LANGS.size() * 1.5), 9, 1, 54), nbLine = size / 9;
		Inventory inv = Inventory.builder().withCarrier(new LangHolder())
				.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(Inv.NAME_LANG_MENU)))
				.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
				.property(Inv.INV_ID_KEY, Inv.LANG_INV_ID)
				.build(SpongeNegativity.getInstance());

		GridInventory invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
		
		update(invGrid, p, nbLine);
		invGrid.set(7, nbLine - 1, Inv.EMPTY);

		int slot = 0, line = 0;
		for(String s : TranslatedMessages.LANGS) {
			boolean searchSlot = true;
			while (searchSlot) {
				Optional<Slot> optSlot = invGrid.getSlot(slot, line);
				if(!optSlot.isPresent() || optSlot.get().contains(ItemTypes.AIR)) {
					if(slot > 6)
						invGrid.set(slot, line, Inv.EMPTY);
					else {
						invGrid.set(slot, line, createItem(ItemTypes.PAPER, s));
						searchSlot = false;
					}
				}
				slot++;
				if(slot >= 8) {
					line++;
					slot = 0;
				}
				if(line >= nbLine)
					searchSlot = false;
			}
		}
		
		/*Iterator<Inventory> slots = inv.slots().iterator();
        Iterator<String> langs = TranslatedMessages.LANGS.iterator();
		boolean searchSlot = true;
		int iSlot = 0;
        while (slots.hasNext() && langs.hasNext() && searchSlot) {
        	Inventory slot = slots.next();
        	String lang = langs.next();
			if(slot.size() == 0) {
				if((iSlot + 3) % 9 == 0 || (iSlot + 2) % 9 == 0 || (iSlot + 1) % 9 == 0) // 3 last colums of inventory
					slot.set(Inv.EMPTY);
				else {
					slot.set(Utils.createItem(ItemTypes.PAPER, lang));
					searchSlot = false;
				}
			}
			iSlot++;
        }*/
		
		p.openInventory(inv);
	}
	
	private void update(GridInventory inv, Player p, int nbLine) {
		inv.set(8, 1, createItem(ItemTypes.MAP, Messages.getStringMessage(p, "lang.current", "%lang%", TranslatedMessages.getDefaultLang())));
		inv.set(8, nbLine - 2, createItem(ItemTypes.ARROW, Messages.getStringMessage(p, "inventory.back")));
		inv.set(8, nbLine - 1, createItem(ItemTypes.BARRIER, Messages.getStringMessage(p, "inventory.close")));	
	}

	@Override
	public void manageInventory(ClickInventoryEvent e, ItemType m, Player p, LangHolder nh) {
		if(m.equals(ItemTypes.ARROW)) {
			AbstractInventory.open(InventoryType.ADMIN, p);
		} else if(m.equals(ItemTypes.PAPER)) {
			String lang = "";
			String name = e.getTransactions().get(0).getOriginal().get(Keys.DISPLAY_NAME).orElse(Text.of()).toPlain();
			for(String s : TranslatedMessages.LANGS) {
				if(name.contains(s))
					lang = s;
			}
			if(lang != "") {
				ConfigAdapter config = Adapter.getAdapter().getConfig();
				config.set("Translation.default", lang);
				try {
					config.save();
				} catch (IOException ex) {
					p.sendMessage(Text.of("Failed to save configuration after changing the default language ", lang));
					SpongeNegativity.getInstance().getLogger().error("Failed to save configuration after {} changed the default language to {}", p.getName(), lang, ex);
				}

				TranslatedMessages.DEFAULT_LANG = lang;
				TranslatedMessages.loadMessages();
				update(e.getTargetInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class)),
						p, (int) UniversalUtils.getMultipleOf((int) (TranslatedMessages.LANGS.size() * 1.5), 9, 1, 54) / 9);
			}
		}
	}


	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof LangHolder;
	}
}
