package com.elikill58.negativity.universal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.item.ItemType;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerificationManager;

public abstract class Cheat {

	public static final List<Cheat> CHEATS = new ArrayList<>();
	public static final Map<String, Cheat> CHEATS_BY_KEY = new HashMap<>();
	private boolean needPacket, hasListener;
	private CheatCategory cheatCategory;
	private String key;
	private Material m;
	private String[] aliases;

	public Cheat(String key, boolean needPacket, Material m, CheatCategory type, boolean hasListener, String... alias) {
		this.needPacket = needPacket;
		this.m = m;
		this.cheatCategory = type;
		this.hasListener = hasListener;
		this.key = key.toLowerCase();
		this.aliases = alias;
	}

	// fix degueu to remove error
	public Cheat(String key, boolean needPacket, ItemType m, CheatCategory type, boolean hasListener, String... alias) {
		this.needPacket = needPacket;
		this.m = (Material) m;
		this.cheatCategory = type;
		this.hasListener = hasListener;
		this.key = key.toLowerCase();
		this.aliases = alias;
	}
	
	public String getKey() {
		return key.toUpperCase();
	}
	
	public String getName() {
		return Adapter.getAdapter().getConfig().getString("cheats." + key + ".exact_name");
	}

	public boolean isActive() {
		return Adapter.getAdapter().getConfig().getBoolean("cheats." + key + ".isActive");
	}
	
	public boolean isBlockedInFight() {
		return false;
	}
	
	public CheatCategory getCheatCategory() {
		return cheatCategory;
	}

	public boolean needPacket() {
		return needPacket;
	}

	public Material getMaterial() {
		return m;
	}
	
	public boolean hasListener() {
		return hasListener;
	}

	public int getReliabilityAlert() {
		return Adapter.getAdapter().getConfig().getInt("cheats." + key + ".reliability_alert");
	}

	public boolean isSetBack() {
		return Adapter.getAdapter().getConfig().getBoolean("cheats." + key + ".setBack");
	}

	public int getAlertToKick() {
		return Adapter.getAdapter().getConfig().getInt("cheats." + key + ".alert_to_kick");
	}

	public boolean allowKick() {
		return Adapter.getAdapter().getConfig().getBoolean("cheats." + key + ".kick");
	}

	public boolean setAllowKick(boolean b) {
		Adapter.getAdapter().getConfig().set("cheats." + key + ".kick", b);
		return b;
	}

	public boolean setBack(boolean b) {
		Adapter.getAdapter().getConfig().set("cheats." + key + ".setBack", b);
		return b;
	}

	public boolean setActive(boolean active) {
		Adapter ada = Adapter.getAdapter();
		ada.getConfig().set("cheats." + key + ".isActive", active);
		for(Player players : Adapter.getAdapter().getOnlinePlayers()) {
			if(active)
				NegativityPlayer.getNegativityPlayer(players).startAnalyze(this);
			else
				NegativityPlayer.getNegativityPlayer(players).stopAnalyze(this);
		}
		return active;
	}

	public int getMaxAlertPing() {
		return Adapter.getAdapter().getConfig().getInt("cheats." + key + ".ping");
	}
	
	public String[] getAliases() {
		return aliases;
	}
	
	public void setVerif(boolean verif) {
		Adapter.getAdapter().getConfig().set("cheats." + key + ".check_in_verif", verif);
	}

	public boolean hasVerif() {
		return Adapter.getAdapter().getConfig().getBoolean("cheats." + key + ".check_in_verif");
	}

	public CheatHover hoverMsg(String key, Object... placeholders) {
		return new CheatHover("hover." + this.key + "." + key, placeholders);
	}
	
	public @Nullable String makeVerificationSummary(VerifData data, NegativityPlayer np) { return null; }
	
	public final <T> void recordData(UUID target, VerifData.DataType<T> type, T value) {
		VerificationManager.recordData(target, this, type, value);
	}
	
	public static Cheat fromString(String name) {
		for (Cheat c : Cheat.values()) {
			try {
				if (c.getKey().equalsIgnoreCase(name) || c.getName().equalsIgnoreCase(name) || Arrays.asList(c.getAliases()).contains(name))
					return c;
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Cheat forKey(String key) {
		return CHEATS_BY_KEY.get(key.toLowerCase());
	}
	
	public static void loadCheat() {
		CHEATS.clear();
		CHEATS_BY_KEY.clear();
		try {
			String dir = Cheat.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceAll("%20", " ");
			if (dir.endsWith(".class"))
				dir = dir.substring(0, dir.lastIndexOf('!'));

			if (dir.startsWith("file:/"))
				dir = dir.substring(UniversalUtils.getOs() == UniversalUtils.OS.LINUX ? 5 : 6);

			for (Object classDir : UniversalUtils.getClasseNamesInPackage(dir, "com.elikill58.negativity.common.protocols")) {
				try {
					Cheat cheat = (Cheat) Class.forName(classDir.toString().replaceAll(".class", "")).newInstance();
					if(cheat.hasListener())
						EventManager.registerEvent((Listeners) cheat);
					CHEATS.add(cheat);
					CHEATS_BY_KEY.put(cheat.key, cheat);
				} catch (Exception temp) {
					// on ignore
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Adapter.getAdapter().getLogger().info("Loaded " + CHEATS.size() + " cheats.");
	}

	public static List<Cheat> values() {
		return CHEATS;
	}

	public static enum CheatCategory {
		COMBAT, MOVEMENT, WORLD, PLAYER;
	}
	
	public static class CheatHover {
		private final String key;
		private final Object[] placeholders;
		
		public CheatHover(String key, Object... placeholders) {
			this.key = Objects.requireNonNull(key);
			this.placeholders = placeholders;
		}

		public String getKey() {
			return key;
		}

		public Object[] getPlaceholders() {
			return placeholders;
		}
		
		public String compile() {
			return TranslatedMessages.getStringFromLang(TranslatedMessages.DEFAULT_LANG, getKey(), getPlaceholders());
		}
		
		public String compile(NegativityPlayer np) {
			return TranslatedMessages.getStringFromLang(np.getAccount().getLang(), getKey(), getPlaceholders());
		}

		public static class Literal extends CheatHover {
			public Literal(String text) {
				super(text);
			}

			@Override
			public String compile() {
				return getKey();
			}

			@Override
			public String compile(NegativityPlayer np) {
				return getKey();
			}
		}
	}
}
