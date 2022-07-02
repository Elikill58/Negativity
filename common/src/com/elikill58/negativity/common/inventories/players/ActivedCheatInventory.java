package com.elikill58.negativity.common.inventories.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.inventories.holders.players.ActivedCheatHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ActivedCheatInventory extends AbstractInventory<ActivedCheatHolder> {

	public ActivedCheatInventory() {
		super(NegativityInventory.ACTIVED_CHEAT, ActivedCheatHolder.class);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Inventory.createInventory(Messages.getMessage(p, "inventory.detection.name_inv"), UniversalUtils.getMultipleOf(Cheat.getEnabledCheat().size() + 3, 9, 1, 54), new ActivedCheatHolder(cible));
		inv.set(inv.getSize() - 2, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
		actualizeInventory(p, args);
	}
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = p.getOpenInventory();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> active = new ArrayList<>(Cheat.getEnabledCheat());
		if (active.size() > 0) {
			int slot = 0;
			active.sort(Comparator.comparing(Cheat::getKey));
			for (Cheat c : active) {
				if (inv.getSize() > slot) {
					List<String> lore = new ArrayList<>();
					if(np.hasDetectionActive(c)) {
						lore.add("&aYou can be detected.");
						Cheat.getCheckManager().getCheckMethodForCheat(c).forEach((check) -> {
							if(c.checkActive(check.getCheck().name())) {
								StringJoiner sj = new StringJoiner(", ");
								for(CheckConditions condition : check.getCheck().conditions()) {
									if(!condition.check(p)) {
										sj.add(condition.getDisplayName());
									}
								}
								lore.add(ChatColor.GRAY + check.getCheck().name() + ": " + (sj.length() == 0 ? "&aActive." : "&cDisabled, Should " + sj.toString()));
							}
						});
						if(lore.size() == 1) { // no line added
							lore.add(ChatColor.YELLOW + "No check active or available.");
						}
					} else {
						lore.addAll(Arrays.asList("&cCannot be detected.", "&7Reason: &c" + np.getWhyDetectionNotActive(c)));
					}
					inv.set(slot++, ItemBuilder.Builder(c.getMaterial()).displayName(ChatColor.RESET + c.getName()).lore(lore).build());
				}
			}
		} else
			inv.set(4, ItemBuilder.Builder(Materials.REDSTONE_BLOCK).displayName(Messages.getMessage(p, "inventory.detection.no_active", "%name%", cible.getName())).build());
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, ActivedCheatHolder nh) {
		if (m.equals(Materials.ARROW))
			InventoryManager.open(NegativityInventory.CHECK_MENU, p, nh.getCible());
	}
}
