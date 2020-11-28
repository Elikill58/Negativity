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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.api.yaml.config.YamlConfiguration;
import com.elikill58.negativity.universal.setBack.SetBackEntry;
import com.elikill58.negativity.universal.setBack.SetBackProcessor;
import com.elikill58.negativity.universal.setBack.processor.PotionEffectProcessor;
import com.elikill58.negativity.universal.setBack.processor.TeleportProcessor;
import com.elikill58.negativity.universal.setBack.processor.ValueEditorProcessor;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerificationManager;

public abstract class Cheat {

	protected static final File MODULE_FOLDER = new File(Adapter.getAdapter().getDataFolder(), "modules");
	public static final List<Cheat> CHEATS = new ArrayList<>();
	private final String key;
	private Configuration config;
	private boolean needPacket, hasVerif;
	private CheatCategory cheatCategory;
	private Material m;
	private String[] aliases;
	private final List<SetBackProcessor> setBackProcessor = new ArrayList<>();

	/**
	 * Create a new cheat object and load default config
	 * 
	 * @param key the cheat key
	 * @param type the cheat category
	 * @param m the material used in inventory to represent this cheat
	 * @param needPacket if the cheat need packet in detections
	 * @param hasVerif know if the cheat can be used in verification system
	 * @param alias all other names of the cheat
	 */
	public Cheat(String key, CheatCategory type, Material m, boolean needPacket, boolean hasVerif, String... alias) {
		this.needPacket = needPacket;
		this.m = m;
		this.cheatCategory = type;
		this.key = key.toLowerCase();
		this.aliases = alias;
		this.hasVerif = hasVerif;
		
		try {
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
		
			this.config.getStringList("set_back.action").forEach((line) -> {
				
				JSONObject json = null;
				try {
					json = (JSONObject) new JSONParser().parse(line);
				} catch (Exception e) {}
				SetBackEntry entry = json == null ? new SetBackEntry(line) : new SetBackEntry(json);
				switch (entry.getType().toLowerCase()) {
				case "potion_effect":
					setBackProcessor.add(new PotionEffectProcessor(entry));
					break;
				case "teleport":
					setBackProcessor.add(new TeleportProcessor(entry));
					break;
				case "value_editor":
					setBackProcessor.add(new ValueEditorProcessor(entry));
					break;
				default:
					break;
				}
			});
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("Cannot load cheat " + key + ".");
			e.printStackTrace();
		}
	}
	
	/**
	 * The cheat key
	 * In upper case to be compared to CheatKeys's value
	 * 
	 * @return the cheat key in upper case
	 */
	public String getKey() {
		return key.toUpperCase();
	}
	
	/**
	 * Get the configuration of the cheat
	 * 
	 * @return the cheat config
	 */
	public Configuration getConfig() {
		return config;
	}
	
	/**
	 * Save the configuration of the cheat
	 */
	public void saveConfig() {
		config.save();
	}
	
	/**
	 * Get the exact name of the cheat
	 * 
	 * @return the name
	 */
	public String getName() {
		return config.getString("exact_name", key);
	}

	/**
	 * Check if the cheat is active
	 * 
	 * @return true if the cheat is active
	 */
	public boolean isActive() {
		return config.getBoolean("active", true);
	}
	
	/**
	 * Check if a detection is active
	 * 
	 * @param checkName the name of the detection
	 * @return true if the detection is active
	 */
	public boolean checkActive(String checkName) {
		//if(config.contains("checks." + checkName + ".active"))
			return config.getBoolean("check." + checkName + ".active", true);
		//config.set("checks." + checkName + ".active", true);
		//return true;
	}
	
	/**
	 * Check if the cheat is blocked in fight.
	 * Overrided if true, by default it's false
	 * 
	 * @return true if the cheat is blocked in fight
	 */
	public boolean isBlockedInFight() {
		return false;
	}
	
	/**
	 * Get the category of the cheat
	 * 
	 * @return the cheat category
	 */
	public CheatCategory getCheatCategory() {
		return cheatCategory;
	}

	/**
	 * Check if the cheat need packet for at least one detection
	 * 
	 * @return true if the cheat need packet
	 */
	public boolean needPacket() {
		return needPacket;
	}

	/**
	 * Get the cheat material which can be showed on inventory
	 * 
	 * @return the material
	 */
	public Material getMaterial() {
		return m;
	}

	/**
	 * Get needed reliability to see alert
	 * By default it's 60%
	 * 
	 * @return the needed reliability
	 */
	public int getReliabilityAlert() {
		return config.getInt("reliability_alert", 60);
	}

	/**
	 * Check if the setBack option is enabled
	 * 
	 * @return true if enabled
	 */
	public boolean isSetBack() {
		return config.getBoolean("set_back.active", false);
	}

	/**
	 * Check if the cheat is allowed to kick player
	 * 
	 * @return true if kick is allowed
	 */
	public boolean allowKick() {
		return config.getBoolean("kick.active", false);
	}

	/**
	 * Set the allowability to kick
	 * Warn: this don't save the config
	 * 
	 * @param b the new value
	 * @return the given boolean value
	 */
	public boolean setAllowKick(boolean b) {
		config.set("kick.active", b);
		return b;
	}

	/**
	 * Get the amount of needed alert to kick
	 * By default it's 5
	 * 
	 * @return the needed alert counter
	 */
	public int getAlertToKick() {
		return config.getInt("kick.alert", 5);
	}


	/**
	 * Set if setback option is active
	 * Warn: this don't save the config
	 * 
	 * @param b the new value
	 * @return the given boolean value
	 */
	public boolean setBack(boolean b) {
		config.set("set_back.active", b);
		return b;
	}


