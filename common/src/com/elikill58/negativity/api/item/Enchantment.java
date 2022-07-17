package com.elikill58.negativity.api.item;

public enum Enchantment {

	AQUA_AFFINITY("minecraft:aqua_affinity"),
	BANE_OF_ARTHROPODS("minecraft:bane_of_arthropods"),
	BLAST_PROTECTION("minecraft:blast_protection"),
	CHANNELING("minecraft:channeling"),
	BINDING_CURSE("minecraft:binding_curse"),
	VANISHING_CURSE("minecraft:vanishing_curse"),
	DEPTH_STRIDER("minecraft:depth_strider"),
	EFFICIENCY("minecraft:efficiency", "DIG_SPEED"),
	FEATHER_FALLING("minecraft:feather_falling"),
	FIRE_ASPECT("minecraft:fire_aspect"),
	FIRE_PROTECTION("minecraft:fire_protection"),
	FLAME("minecraft:flame"),
	FORTUNE("minecraft:fortune"),
	FROST_WALKER("minecraft:frost_walker"),
	IMPALING("minecraft:impaling"),
	INFINITY("minecraft:infinity"),
	KNOCKBACK("minecraft:knockback"),
	LOOTING("minecraft:looting"),
	LOYALTY("minecraft:loyalty"),
	LUCK_OF_THE_SEA("minecraft:luck_of_the_sea"),
	LURE("minecraft:lure"),
	MENDING("minecraft:mending"),
	MULTISHOT("minecraft:multishot"),
	PIERCING("minecraft:piercing"),
	POWER("minecraft:power"),
	PROJECTILE_PROTECTION("minecraft:projectile_protection"),
	PROTECTION("minecraft:protection"),
	PUNCH("minecraft:punch"),
	QUICK_CHARGE("minecraft:quick_charge"),
	RESPIRATION("minecraft:respiration"),
	RIPTIDE("minecraft:riptide"),
	SHARPNESS("minecraft:sharpness"),
	SILK_TOUCH("minecraft:silk_touch"),
	SMITE("minecraft:smite"),
	SOUL_SPEED("minecraft:soul_speed"),
	SWEEPING("minecraft:sweeping"),
	SWIFT_SNEAK("minecraft:swift_sneak"),
	THORNS("minecraft:thorns"),
	UNBREAKING("minecraft:unbreaking");
	
	private final String id;
	private final String[] aliases;
	
	Enchantment(String id, String... alias) {
		this.id = id;
		this.aliases = alias;
	}
	
	public String getId() {
		return id;
	}
	
	public String[] getAliases() {
		return aliases;
	}
	
	@Override
	public String toString() {
		return "Enchantment:" + name();
	}
	
	public static Enchantment getByName(String name) {
		for(Enchantment e : values()) {
			if(e.getId().contains(name.toLowerCase()) || e.name().equalsIgnoreCase(name))
				return e;
			for(String alias : e.getAliases())
				if(alias.equalsIgnoreCase(name))
					return e;
		}
		return null;
	}
}
