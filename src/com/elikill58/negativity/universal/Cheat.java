package com.elikill58.negativity.universal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class Cheat {

	public static Cheat ALL;
	public static final List<Cheat> CHEATS = new ArrayList<>();
	public static final Map<String, Cheat> CHEATS_BY_KEY = new HashMap<>();
	private boolean needPacket, isRunning = false, blockedInFight, hasListener;
	private String key, name;
	private Object m;
	private String[] aliases;

	public Cheat(String key, boolean needPacket, Object m, boolean blockedInFight, boolean hasListener, String... alias) {
		this.needPacket = needPacket;
		this.m = m;
		this.blockedInFight = blockedInFight;
		this.hasListener = hasListener;
		this.key = key.toLowerCase();
		if(key.equalsIgnoreCase("ALL"))
			ALL = this;
		this.name = Adapter.getAdapter().getStringInConfig("cheats." + key.toLowerCase() + ".exact_name");
		this.aliases = alias;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getName() {
		return name;
	}

	public boolean isActive() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + key + ".isActive");
	}
	
	public boolean isBlockedInFight() {
		return blockedInFight;
	}

	public boolean needPacket() {
		return needPacket;
	}

	public Object getMaterial() {
		return m;
	}
	
	public boolean hasListener() {
		return hasListener;
	}
	
	@Deprecated
	public void run() {
		if(isRunning)
			return;
		/*try {
			((BukkitRunnable) protocolClass.newInstance()).runTaskTimer(SpigotNegativity.getInstance(), 20, 20);
			isRunning = true;
		} catch (Exception e) {
		}*/
	}

	public boolean isAutoVerif() {
		/*if (this.equals(ALL))
			return false;
		else*/
		return Adapter.getAdapter().getBooleanInConfig("cheats." + key + ".autoVerif");
	}

	public int getReliabilityAlert() {
		return Adapter.getAdapter().getIntegerInConfig("cheats." + key + ".reliability_alert");
	}

	public boolean isSetBack() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + key + ".setBack");
	}

	public int getAlertToKick() {
		return Adapter.getAdapter().getIntegerInConfig("cheats." + key + ".alert_to_kick");
	}

	public boolean allowKick() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + key + ".kick");
	}

	public boolean setAllowKick(boolean b) {
		Adapter.getAdapter().set("cheats." + key + ".kick", b);
		Adapter.getAdapter().reloadConfig();
		return b;
	}

	public boolean setBack(boolean b) {
		Adapter.getAdapter().set("cheats." + key + ".setBack", b);
		Adapter.getAdapter().reloadConfig();
		//SpigotNegativity.getInstance().reloadConfig();
		return b;
	}

	public boolean setAutoVerif(boolean b) {
		Adapter.getAdapter().set("cheats." + key + ".autoVerif", b);
		Adapter.getAdapter().reloadConfig();
		return b;
	}

	public boolean setActive(boolean active) {
		Adapter.getAdapter().set("cheats." + key + ".isActive", active);
		Adapter.getAdapter().reloadConfig();
		return active;
	}

	public int getMaxAlertPing() {
		return Adapter.getAdapter().getIntegerInConfig("cheats." + key + ".ping");
	}
	
	public String[] getAliases() {
		return aliases;
	}

	public static Optional<Cheat> fromString(String name) {
		for (Cheat c : Cheat.values()) {
			try {
				if (c.getKey().equalsIgnoreCase(name) || c.getName().equalsIgnoreCase(name) || Arrays.asList(c.getAliases()).contains(name))
					return Optional.of(c);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		if(name.equalsIgnoreCase("all"))
			return Optional.of(ALL);
		return Optional.empty();
	}
	
	public static Optional<Cheat> forKey(String key) {
		for (Cheat c : Cheat.values()) {
			try {
				if (c.getKey().equalsIgnoreCase(key))
					return Optional.of(c);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		if(key.equalsIgnoreCase("all"))
			return Optional.of(ALL);
		return Optional.empty();
	}
	
	public static void loadCheat() {
		try {
			String dir = Cheat.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			if (dir.endsWith(".class"))
				dir = dir.substring(0, dir.lastIndexOf('!'));

			if (dir.startsWith("file:/"))
				dir = dir.substring(6);

			for (Object classDir : UniversalUtils.getClasseNamesInPackage(dir, "com.elikill58.negativity." + Adapter.getAdapter().getName() + ".protocols")) {
				try {
					Cheat cheat = (Cheat) Class.forName(classDir.toString().replaceAll(".class", "")).newInstance();
					CHEATS.add(cheat);
					CHEATS_BY_KEY.put(cheat.key, cheat);
				} catch (Exception temp) {
					// on ignore
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Cheat> values() {
		return CHEATS;
	}
}