	/**
	 * Set if the cheat is active
	 * Warn: this don't save the config
	 * 
	 * @param active the new value
	 * @return the given boolean value
	 */
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

	/**
	 * Get the maximum ping to create alert
	 * By default it's 150ms
	 * 
	 * @return the max ping to create alert
	 */
	public int getMaxAlertPing() {
		return config.getInt("ping", 150);
	}
	
	/**
	 * Get all alias of this cheat
	 * 
	 * @return cheat aliases
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Set if the cheat is used in verif
	 * Warn: this don't save the config
	 * 
	 * @param verif the new value
	 */
	public void setVerif(boolean verif) {
		config.set("verif.check_in_verif", verif);
	}

	/**
	 * Check if the cheat can verif someone
	 * 
	 * @return true if the cheat can verif
	 */
	public boolean hasVerif() {
		return hasVerif && config.getBoolean("verif.check_in_verif", true);
	}
	
	/**
	 * Use set back option of the player
	 * Warn: this doesn't check if setBack option is enabled
	 * 
	 * @param p the player which have to be set back
	 */
	public void performSetBack(Player p) {
		setBackProcessor.forEach((st) -> st.perform(p));
	}

	/**
	 * Create a new hover message
	 * 
	 * @param key the key of the message
	 * @param placeholders all placeholders of the message
	 * @return the cheatHover which  will be showed
	 */
	public CheatHover hoverMsg(String key, Object... placeholders) {
		return new CheatHover("hover." + this.key + "." + key, placeholders);
	}
	
	/**
	 * Create a verification summary
	 * 
	 * @param data the verif data
	 * @param np the negativity player
	 * @return the summary (or null if cheat didn't verif/find something)
	 */
	public @Nullable String makeVerificationSummary(VerifData data, NegativityPlayer np) { return null; }
	
	/**
	 * Add data to verification
	 * 
	 * @param target the player which create the data
	 * @param type the data type
	 * @param value the value recorded
	 */
	public final <T> void recordData(UUID target, VerifData.DataType<T> type, T value) {
		VerificationManager.recordData(target, this, type, value);
	}
	
	/**
	 * Get cheat from a name
	 * Can be the key, the name or one of the alias
	 * 
	 * @param name the cheat name
	 * @return the cheat or null if anything found
	 */
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
	
	/**
	 * Get the cheat according to the key
	 * 
	 * @param key the cheat key
	 * @return the cheat or null if anything is found
	 */
	public static Cheat forKey(String key) {
		return CHEATS.stream().filter((c) -> c.getKey().equalsIgnoreCase(key)).findAny().orElse(null);
	}
	
	/**
	 * Get all cheat keys
	 * 
	 * @return all cheat keys
	 */
	public static Set<String> getCheatKeys(){
		return Cheat.CHEATS.stream().collect(Collectors.groupingBy(Cheat::getKey)).keySet();
	}
	
	/**
	 * Return a map of cheat keys and their corresponding cheat instance
	 * 
	 * @return keys and their cheat
	 */
	public static Map<String, Cheat> getCheatByKeys(){
		return CHEATS.stream().collect(Collectors.toMap(Cheat::getKey, Function.identity()));
	}
	
	/**
	 * Load all cheat
	 * Support reload
	 */
	public static void loadCheat() {
		CHEATS.clear();
		MODULE_FOLDER.mkdirs();
		Adapter ada = Adapter.getAdapter();
		for (Cheat cheat : ServiceLoader.load(Cheat.class, Cheat.class.getClassLoader())) {
			try {
				EventManager.registerEvent((Listeners) cheat);
			} catch (Exception e) {
				ada.getLogger().error("Failed to register cheat " + cheat.getName() + " as a listener");
				e.printStackTrace();
			}
			CHEATS.add(cheat);
		}
		CHEATS.sort(Comparator.comparing(Cheat::getKey));
		ada.getLogger().info("Loaded " + CHEATS.size() + " cheats.");
	}

	public static List<Cheat> values() {
		return CHEATS;
	}

	public static enum CheatCategory {
		
		/**
		 * Edit combat value such as attack speed of attack distance
		 */
		COMBAT,
		
		/**
		 * Edit player movement
		 */
		MOVEMENT,
		/**
		 * Edit the world of the player such as block breaker
		 */
		WORLD,
		/**
		 * Edit player variable/abilities directly
		 */
		PLAYER;
	}
	
	public static class CheatHover {
		private final String key;
		private final Object[] placeholders;
		
		/**
		 * Create a new cheat hover
		 * 
		 * @param key the key of the message which will be translated
		 * @param placeholders the placeholders of the message
		 */
		public CheatHover(String key, Object... placeholders) {
			this.key = Objects.requireNonNull(key);
			this.placeholders = placeholders;
		}

		/**
		 * Get the message key
		 * 
		 * @return the message key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * Get all message placeholders
		 * 
		 * @return placeholders
		 */
		public Object[] getPlaceholders() {
			return placeholders;
		}
		
		/**
		 * Compile message with default lang
		 * Prefer use {@link #compile(NegativityPlayer)} to have a message translated for each player
		 * 
		 * @return the translated message
		 */
		public String compile() {
			return TranslatedMessages.getStringFromLang(TranslatedMessages.DEFAULT_LANG, getKey(), getPlaceholders());
		}
		
		/**
		 * Compile message with the lang of the given negativity player
		 * 
		 * @param np the player which will receive the message
		 * @return the translated message
		 */
		public String compile(NegativityPlayer np) {
			return TranslatedMessages.getStringFromLang(np.getAccount().getLang(), getKey(), getPlaceholders());
		}

		/**
		 * This class is used to have CheatHover but not translated
		 * It's useful when you have a cheat hover to show but don't want to edit lang message
		 * 
		 */
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
