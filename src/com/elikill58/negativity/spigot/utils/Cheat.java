package com.elikill58.negativity.spigot.utils;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.protocols.*;
import com.elikill58.negativity.universal.AbstractCheat;
import com.elikill58.negativity.universal.adapter.Adapter;

public enum Cheat implements AbstractCheat {

	FORCEFIELD(true, true, Material.DIAMOND_SWORD, ForceFieldProtocol.class, true, aliases_forcefield),
	FASTPLACE(true, false, Material.DIRT, FastPlaceProtocol.class, false, aliases_fastplace),
	SPEEDHACK(true, false, Material.BEACON, SpeedHackProtocol.class, true, aliases_speedhack),
	AUTOCLICK(true, false, Material.FISHING_ROD, AutoClickProtocol.class, false, aliases_autoclick),
	FLY(true, true, Utils.getMaterialWith1_13_Compatibility("FIREWORK", "LEGACY_FIREWORK"), FlyProtocol.class, true, aliases_fly),
	ANTIPOTION(true, true, Material.POTION, AntiPotionProtocol.class, false, aliases_antipotion),
	AUTOEAT(true, true, Material.COOKED_BEEF, AutoEatProtocol.class, false, aliases_autoeat),
	AUTOREGEN(true, true, Material.GOLDEN_APPLE, AutoRegenProtocol.class, false, aliases_autoregen),
	ANTIKNOCKBACK(true, false, Material.STICK, AntiKnockbackProtocol.class, true, aliases_antiknockback),
	JESUS(true, false, Material.WATER_BUCKET, JesusProtocol.class, false, aliases_jesus),
	NOFALL(true, false, Utils.getMaterialWith1_13_Compatibility("WOOL", "RED_WOOL"), NoFallProtocol.class, false, aliases_nofall),
	BLINK(true, true, Material.COAL_BLOCK, BlinkProtocol.class, false, aliases_blink),
	SPIDER(true, false, Utils.getMaterialWith1_13_Compatibility("WEB", "COBWEB"), SpiderProtocol.class, false, aliases_spider),
	SNEAK(true, true, Material.BLAZE_POWDER, SneakProtocol.class, false, aliases_sneak),
	FASTBOW(true, true, Material.BOW, FastBowProtocol.class, false, aliases_fastbow),
	SCAFFOLD(true, false, Material.GRASS, ScaffoldProtocol.class, false, aliases_scaffold),
	STEP(true, false, Material.BRICK_STAIRS, StepProtocol.class, true, aliases_step),
	NOSLOWDOWN(true, false, Material.SOUL_SAND, NoSlowDownProtocol.class, false, aliases_noslowdown),
	FASTLADDERS(true, false, Material.LADDER, FastLadderProtocol.class, false, aliases_fastladders),
	PHASE(true, false, Utils.getMaterialWith1_13_Compatibility("STAINED_GLASS", "WHITE_STAINED_GLASS"), PhaseProtocol.class, false, aliases_phase),
	AUTOSTEAL(true, false, Material.CHEST, AutoStealProtocol.class, false, aliases_autosteal),
	EDITED_CLIENT(true, false, Material.FEATHER, EditedClientProtocol.class, false, aliases_edited_client),
	ALL(false, true, Material.AIR, null, false, aliases_all);

	private boolean needPacket, isRunning = false, blockedInFight;
	private String name;
	private Material m;
	private Class<?> protocolClass;
	private String[] aliases;

	Cheat(boolean hasName, boolean needPacket, Material m, Class<?> protocolClass, boolean blockedInFight, String... aliases) {
		this.needPacket = needPacket;
		this.m = m;
		this.protocolClass = protocolClass;
		this.blockedInFight = blockedInFight;
		this.name = Adapter.getAdapter().getStringInConfig("cheats." + this.name().toLowerCase() + ".exact_name");
		String[] tempAlias = new String[aliases.length + 1];
		int i = 0;
		for(String s : aliases)
			tempAlias[i++] = s;
		tempAlias[i] = this.name().toLowerCase();
		this.aliases = tempAlias;
	}
	
	@Override
	public String getName() {
		return name;
	}

	public boolean isActive() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + this.name().toLowerCase() + ".isActive");
	}
	
	public boolean isBlockedInFight() {
		return blockedInFight;
	}

	public boolean needPacket() {
		return needPacket;
	}

	public Material getMaterial() {
		return m;
	}

	public Class<?> getProtocolClass() {
		return protocolClass;
	}

	public void run() {
		if(isRunning)
			return;
		try {
			((BukkitRunnable) protocolClass.newInstance()).runTaskTimer(SpigotNegativity.getInstance(), 20, 20);
			isRunning = true;
		} catch (Exception e) {
		}
	}

	public boolean isAutoVerif() {
		if (this.equals(ALL))
			return false;
		else
			return Adapter.getAdapter().getBooleanInConfig("cheats." + this.name().toLowerCase() + ".autoVerif");
	}

	public int getReliabilityAlert() {
		return Adapter.getAdapter().getIntegerInConfig("cheats." + this.name().toLowerCase() + ".reliability_alert");
	}

	public boolean isSetBack() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + this.name().toLowerCase() + ".setBack");
	}

	public int getAlertToKick() {
		return Adapter.getAdapter().getIntegerInConfig("cheats." + this.name().toLowerCase() + ".alert_to_kick");
	}

	public boolean allowKick() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + this.name().toLowerCase() + ".kick");
	}

	public boolean setAllowKick(boolean b) {
		for(Player p : Utils.getOnlinePlayers())
			SpigotNegativity.manageAutoVerif(p);
		Adapter.getAdapter().set("cheats." + this.name().toLowerCase() + ".kick", b);
		SpigotNegativity.getInstance().reloadConfig();
		return b;
	}

	public boolean setBack(boolean b) {
		Adapter.getAdapter().set("cheats." + this.name().toLowerCase() + ".setBack", b);
		SpigotNegativity.getInstance().reloadConfig();
		return b;
	}

	public boolean setAutoVerif(boolean b) {
		Adapter.getAdapter().set("cheats." + this.name().toLowerCase() + ".autoVerif", b);
		SpigotNegativity.getInstance().reloadConfig();
		return b;
	}

	public boolean setActive(boolean active) {
		Adapter.getAdapter().set("cheats." + this.name().toLowerCase() + ".isActive", active);
		SpigotNegativity.getInstance().reloadConfig();
		return active;
	}

	public static Optional<Cheat> getCheatFromString(String name) {
		for (Cheat c : Cheat.values()) {
			try {
				if (c.name().equalsIgnoreCase(name.toLowerCase()) || c.getName().equalsIgnoreCase(name))
					return Optional.of(c);
			} catch (NullPointerException e) {
				System.out.println("NPE: " + e.getMessage() + " FOR Cheat: " + c);
				System.out.println("getName: " + c.getName() + " name: " + c.name());
			}
		}
		return Optional.empty();
	}

	@Override
	public int getMaxAlertPing() {
		return Adapter.getAdapter().getIntegerInConfig("cheats." + this.name().toLowerCase() + ".ping");
	}
	
	public String[] getAliases() {
		return aliases;
	}
}