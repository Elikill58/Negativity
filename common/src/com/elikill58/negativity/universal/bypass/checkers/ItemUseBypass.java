package com.elikill58.negativity.universal.bypass.checkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.bypass.BypassChecker;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;

public class ItemUseBypass implements BypassChecker {

	public static final List<ItemUseBypass> CLICK_BYPASS = new ArrayList<>();
	
	public static boolean hasBypassWithClick(Player p, Cheat c, ItemStack item, String actionName) {
		return CLICK_BYPASS.stream().filter((ib) -> ib.isForThisCheat(c) && actionName.toLowerCase(Locale.ROOT).contains(ib.getWhen().name().toLowerCase(Locale.ROOT))).findAny().isPresent();
	}
	
	private final String item;
	private final List<CheatKeys> cheats;
	private final WhenBypass when;
	private final Configuration config;
	
	public ItemUseBypass(String itemName, Configuration config) {
		this.config = config;
		this.item = itemName.toLowerCase(Locale.ROOT);
		this.when = WhenBypass.getWhenBypass(config.getString("when"));
		this.cheats = Arrays.asList(config.getString("cheats").split(",")).stream().map(CheatKeys::fromLowerKey).collect(Collectors.toList());
		if(this.item == null)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow item : " + itemName);
		else if(this.when == WhenBypass.UNKNOW)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow when : " + when);
		else if(this.cheats.size() == 0)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow cheats : " + cheats);
		if(this.when.isClick())
			CLICK_BYPASS.add(this);
	}
	
	public Configuration getConfig() {
		return config;
	}
	
	public List<CheatKeys> getCheats() {
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
		if(!isForThisCheat(c))
			return false;
		if(getWhen().equals(WhenBypass.ALWAYS)) {
			return isItem(p.getItemInHand());
		} else if(getWhen().equals(WhenBypass.BELOW)) {
			Material blockBelow = p.getLocation().clone().sub(0, 1, 0).getBlock().getType();
			return blockBelow.getId().equalsIgnoreCase(item);
		} else if(getWhen().equals(WhenBypass.LOOKING)) {
			List<Block> targetVisual = p.getTargetBlock(7);
			if(!targetVisual.isEmpty()) {
				for(Block b : targetVisual)
					if(b.getType().getId().equalsIgnoreCase(item))
						return true;
			}
		} else if(getWhen().equals(WhenBypass.WEARING)) {
			for(ItemStack armor : p.getInventory().getArmorContent()) {
				if(isItem(armor)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isItem(ItemStack item) {
		if(item == null || !item.getType().getId().equalsIgnoreCase(getItem()))
			return false;
		if(config.contains("name")) {
			if(item.getName() == null || !item.getName().equalsIgnoreCase(config.getString("name"))) // wrong name
				return false;
		}
		if(config.contains("enchants")) {
			for(String enchant : config.getStringList("enchants")) {
				Enchantment en = Enchantment.getByName(enchant);
				if(en == null) {
					Adapter.getAdapter().getLogger().warn("The enchant " + enchant + " isn't known. Please report this.");
					continue;
				}
				if(!item.hasEnchant(en))
					return false;
			}
		}
		return true;
	}
	
	public enum WhenBypass {
		ALWAYS, RIGHT_CLICK(true), LEFT_CLICK(true), LOOKING, BELOW, WEARING, UNKNOW;
		
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
