package com.elikill58.negativity.universal;

import java.util.HashMap;

public class Minerate {

	private HashMap<MinerateType, Integer> mined = new HashMap<>();
	private int fullMined = 0;

	public Minerate() {
		for(MinerateType type : MinerateType.values())
			mined.put(type, 0);
	}

	public void addMine(MinerateType type) {
		fullMined++;
		if(type == null)
			return;
		mined.put(type, mined.get(type) + 1);
	}

	public Integer getMinerateType(MinerateType type) {
		return mined.get(type);
	}

	public String[] getInventoryLoreString() {
		String[] s = new String[MinerateType.values().length + 1];//ChatColor.RESET + "";
		s[0] = "&r&7" + "Full Mined: " + fullMined;
		int i = 1;
		for(MinerateType type : MinerateType.values())
			s[i++] = "&r&7" + type.getName() + ": " + (mined.get(type) / (fullMined == 0 ? 1 : fullMined)) * 100 + "% (" + mined.get(type) + ")";
		return s;
	}

	public static enum MinerateType {
		DIAMOND("Diamond", "DIAMOND_ORE"), GOLD("Gold", "GOLD_ORE"), IRON("Iron", "IRON_ORE"), COAL("Coal", "COAL_ORE");

		private String name, oreName;

		private MinerateType(String name, String oreName) {
			this.name = name;
			this.oreName = oreName;
		}

		public String getName() {
			return name;
		}

		public String getOreName() {
			return oreName;
		}

		public static MinerateType getMinerateType(String s) {
			for(MinerateType type : MinerateType.values())
				if(type.name().equalsIgnoreCase(s) || type.getOreName().equalsIgnoreCase(s))
					return type;
			return null;
		}
	}
}
