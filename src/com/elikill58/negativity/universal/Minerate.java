package com.elikill58.negativity.universal;

import java.util.HashMap;

import com.elikill58.negativity.universal.adapter.Adapter;

public class Minerate {

	private HashMap<MinerateType, Integer> mined = new HashMap<>();
	private int fullMined = 0;

	public Minerate() {
		for(MinerateType type : MinerateType.values())
			mined.put(type, 0);
	}
	
	public void setMine(MinerateType type, int value) {
		mined.put(type, value);
	}

	public void addMine(MinerateType type, Object player) {
		fullMined++;
		if(type == null)
			return;
		mined.put(type, mined.get(type) + 1);
		int minedType = 0;
		for(int i : mined.values())
			minedType += i;
		int relia = minedType / fullMined;
		Adapter.getAdapter().alertMod(relia > 80 ? ReportType.VIOLATION : ReportType.WARNING, player, Cheat.forKey(CheatKeys.XRAY), relia, type.getOreName() + " mined. Full mined: " + fullMined + ". Mined by type: " + toString(), type.getName() + " mined: " + mined.get(type));
	}

	public Integer getMinerateType(MinerateType type) {
		return mined.get(type);
	}

	public String[] getInventoryLoreString() {
		String[] s = new String[MinerateType.values().length + 1];
		s[0] = "&r&7" + "Full Mined: " + fullMined;
		int i = 1;
		for(MinerateType type : MinerateType.values())
			s[i++] = "&r&7" + type.getName() + ": " + (mined.get(type) / (fullMined == 0 ? 1 : fullMined)) * 100 + "% (" + mined.get(type) + ")";
		return s;
	}
	
	public String toString() {
		String s = "";
		for(MinerateType m : mined.keySet()) {
			if(s.equalsIgnoreCase(""))
				s = m.getName() + " (" + m.getOreName() + ") : " + mined.get(m);
			else
				s = s + ", " + m.getName() + " (" + m.getOreName() + ") : " + mined.get(m);
		}
		return s;
	}

	public enum MinerateType {
		DIAMOND("Diamond", "DIAMOND_ORE", "minecraft:diamond_ore"),
		GOLD("Gold", "GOLD_ORE", "minecraft:gold_ore"),
		IRON("Iron", "IRON_ORE", "minecraft:iron_ore"),
		COAL("Coal", "COAL_ORE", "minecraft:coal_ore");

		private final String name;
		private final String oreName;
		private final String mcId;

		MinerateType(String name, String oreName, String mcId) {
			this.name = name;
			this.oreName = oreName;
			this.mcId = mcId;
		}

		public String getName() {
			return name;
		}

		public String getOreName() {
			return oreName;
		}

		public String getMcId() {
			return mcId;
		}

		public static MinerateType getMinerateType(String s) {
			for(MinerateType type : MinerateType.values())
				if(type.name().equalsIgnoreCase(s) || type.getOreName().equalsIgnoreCase(s) || type.getMcId().equalsIgnoreCase(s))
					return type;
			return null;
		}

		public static MinerateType fromId(String id) {
			for (MinerateType type : MinerateType.values()) {
				if (type.getMcId().equalsIgnoreCase(id)) {
					return type;
				}
			}
			return null;
		}
	}
}
