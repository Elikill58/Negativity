package com.elikill58.negativity.api.entity;

public enum EntityType {

	ARMOR_STAND("ArmorStand"),
	ARROW("Arrow"),
	BAT("Bat"),
	BEE("Bee"),
	BLAZE("Blaze"),
	BOAT("Boat"),
	CAVE_SPIDER("CaveSpider"),
	CHICKEN("Chicken"),
	COD("Cod"),
	COMPLEX_PART("Complex"),
	COW("Cow"),
	CREEPER("Creeper"),
	DOLPHIN("Dolphin"),
	DROWNED("Drowned"),
	DROPPED_ITEM("Item"),
	DRAGON_FIREBALL("DragonFireball"),
	EGG("Egg"),
	ENDERMAN("Enderman"),
	ENDERMITE("Endermite"),
	ENDER_CRYSTAL("EnderCrystal"),
	ENDER_DRAGON("EnderDragon"),
	ENDER_PEARL("ThrownEnderpearl"),
	ENDER_SIGNAL("EyeOfEnderSignal"),
	EVOKER("Evoker"),
	EVOKER_FANGS("EvokerFangs"),
	EXPERIENCE_ORB("XPOrb"),
	FALLING_BLOCK("FallingSand"),
	FIREBALL("Fireball"),
	FIREWORK("FireworksRocketEntity"),
	FISHING_HOOK("FishingHook"),
	GIANT("Giant"),
	GHAST("Ghast"),
	GUARDIAN("Guardian"),
	HORSE("EntityHorse"),
	ILLUSIONER("Illusioner"),
	IRON_GOLEM("VillagerGolem"),
	ITEM_FRAME("ItemFrame"),
	LEASH_HITCH("LeashKnot"),
	LIGHTNING("Lightning"),
	MAGMA_CUBE("LavaSlime"),
	MINECART("MinecartRideable"),
	MINECART_COMMAND("MinecartCommandBlock"),
	MINECART_CHEST("MinecartChest"),
	MINECART_FURNACE("MinecartFurnace"),
	MINECART_HOPPER("MinecartHopper"),
	MINECART_MOB_SPAWNER("MinecartMobSpawner"),
	MINECART_TNT("MinecartTNT"),
	MUSHROOM_COW("MushroomCow"),
	OCELOT("Ozelot"),
	PAINTING("Painting"),
	PLAYER("Player"),
	PRIMED_TNT("PrimedTnt"),
	PIG("Pig"),
	PIGLIN("Piglin"),
	PIG_ZOMBIE("PigZombie"),
	RABBIT("Rabbit"),
	SALMON("Salmon"),
	SHEEP("Sheep"),
	SILVERFISH("Silverfish"),
	SKELETON("Skeleton"),
	SLIME("Slime"),
	SMALL_FIREBALL("SmallFireball"),
	SNOWBALL("Snowball"),
	SNOWMAN("SnowMan"),
	SPIDER("Spider"),
	SPLASH_POTION("Potion"),
	SPECTRAL_ARROW("SpectralArrow"),
	STRAY("Stray"),
	STRIDER("Strider"),
	SQUID("Squid"),
	SHULKER("Shulker"),
	SHULKER_BULLET("ShulkerBullet"),
	THROWN_EXP_BOTTLE("ThrownExpBottle"),
	TURTLE("Turtle"),
	UNKNOWN("Unknow"),
	VEX("Vex"),
	VINDICATOR("Vindicator"),
	VILLAGER("Villager"),
	WITHER("WitherBoss"),
	WITHER_SKULL("WitherSkull"),
	WITHER_SKELETON("WitherSkeleton"),
	WITCH("Witch"),
	WOLF("Wolf"),
	WEATHER("Weather"),
	ZOGLIN("Zoglin"),
	ZOMBIE("Zombie"),
	ZOMBIE_VILLAGER("ZombieVillager");

	private final String name;
	
	EntityType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static EntityType get(String name) {
		for(EntityType gm : EntityType.values())
			if(gm.getName().equalsIgnoreCase(name) || gm.name().equalsIgnoreCase(name))
				return gm;
		return EntityType.UNKNOWN;
	}
}
