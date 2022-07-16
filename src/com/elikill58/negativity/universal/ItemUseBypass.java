package com.elikill58.negativity.universal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;

public class ItemUseBypass {

	public static final ConcurrentHashMap<String, ItemUseBypass> ITEM_BYPASS = new ConcurrentHashMap<>();
	
	public static ConcurrentHashMap<String, ItemUseBypass> getItemBypass() {
		return ITEM_BYPASS;
	}
	
	public static List<String> getItemBypassWithBypass(WhenBypass when){
		List<String> list = new ArrayList<>();
		ITEM_BYPASS.forEach((key, bypass) -> {
			if(bypass.getWhen().equals(when))
				list.add(key);
		});
		return list;
	}
	
	public static void load() {
		ITEM_BYPASS.clear();
		ConfigAdapter config = Adapter.getAdapter().getConfig();
		if (config.contains("items")) {
			ConfigAdapter cs = config.getChild("items");
			for (String s : cs.getKeys())
				new ItemUseBypass(cs.getChild(s), s);
		}
	}
	
	private final String item;
	private final List<String> cheats;
	private final WhenBypass when;
	private final ConfigAdapter config;
	
	public ItemUseBypass(ConfigAdapter config, String key) {
		this.config = config;
		this.item = key.toLowerCase(Locale.ROOT);
		this.when = WhenBypass.getWhenBypass(config.getString("when"));
		this.cheats = Arrays.asList(config.getString("cheats").toLowerCase().split(","));
		if(this.item == null)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow item : " + key);
		else if(this.when == WhenBypass.UNKNOW)
			Adapter.getAdapter().getLogger().error("[Config - Error] Item bypass System - Unknow when : " + config.getString("when"));
		else
			ITEM_BYPASS.put(item, this);
	}
	
	public ConfigAdapter getConfig() {
		return config;
	}
	
	public List<String> getCheats(){
		return cheats;
	}
	
	public boolean isForThisCheat(Cheat c) {
		return cheats.contains(c.getKey().toLowerCase());
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
