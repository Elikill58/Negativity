package com.elikill58.negativity.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.IronGolem;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatCategory;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerManager;
import com.elikill58.negativity.universal.bypass.BypassManager;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsManager;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NegativityPlayer {

	private static final Map<UUID, NegativityPlayer> players = new HashMap<>();
	public static ArrayList<UUID> INJECTED = new ArrayList<>();

	private final UUID playerId;
	private final Player p;
	
	public Set<CheatKeys> ACTIVE_CHEAT = new HashSet<>();
	public ArrayList<String> proof = new ArrayList<>();
	public HashMap<CheatKeys, List<PlayerCheatAlertEvent>> ALERT_NOT_SHOWED = new HashMap<>();
	public HashMap<String, String> MODS = new HashMap<>();
	public ArrayList<Double> flyMoveAmount = new ArrayList<>();
	
	// packets
	public HashMap<PacketType, Integer> PACKETS = new HashMap<>();
	public int ALL_PACKETS = 0, MAX_FLYING = 0;

	public int ACTUAL_CLICK = 0, LAST_CLICK = 0, SEC_ACTIVE = 0;
	
	// setBack
	public int NO_FALL_DAMAGE = 0;
	public Material eatMaterial = null;
	public List<PotionEffect> POTION_EFFECTS = new ArrayList<>();
	
	// detection and bypass
	public long TIME_INVINCIBILITY = 0, TIME_LAST_MESSAGE = 0, timeStartFakePlayer = 0, LAST_BLOCK_BREAK = 0, LAST_BLOCK_PLACE = 0, LAST_REGEN = 0, TIME_REPORT = 0,
			TIME_OTHER_KEEP_ALIVE = 0;
	public int MOVE_TIME = 0, LAST_CHAT_MESSAGE_NB = 0, fakePlayerTouched = 0, BYPASS_SPEED = 0, SPEED_NB = 0, SPIDER_SAME_DIST = 0, IS_LAST_SEC_BLINK = 0;
	public FlyingReason flyingReason = FlyingReason.REGEN;
	public boolean bypassBlink = false, isOnLadders = false, useAntiNoFallSystem = false;
	public PlayerChatEvent LAST_CHAT_EVENT = null;
	public List<Integer> TIMER_COUNT = new ArrayList<>();
	public List<Double> lastY = new ArrayList<>();
	public Location lastSpiderLoc = null;
	public String LAST_OTHER_KEEP_ALIVE = "";
	
	// content
	public Content<Location> locations = new Content<>();
	public Content<Material> materials = new Content<>();
	public Content<Boolean> booleans = new Content<>();
	public Content<Double> doubles = new Content<>();
	public Content<Integer> ints = new Content<>();
	public Content<Long> longs = new Content<>();
	
	// general values
	public boolean isInFight = false, already_blink = false, disableShowingAlert = false, isFreeze = false, isJumpingWithBlock = false, isUsingSlimeBlock = false,
			mustToBeSaved = false, isInvisible = false;
	private boolean isBedrockPlayer = false;
	private String clientName;
	private ScheduledTask fightCooldownTask;

	public NegativityPlayer(Player p) {
		this.p = p;
		this.playerId = p.getUniqueId();
		Adapter ada = Adapter.getAdapter();
		NegativityAccount account = getAccount();
		account.setPlayerName(p.getName());
		account.setIp(p.getIP());
		ada.getAccountManager().save(playerId);
		ACTIVE_CHEAT.clear();
		Cheat.values().stream().filter(Cheat::isActive).forEach(this::startAnalyze);
		this.clientName = "Not loaded";
		this.isBedrockPlayer = BedrockPlayerManager.isBedrockPlayer(p);
	}

	/**
	 * Get the Negativity account of the player
	 * 
	 * @return the negativity account
	 */
	public NegativityAccount getAccount() {
		return NegativityAccount.get(playerId);
	}
	
	/**
	 * Get the player UUID
	 * 
	 * @return the player UUID
	 */
	public UUID getUUID() {
		return playerId;
	}
	
	/**
	 * Get the player name
	 * 
	 * @return the player name
	 */
	public String getName() {
		return getPlayer().getName();
	}
	
	/**
	 * Check if it's a bedrock player
	 * 
	 * @return true if the player connect from bedrock
	 */
	public boolean isBedrockPlayer() {
		return isBedrockPlayer;
	}
	
	/**
	 * Check if the player have be detected for the given cheat
	 * It also cehck for bypass and TPS drop
	 * 
	 * @param c the cheat which we are trying to detect
	 * @return true if the player can be detected
	 */
	public boolean hasDetectionActive(Cheat c) {
		if (!c.isActive() || Negativity.tpsDrop)
			return false;
		if (!ACTIVE_CHEAT.contains(c.getKey()))
			return false;
		if (TIME_INVINCIBILITY > System.currentTimeMillis())
			return false;
		if (isFreeze)
			return false;
		if (isInFight && c.isBlockedInFight())
			return false;
		Adapter ada = Adapter.getAdapter();
		if(ada.getConfig().getDouble("tps_alert_stop") > ada.getLastTPS()) // to make TPS go upper
			return false;
		Player p = getPlayer();
		if (c.getCheatCategory().equals(CheatCategory.MOVEMENT) && PlayerModificationsManager.shouldIgnoreMovementChecks(p))
			return false;
		if (c.getKey().equals(CheatKeys.FLY) && PlayerModificationsManager.canFly(p))
			return false;
		if(BypassManager.hasBypass(p, c))
			return false;
		return p.getPing() < c.getMaxAlertPing();
	}
	
	public String getWhyDetectionNotActive(Cheat c) {
		if(!c.isActive())
			return "Cheat disabled";
		if(Negativity.tpsDrop)
			return "TPS drop";
		if(!ACTIVE_CHEAT.contains(c.getKey()))
			return "Cheat not active";
		if(TIME_INVINCIBILITY > System.currentTimeMillis())
			return "Player invincibility";
		if (isInFight && c.isBlockedInFight())
			return "In fight";
		Adapter ada = Adapter.getAdapter();
		if(ada.getConfig().getDouble("tps_alert_stop") > ada.getLastTPS()) // to make TPS go upper
			return "Low TPS";
		Player p = getPlayer();
		if (c.getCheatCategory().equals(CheatCategory.MOVEMENT) && PlayerModificationsManager.shouldIgnoreMovementChecks(p))
			return "Should ignore movement";
		if (c.getKey().equals(CheatKeys.FLY) && PlayerModificationsManager.canFly(p))
			return "Allowed to fly";
		if(BypassManager.hasBypass(p, c))
			return "Has bypass";
		if(p.getPing() > c.getMaxAlertPing())
			return "Too high ping (" + p.getPing() + " > " + c.getMaxAlertPing() + ")";
		return "Unknown";
	}

	/**
	 * Get warn of the cheat
	 * 
	 * @param c the cheat
	 * @return the number of warn made by the given cheat
	 */
	public int getWarn(Cheat c) {
		return getAccount().getWarn(c);
	}

	/**
	 * Get all warn of the cheat
	 * 
	 * @param c the cheat
	 * @return the number of warn made by the given cheat
	 */
	public int getAllWarn(Cheat c) {
		return getWarn(c);
	}

	/**
	 * Get the player
	 * 
	 * @return the player
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Get the name of the client such as vanilla/fabric...
	 * 
	 * @return client name
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * Set the name of the client such as vanilla/fabric...
	 * 
	 * @param clientName the new client name
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	/**
	 * Manage autoverif.
	 * Remove all current cheat detected, and re-add all which have to be verified
	 */
	public void manageAutoVerif() {
		ACTIVE_CHEAT.clear();
		boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive()) {
				startAnalyze(c);
				if (c.needPacket())
					needPacket = true;
			}
		if (needPacket && !NegativityPlayer.INJECTED.contains(p.getUniqueId()))
			NegativityPlayer.INJECTED.add(p.getUniqueId());
	}
	
	/**
	 * Kick the player after a ban
	 * 
	 * @param reason the reason of kick
	 * @param time the time of the ban which make the kick
	 * @param by who kick the player
	 * @param def if the ban is definitive
	 */
	public void kickPlayer(String reason, String time, String by, boolean def) {
		getPlayer().kick(Messages.getMessage(getPlayer(), "ban.kick_" + (def ? "def" : "time"), "%reason%",
					reason, "%time%", String.valueOf(time), "%by%", by));
	}

	/**
	 * Add one warn to the given cheat
	 * 
	 * @param c the cheat which create alert
	 * @param reliability the reliability of alert
	 */
	public void addWarn(Cheat c, int reliability) {
		addWarn(c, reliability, 1);
	}

	/**
	 * Add multiple warn
	 * 
	 * @param c the cheat which create alert
	 * @param reliability the reliability of all warn
	 * @param amount the amount of alert
	 */
	public void addWarn(Cheat c, int reliability, int amount) {
		if (System.currentTimeMillis() < TIME_INVINCIBILITY || c.getReliabilityAlert() > reliability)
			return;
		NegativityAccount account = getAccount();
		account.setWarnCount(c, account.getWarn(c) + amount);
		mustToBeSaved = true;
	}

	/**
	 * Set a new value for the amount of alert of the given cheat
	 * 
	 * @param c the cheat
	 * @param alerts the new amount of alert
	 */
	public void setWarn(Cheat c, int alerts) {
		NegativityAccount account = getAccount();
		account.setWarnCount(c, alerts);
		Adapter.getAdapter().getAccountManager().save(account.getPlayerId());
	}
	
	/**
	 * Start the analyze of the given cheat
	 * 
	 * @param c the cheat to analyze
	 */
	public void startAnalyze(Cheat c) {
		ACTIVE_CHEAT.add(c.getKey());
		if (c.needPacket() && !INJECTED.contains(getPlayer().getUniqueId()))
			INJECTED.add(getPlayer().getUniqueId());
		if (c.getKey().equals(CheatKeys.FORCEFIELD)) {
			/*if (timeStartFakePlayer == 0)
				timeStartFakePlayer = 1; // not on the player connection
			else
				makeAppearEntities();*/
		}
	}

	/**
	 * Start analyze of all cheat
	 */
	public void startAllAnalyze() {
		INJECTED.add(getPlayer().getUniqueId());
		for (Cheat c : Cheat.values())
			startAnalyze(c);
	}

	/**
	 * Stop analyze of the given cheat
	 * 
	 * @param c the cheat to disable
	 */
	public void stopAnalyze(Cheat c) {
		ACTIVE_CHEAT.remove(c.getKey());
	}
	
	/**
	 * Call {@link PlayerPacketsClearEvent} and then clear all packets
	 */
	public void clearPackets() {
		EventManager.callEvent(new PlayerPacketsClearEvent(getPlayer(), this));
		PACKETS.clear();
	}
	
	/**
	 * Get the reason of the given cheat
	 * 
	 * @param c the cheat
	 * @return the reason
	 */
	public String getReason(Cheat c) {
		String n = "";
		for(Cheat all : Cheat.values())
			if(getAllWarn(all) > 5 && all.isActive())
				n = n + (n.equals("") ? "" : ", ") + all.getName();
		if(!n.contains(c.getName()))
			n = n + (n.equals("") ? "" : ", ") + c.getName();
		return n;
	}

	/**
	 * Log the given message into proof file
	 * 
	 * @param msg the proof message
	 */
	public void logProof(String msg) {
		proof.add(msg);
	}
	
	/**
	 * Save proof and account manager if need to be saved
	 */
	public void saveProof() {
		if(mustToBeSaved) {
			mustToBeSaved = false;
			Adapter.getAdapter().getAccountManager().save(getUUID());
		}
		if (proof.isEmpty())
			return;
		try {
			Path proofDir = Adapter.getAdapter().getDataFolder().getAbsoluteFile().toPath().resolve("user").resolve("proof");
			Path proofFile = proofDir.resolve(getUUID() + ".txt");
			Files.createDirectories(proofDir);
			Files.write(proofFile, (String.join("\n", proof) + '\n').getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			proof.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Compile and return all cheat alert for each cheat
	 * 
	 * @return all cheat alert
	 */
	public List<PlayerCheatAlertEvent> getAlertForAllCheat(){
		final List<PlayerCheatAlertEvent> list = new ArrayList<>();
		ALERT_NOT_SHOWED.forEach((c, listAlerts) -> {
			if(!listAlerts.isEmpty())
				list.add(getAlertForCheat(Cheat.forKey(c), listAlerts));
		});
		return list;
	}
	
	/**
	 * Compile the list of alert into one of the given cheat
	 * 
	 * @param c the cheat of all alert
	 * @param list all last alert of the cheat
	 * @return a new alert, summary of all others
	 */
	public PlayerCheatAlertEvent getAlertForCheat(Cheat c, List<PlayerCheatAlertEvent> list) {
		int nb = 0, nbConsole = 0;
		HashMap<Integer, Integer> relia = new HashMap<>();
		HashMap<Integer, Integer> ping = new HashMap<>();
		ReportType type = ReportType.NONE;
		boolean hasRelia = false;
		CheatHover hoverProof = null;
		for(PlayerCheatAlertEvent e : list) {
			if(e == null)
				continue;
			nb += e.getNbAlert();
			
			relia.put(e.getReliability(), relia.getOrDefault(e.getReliability(), 0) + 1);

			ping.put(e.getPing(), ping.getOrDefault(e.getPing(), 0) + 1);

			if(type == ReportType.NONE || (type == ReportType.WARNING && e.getReportType() == ReportType.VIOLATION))
				type = e.getReportType();

			hasRelia = e.hasManyReliability() || hasRelia;
			
			if(hoverProof == null && e.getHover() != null)
				hoverProof = e.getHover();
			
			nbConsole += e.getNbAlertConsole();
			e.clearNbAlertConsole();
		}
		// Don't to 100% each times that there is more than 2 alerts, we made a summary, and a the nb of alert to upgrade it
		int newRelia = UniversalUtils.parseInPorcent(UniversalUtils.sum(relia) + nb);
		int newPing = UniversalUtils.sum(ping);
		// we can ignore "proof" and "stats_send" because they have been already saved and they are NOT showed to player
		return new PlayerCheatAlertEvent(type, p, c, newRelia, hasRelia, newPing, "", hoverProof, nb, nbConsole);
	}

	/**
	 * Save and destroy Negativity player and account
	 */
	public void destroy() {
		saveProof();
		NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
		accountManager.save(playerId).join();
		accountManager.dispose(playerId);
	}

	/**
	 * Make the player currently in fight
	 */
	public void fight() {
		isInFight = true;
		if (fightCooldownTask != null)
			fightCooldownTask.cancel();
		fightCooldownTask = Scheduler.getInstance().runRepeating(this::unfight, 100);
	}

	/**
	 * Make the player no longer in fight
	 */
	public void unfight() {
		isInFight = false;
	}

	/**
	 * Check if the player is target by a golem
	 * 
	 * @return true if at least one golem target the player
	 */
	public boolean isTargetByIronGolem() {
		for(Entity et : p.getWorld().getEntities())
			if(et instanceof IronGolem)
				if(((IronGolem) et).getTarget() != null && ((IronGolem) et).getTarget().equals(getPlayer()))
					return true;
		return false;
	}

	/**
	 * Make fake player appears
	 */
	public void makeAppearEntities() {
		// TODO make appear fake players
	}
	
	/**
	 * Create ban effect
	 */
	public void banEffect() {
		
	}
	
	/**
	 * Get the Negativity Player or create a new one
	 * 
	 * @param p the player which we are looking for it's NegativityPlayer
	 * @return the negativity player
	 */
	public static NegativityPlayer getNegativityPlayer(Player p) {
		synchronized (players) {
			return players.computeIfAbsent(p.getUniqueId(), id -> new NegativityPlayer(p));
		}
	}

	/**
	 * Get the Negativity Player or create a new one
	 * 
	 * @param uuid the player UUID
	 * @param call a creator of a new player
	 * @return the negativity player
	 */
	public static NegativityPlayer getNegativityPlayer(UUID uuid, Callable<Player> call) {
		synchronized (players) {
			return players.computeIfAbsent(uuid, id -> {
				try {
					return new NegativityPlayer(call.call());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			});
		}
	}

	/**
	 * Get the negativity player in cache of the given UUID
	 * 
	 * @param playerId the player UUID
	 * @return the negativity player
	 */
	public static NegativityPlayer getCached(UUID playerId) {
		return players.get(playerId);
	}
	
	/**
	 * Get all uuid and their negativity players
	 * 
	 * @return negativity players
	 */
	public static Map<UUID, NegativityPlayer> getAllPlayers(){
		return players;
	}

	/**
	 * Remove the player from cache
	 * 
	 * @param playerId the player UUID
	 */
	public static void removeFromCache(UUID playerId) {
		NegativityPlayer cached = players.remove(playerId);
		if (cached != null) {
			cached.destroy();
		}
	}
}
