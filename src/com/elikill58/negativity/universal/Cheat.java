package com.elikill58.negativity.universal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.item.ItemType;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.api.yaml.config.YamlConfiguration;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerificationManager;

import net.minecraft.util.com.google.common.collect.Maps;

public abstract class Cheat {

	private static final File MODULE_FOLDER = new File(Adapter.getAdapter().getDataFolder(), "modules");
	public static final List<Cheat> CHEATS = new ArrayList<>();
	private final String key;
	private Configuration config;
	private boolean needPacket, hasListener;
	private CheatCategory cheatCategory;
	private Material m;
	private String[] aliases;

	public Cheat(String key, boolean needPacket, Material m, CheatCategory type, boolean hasListener, String... alias) {
		this.needPacket = needPacket;
		this.m = m;
		this.cheatCategory = type;
		this.hasListener = hasListener;
		this.key = key.toLowerCase();
		this.aliases = alias;
		
		File moduleFile = new File(MODULE_FOLDER, this.key + ".yml");
		if(!moduleFile.exists()) {
			try {
				URI migrationsDirUri = Cheat.class.getResource("/modules").toURI();
				if (migrationsDirUri.getScheme().equals("jar")) {
					try (FileSystem jarFs = FileSystems.newFileSystem(migrationsDirUri, Collections.emptyMap())) {
						Path cheatPath = jarFs.getPath("/modules", this.key + ".yml");
						if(Files.isRegularFile(cheatPath)) {
							Files.copy(cheatPath, Paths.get(moduleFile.toURI()));
						} else {
							Adapter.getAdapter().getLogger().error("Cannot load cheat " + this.key + ": unable to find default config.");
							return;
						}
					}
				}
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		this.config = YamlConfiguration.load(moduleFile);
	}

	// fix degueu to remove error
	public Cheat(String key, boolean needPacket, ItemType m, CheatCategory type, boolean hasListener, String... alias) {
		this(key, needPacket, (Material) m, type, hasListener, alias);
	}
	
	public String getKey() {
		return key.toUpperCase();
	}
	
	public Configuration getConfig() {
		return config;
	}
	
	public void saveConfig() {
		config.save();
	}
	
	public String getName() {
		return config.getString("exact_name", key);
	}

	public boolean isActive() {
		return config.getBoolean("active", true);
	}
	
	public boolean checkActive(String checkName) {
		//if(config.contains("checks." + checkName + ".active"))
			return config.getBoolean("check." + checkName + ".active", true);
		//config.set("checks." + checkName + ".active", true);
		//return true;
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
		return config.getInt("reliability_alert", 60);
	}

	public boolean isSetBack() {
		return config.getBoolean("set_back.active", false);
	}

	public boolean allowKick() {
		return config.getBoolean("kick.active", false);
	}

	public int getAlertToKick() {
		return config.getInt("kick.alert", 5);
	}

	public boolean setAllowKick(boolean b) {
		config.set("kick.active", b);
		return b;
	}

	public boolean setBack(boolean b) {
		config.set("set_back.active", b);
		return b;
	}

	public boolean setActive(boolean active) {
		config.set("active", active);
		for(Player players : Adapter.getAdapter().getOnlinePlayers()) {
			if(active)
				NegativityPlayer.getNegativityPlayer(players).startAnalyze(this);
			else
				NegativityPlayer.getNegativityPlayer(players).stopAnalyze(this);
		}
		return active;
	}

	public int getMaxAlertPing() {
		return config.getInt("ping", 150);
	}
	
	public String[] getAliases() {
		return aliases;
	}
	
	public void setVerif(boolean verif) {
		config.set("verif.check_in_verif", verif);
	}

	public boolean hasVerif() {
		return config.getBoolean("verif.check_in_verif", true);
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
		return CHEATS.stream().filter((c) -> c.getKey().equalsIgnoreCase(key)).findAny().orElse(null);
	}
	
	public static Set<String> getCheatKeys(){
		return Cheat.CHEATS.stream().collect(Collectors.groupingBy(Cheat::getKey)).keySet();
	}
	
	public static Map<String, Cheat> getCheatByKeys(){
		return Maps.toMap(getCheatKeys(), (key) -> forKey(key));
	}
	
	public static void loadCheat() {
		CHEATS.clear();
		Adapter ada = Adapter.getAdapter();
		try {
			MODULE_FOLDER.mkdirs();
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
				} catch (Exception temp) {
					// on ignore
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ada.getLogger().info("Loaded " + CHEATS.size() + " cheats.");
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
