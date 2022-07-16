package com.elikill58.negativity.universal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.universal.adapter.Adapter;

public class ItemUseBypass {

	public static final HashMap<String, ItemUseBypass> ITEM_BYPASS = new HashMap<>();
	
	public static List<String> getItemBypassWithBypass(WhenBypass when){
		List<String> list = new ArrayList<>();
		ITEM_BYPASS.forEach((key, bypass) -> {
			if(bypass.getWhen().equals(when))
				list.add(key);
		});
		return list;
	}
	
	private String item;
	private List<Cheat> cheats = new ArrayList<>();
	private WhenBypass when;
	
	public ItemUseBypass(String itemName, String cheats, String when) {
		this.item = itemName.toLowerCase(Locale.ROOT);
		this.when = WhenBypass.getWhenBypass(when);
		this.cheats = updateCheats(cheats);
		if(this.item == null)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow item : " + itemName);
		else if(this.when == WhenBypass.UNKNOW)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow when : " + when);
		else if(this.cheats.size() == 0)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow cheats : " + cheats);
		else
			ITEM_BYPASS.put(item, this);
	}
	
	private List<Cheat> updateCheats(String cheats){
		List<Cheat> list = new ArrayList<>();
		for(Cheat ac : Cheat.CHEATS)
			for(String s : cheats.split(","))
				if(ac.getKey().equalsIgnoreCase(s))
					list.add(ac);
		return list;
	}
	
	public List<Cheat> getCheats(){
		return cheats;
	}
	
	public boolean isForThisCheat(Cheat c) {
		return cheats.contains(c);
	}
	
	public String getItem() {
		return item;
	}
	
	public WhenBypass getWhen() {
		return when;
	}
	
	public static enum WhenBypass {
		ALWAYS, RIGHT_CLICK(true), LEFT_CLICK(true), LOOKING, BELOW, WEARING, UNKNOW;
		
		private boolean isClick = false;
		
		private WhenBypass() {}
		
		private WhenBypass(boolean isClick) {
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
