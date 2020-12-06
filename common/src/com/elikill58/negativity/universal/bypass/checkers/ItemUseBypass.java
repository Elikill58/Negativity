package com.elikill58.negativity.universal.bypass.checkers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.bypass.BypassChecker;

public class ItemUseBypass implements BypassChecker {

	public static final List<ItemUseBypass> CLICK_BYPASS = new ArrayList<>();
	
	public static boolean hasBypassWithClick(Player p, Cheat c, ItemStack item, String actionName) {
		return CLICK_BYPASS.stream().filter((ib) -> ib.isForThisCheat(c) && actionName.toLowerCase().contains(ib.getWhen().name().toLowerCase())).findAny().isPresent();
	}
	
	private String item;
	private Set<String> cheats;
	private WhenBypass when;
	
	public ItemUseBypass(String itemName, String cheats, String when) {
		this.item = itemName.toLowerCase();
		this.when = WhenBypass.getWhenBypass(when);
		this.cheats = updateCheats(cheats);
		if(this.item == null)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow item : " + itemName);
		else if(this.when == WhenBypass.UNKNOW)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow when : " + when);
		else if(this.cheats.size() == 0)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow cheats : " + cheats);
		if(this.when.isClick())
			CLICK_BYPASS.add(this);
	}
	
	private Set<String> updateCheats(String cheats){
		Set<String> keys = new HashSet<>();
		Set<String> allCheatKeys = Cheat.getCheatKeys();
		for(String cheat : cheats.split(","))
			for (String knownCheat : allCheatKeys)
				if(knownCheat.equalsIgnoreCase(cheat))
					keys.add(knownCheat);
		return keys;
	}
	
	public Set<String> getCheats(){
		return cheats;
	}
	
	public boolean isForThisCheat(Cheat c) {
		return cheats.contains(c.getKey());
	}
	
	public String getItem() {
		return item;
	}
	
	public WhenBypass getWhen() {
		return when;
	}
	
	@Override
	public boolean hasBypass(Player p, Cheat c) {

		ItemStack itemInHand = p.getItemInHand();
		if(getWhen().equals(WhenBypass.ALWAYS)) {
			if(itemInHand != null && itemInHand.getType().getId().equalsIgnoreCase(item)) {
				return false;
			}
		} else if(getWhen().equals(WhenBypass.BELOW)) {
			Material blockBelow = p.getLocation().clone().sub(0, 1, 0).getBlock().getType();
			if(blockBelow.getId().equalsIgnoreCase(item)) {
				return false;
			}
		} else if(getWhen().equals(WhenBypass.LOOKING)) {
			List<Block> targetVisual = p.getTargetBlock(7);
			if(!targetVisual.isEmpty()) {
				for(Block b : targetVisual)
					if(b.getType().getId().equalsIgnoreCase(item))
						return false;
			}
		}
		return false;
	}
	
	public enum WhenBypass {
		ALWAYS, RIGHT_CLICK(true), LEFT_CLICK(true), LOOKING, BELOW, UNKNOW;
		
		private boolean isClick = false;
		
		WhenBypass() {}
		
		WhenBypass(boolean isClick) {
			this.isClick = isClick;
		}
		
		public static WhenBypass getWhenBypass(String when) {
			for(WhenBypass wb : WhenBypass.values())
				if(wb.name().equalsIgnoreCase(when))
					return wb;
			return UNKNOW;
		}
		
		public boolean isClick() {
			return isClick;
		}
	}
}
