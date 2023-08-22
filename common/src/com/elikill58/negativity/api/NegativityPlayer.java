package com.elikill58.negativity.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.IronGolem;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.protocols.CheckData;
import com.elikill58.negativity.api.protocols.CheckProcessor;
import com.elikill58.negativity.common.protocols.checkprocessor.PingSpoofCheckProcessor;
import com.elikill58.negativity.common.protocols.checkprocessor.ScaffoldRiseCheckProcessor;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerManager;
import com.elikill58.negativity.universal.bypass.BypassManager;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Cheat.CheatCategory;
import com.elikill58.negativity.universal.detections.Cheat.CheatDescription;
import com.elikill58.negativity.universal.detections.Cheat.CheatHover;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsManager;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class NegativityPlayer {

	private static final Map<UUID, NegativityPlayer> NEGATIVITY_PLAYERS = Collections.synchronizedMap(new ConcurrentHashMap<UUID, NegativityPlayer>());
	private static final Map<UUID, Player> PLAYERS = Collections.synchronizedMap(new ConcurrentHashMap<UUID, Player>());

	private final UUID playerId;
	private final Player p;

	public ArrayList<CheckProcessor> checkProcessors = new ArrayList<>();
	public HashMap<CheatKeys, List<PlayerCheatAlertEvent>> alertNotShowed = new HashMap<>();
	public HashMap<String, String> mods = new HashMap<>();

	// packets
	public Object2IntMap<PacketType> packets = new Object2IntArrayMap<>();
	public int allPackets = 0, lastClick = 0, invincibilityTicks = 0;

	// setBack
	public int noFallDamage = 0, idWaitingAppliedVelocity = -1;
	public Material eatMaterial = null;
	public List<PotionEffect> potionEffects = new ArrayList<>();

	// detection and bypass
	public long loginTime;
	public int rightBlockClick = 0, leftBlockClick = 0, entityClick = 0, leftCancelled = 0, leftFinished = 0, iceCounter = 0, blockAbove = 0, ticks = 0;
	public FlyingReason flyingReason = FlyingReason.REGEN;
	public boolean isOnLadders = false, isTeleporting = false;
	public List<Location> lastLocations = new ArrayList<>();
	public ConcurrentHashMap<IDetectionKey<?>, CheckData> checkDatas = new ConcurrentHashMap<>();

	public Location delta = new Location(null, 0, 0, 0), lastDelta = new Location(null, 0, 0, 0);

	// content
	public Content<List<Double>> listDoubles = new Content<>();
	public Content<Boolean> booleans = new Content<>();
	public Content<Double> doubles = new Content<>();
	public Content<Integer> ints = new Content<>();
	public Content<Long> longs = new Content<>();

	// general values
	public boolean isInFight = false, isFreeze = false, isUsingSlimeBlock = false, isUsingJumpBoost = false, isInvisible = false, isAttacking = false, shouldCheckSensitivity = true,
			buggedVersion = false, hadValidPing = false;
	private boolean isBedrockPlayer = false, disconnecting = false;
	public double sensitivity = 0.0;
	private String clientName, invincibilityReason = "";
	private @Nullable ScheduledTask fightCooldownTask;
	// one thread per person
	private final ExecutorService executor;

	public NegativityPlayer(Player p) {
		this.p = p;
		this.playerId = p.getUniqueId();
		this.loginTime = System.currentTimeMillis();
		this.clientName = "Not loaded";
		this.isBedrockPlayer = BedrockPlayerManager.isBedrockPlayer(p.getUniqueId());
		this.executor = Executors.newSingleThreadExecutor((r) -> new Thread(r, "negativity-player-" + p.getName()));
		
		// add processors like this: checkProcessors.add(new
		// SpiderExampleCheckProcessor(this));
		checkProcessors.add(new ScaffoldRiseCheckProcessor(this));
		checkProcessors.add(new PingSpoofCheckProcessor(this));
		checkProcessors.forEach(CheckProcessor::begin);
	}

	private void load() {
		NegativityAccountManager accManager = Adapter.getAdapter().getAccountManager();
		accManager.get(getUUID()).thenAccept(account -> {
			account.setPlayerName(p.getName());
			account.setIp(p.getIP());
			accManager.save(playerId);
		});
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
	 * Get executor for all players content
	 * 
	 * @return the executor service
	 */
	public ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * Check if the player have be detected for the given cheat It also check for
	 * bypass and TPS drop
	 * 
	 * @param c the cheat which we are trying to detect
	 * @return true if the player can be detected
	 */
	public boolean hasDetectionActive(Cheat c) {
		if (!c.isActive() || Negativity.tpsDrop || buggedVersion || disconnecting)
			return false;
		if (invincibilityTicks > 0)
			return false;
		if (isFreeze)
			return false;
		if (isInFight && c.hasOption(CheatDescription.NO_FIGHT))
			return false;
		if (c.isDisabledForBedrock() && BedrockPlayerManager.isBedrockPlayer(getUUID()))
			return false;
		if (c.isDisabledForJava() && !BedrockPlayerManager.isBedrockPlayer(getUUID()))
			return false;
		if(Negativity.hasBypass && (Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), "bypass." + c.getKey().getLowerKey())
				|| Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), Perm.BYPASS_ALL)))
			return false;
		Adapter ada = Adapter.getAdapter();
		if (ada.getConfig().getDouble("tps_alert_stop") > ada.getLastTPS()) // to make TPS go upper
			return false;
		Player p = getPlayer();
		if (p.getGameMode().equals(GameMode.SPECTATOR))
			return false;
		if (c.getCheatCategory().equals(CheatCategory.MOVEMENT)) {
			if (PlayerModificationsManager.shouldIgnoreMovementChecks(p))
				return false;
			if (!p.getWorld().isChunkLoaded(p.getLocation()))
				return false;
		}
		if (c.getKey().equals(CheatKeys.FLY) && PlayerModificationsManager.canFly(p))
			return false;
		if (BypassManager.hasBypass(p, c))
			return false;
		if(!hadValidPing) {
			if(hadValidPing = (p.getPing() > 0)) {
				return false;
			}
		}
		return p.getPing() < c.getMaxAlertPing();
	}

	public String getWhyDetectionNotActive(Cheat c) {
		if (!c.isActive())
			return "Cheat disabled";
		if (Negativity.tpsDrop)
			return "TPS drop";
		if (disconnecting)
			return "Disconnecting";
		if (buggedVersion)
			return "Bugged Version (1.19)";
		if (invincibilityTicks > 0)
			return "Player invincibility: " + invincibilityReason;
		if (isInFight && c.hasOption(CheatDescription.NO_FIGHT))
			return "In fight";
		if (c.isDisabledForBedrock() && BedrockPlayerManager.isBedrockPlayer(getUUID()))
			return "Bedrock user";
		if (c.isDisabledForJava() && !BedrockPlayerManager.isBedrockPlayer(getUUID()))
			return "Java user";
		if(Negativity.hasBypass && (Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), "bypass." + c.getKey().getLowerKey())
				|| Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), Perm.BYPASS_ALL)))
			return "Bypass permission";
		Adapter ada = Adapter.getAdapter();
		if (ada.getConfig().getDouble("tps_alert_stop") > ada.getLastTPS()) // to make TPS go upper
			return "Low TPS";
		Player p = getPlayer();
		if (p.getGameMode().equals(GameMode.SPECTATOR))
			return "Spectating";
		if (c.getCheatCategory().equals(CheatCategory.MOVEMENT)) {
			if (PlayerModificationsManager.shouldIgnoreMovementChecks(p))
				return "Should ignore movement";
			if (!p.getWorld().isChunkLoaded(p.getLocation()))
				return "Chunk not loaded";
		}
		if (c.getKey().equals(CheatKeys.FLY) && PlayerModificationsManager.canFly(p))
			return "Allowed to fly";
		if (BypassManager.hasBypass(p, c))
			return "Has bypass";
		if(!hadValidPing)
			return "Not valid ping yet";
		if (p.getPing() > c.getMaxAlertPing())
			return "Too high ping (" + p.getPing() + " > " + c.getMaxAlertPing() + ")";
		return "Unknown";
	}

	public void addInvincibilityTicks(int amount, String reason) {
		this.invincibilityTicks += amount;
		this.invincibilityReason = reason;
	}

	public int getInvincibilityTicks() {
		return invincibilityTicks;
	}

	public void downInvincibilityTicks() {
		if (invincibilityTicks > 0)
			this.invincibilityTicks--;
	}

	/**
	 * Get warn of the cheat
	 * 
	 * @param c the cheat
	 * @return the number of warn made by the given cheat
	 */
	public long getWarn(Cheat c) {
		return getAccount().getWarn(c);
	}

	/**
	 * Get all warn of the cheat
	 * 
	 * @param c the cheat
	 * @return the number of warn made by the given cheat
	 */
	public long getAllWarn(Cheat c) {
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
		if (clientName != null && !isBedrockPlayer)
			this.isBedrockPlayer = clientName.equalsIgnoreCase("Geyser");
	}

	public Location getPingedLocation() {
		if (lastLocations.isEmpty())
			return p.getLocation();
		int ping = p.getPing();
		int nbLast = (int) (ping / 50);
		if (lastLocations.size() <= nbLast)
			return lastLocations.get(lastLocations.size() - 1);
		return lastLocations.get(nbLast);
	}

	/**
	 * Kick the player after a ban
	 * 
	 * @param reason the reason of kick
	 * @param time   the time of the ban which make the kick
	 * @param by     who kick the player
	 * @param def    if the ban is definitive
	 */
	public void kickPlayer(String reason, String time, String by, boolean def) {
		getPlayer().kick(Messages.getMessage(getPlayer(), "ban.kick_" + (def ? "def" : "time"), "%reason%", reason, "%time%", String.valueOf(time), "%by%", by));
	}

	/**
	 * Add multiple warn
	 * 
	 * @param c           the cheat which create alert
	 * @param reliability the reliability of all warn
	 * @param amount      the amount of alert
	 * @return old warn amount or -1 if nothing added
	 */
	public long addWarn(Cheat c, int reliability, long amount) {
		if (invincibilityTicks > 0 || c.getReliabilityAlert() > reliability)
			return -1;
		NegativityAccount account = getAccount();
		long old = account.getWarn(c);
		account.setWarnCount(c, old + amount);
		Adapter.getAdapter().getAccountManager().save(getUUID());
		return old;
	}

	/**
	 * Set a new value for the amount of alert of the given cheat
	 * 
	 * @param c      the cheat
	 * @param alerts the new amount of alert
	 */
	public void setWarn(Cheat c, int alerts) {
		NegativityAccount account = getAccount();
		account.setWarnCount(c, alerts);
		Adapter.getAdapter().getAccountManager().save(account.getPlayerId());
	}

	/**
	 * Call {@link PlayerPacketsClearEvent} and then clear all packets
	 */
	public void clearPackets() {
		EventManager.callEvent(new PlayerPacketsClearEvent(getPlayer(), this));
		packets.clear();
	}

	/**
	 * Get the reason of the given cheat
	 * 
	 * @param c the cheat
	 * @return the reason
	 */
	public String getReason(@Nullable Cheat c) {
		String n = "";
		for (Cheat all : Cheat.values())
			if (getAllWarn(all) > 5 && all.isActive())
				n += (n.equals("") ? "" : ", ") + all.getName();
		if (c != null && !n.contains(c.getName()))
			n += (n.equals("") ? "" : ", ") + c.getName();
		return n;
	}

	/**
	 * Compile and return all cheat alert for each cheat
	 * 
	 * @return all cheat alert
	 */
	public List<PlayerCheatAlertEvent> getAlertForAllCheat() {
		final List<PlayerCheatAlertEvent> list = new ArrayList<>();
		alertNotShowed.forEach((c, listAlerts) -> {
			if (!listAlerts.isEmpty())
				list.add(getAlertForCheat(Cheat.forKey(c), listAlerts));
		});
		return list;
	}

	/**
	 * Compile the list of alert into one of the given cheat
	 * 
	 * @param c    the cheat of all alert
	 * @param list all last alert of the cheat
	 * @return a new alert, summary of all others
	 */
	public PlayerCheatAlertEvent getAlertForCheat(Cheat c, List<PlayerCheatAlertEvent> list) {
		int nb = 0, nbConsole = 0;
		HashMap<Integer, Integer> relia = new HashMap<>();
		HashMap<Integer, Integer> ping = new HashMap<>();
		ReportType type = ReportType.INFO;
		boolean hasRelia = false;
		CheatHover hoverProof = null;
		for (PlayerCheatAlertEvent e : list) {
			if (e == null)
				continue;
			nb += e.getNbAlert();

			relia.put(e.getReliability(), relia.getOrDefault(e.getReliability(), 0) + 1);

			ping.put(e.getPing(), ping.getOrDefault(e.getPing(), 0) + 1);

			if (e.getReportType().isStronger(type))
				type = e.getReportType();

			hasRelia = e.hasManyReliability() || hasRelia;

			if (hoverProof == null && e.getHover() != null)
				hoverProof = e.getHover();

			nbConsole += e.getNbAlertConsole();
			e.clearNbAlertConsole();
		}
		// Don't to 100% each times that there is more than 2 alerts, we made a summary,
		// and a the nb of alert to upgrade it
		int newRelia = UniversalUtils.parseInPorcent(UniversalUtils.sum(relia) + nb);
		int newPing = UniversalUtils.sum(ping);
		PlayerCheatAlertEvent first = list.get(0);
		// tried to get check and proof for one, such as we can't show multiple
		return new PlayerCheatAlertEvent(type, p, c, newRelia, hasRelia, newPing, first.getCheckName(), first.getProof(), hoverProof, nb, nbConsole);
	}

	/**
	 * Save and destroy Negativity player and account
	 */
	public void destroy() {
		checkProcessors.forEach(CheckProcessor::stop);
		CompletableFuture.runAsync(() -> {
			NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
			accountManager.save(playerId);
			accountManager.dispose(playerId);
		});
	}

	/**
	 * Make the player currently in fight
	 */
	public void fight() {
		isInFight = true;
		if (fightCooldownTask != null)
			fightCooldownTask.cancel();
		fightCooldownTask = Scheduler.getInstance().runDelayed(this::unfight, 100);
	}

	/**
	 * Make the player no longer in fight
	 */
	public void unfight() {
		isInFight = false;
		if (fightCooldownTask != null) {
			fightCooldownTask.cancel();
			fightCooldownTask = null;
		}
	}

	/**
	 * Get active check processors
	 * 
	 * @return all checks processors
	 */
	public ArrayList<CheckProcessor> getCheckProcessors() {
		return checkProcessors;
	}

	/**
	 * Check if the player is target by a golem
	 * 
	 * @return true if at least one golem target the player
	 */
	public boolean isTargetByIronGolem() {
		for (Entity et : p.getWorld().getEntities())
			if (et instanceof IronGolem)
				if (((IronGolem) et).getTarget() != null && ((IronGolem) et).getTarget().equals(getPlayer()))
					return true;
		return false;
	}

	/**
	 * Create ban effect
	 */
	public void banEffect() {
		// TODO add ban effect
	}

	public int getClick() {
		return rightBlockClick + leftBlockClick + entityClick;
	}

	public void clearClick() {
		rightBlockClick = 0;
		leftBlockClick = 0;
		entityClick = 0;
		leftCancelled = 0;
		leftFinished = 0;
	}

	/**
	 * Check if user is disconnecting
	 * 
	 * @return true is disconnecting
	 */
	public boolean isDisconnecting() {
		return disconnecting;
	}

	/**
	 * Change is user is disconnecting
	 * 
	 * @param disconnecting true if disconnect
	 */
	public void setDisconnecting(boolean disconnecting) {
		this.disconnecting = disconnecting;
	}
	
	/**
	 * Get ticks since the player joins.<br>
	 * They are given by the player.
	 * 
	 * @return ticks
	 */
	public int getTicks() {
		return ticks;
	}
	
	/**
	 * Add one ticks since the player join
	 * 
	 * @param ticks
	 */
	public void addTick() {
		this.ticks++;
	}

	public <D extends CheckData> D getCheckData(Cheat cheat) {
		return (D) this.checkDatas.computeIfAbsent(cheat.getKey(), a -> cheat.getCheckDataCreator().apply(this));
	}

	/**
	 * Get the Negativity Player or create a new one
	 * 
	 * @param p the player which we are looking for it's NegativityPlayer
	 * @return the negativity player
	 */
	public static NegativityPlayer getNegativityPlayer(Player p) {
		NegativityPlayer np = NEGATIVITY_PLAYERS.get(p.getUniqueId());
		if(np != null)
			return np;
		np = new NegativityPlayer(p);
		np.load();
		NEGATIVITY_PLAYERS.put(p.getUniqueId(), np);
		return np;
	}

	/**
	 * Get the Negativity Player or create a new one
	 * 
	 * @param uuid the player UUID
	 * @param call a creator of a new player
	 * @return the negativity player
	 */
	public static NegativityPlayer getNegativityPlayer(UUID uuid, Callable<Player> call) {
		NegativityPlayer np = NEGATIVITY_PLAYERS.get(uuid);
		if(np != null)
			return np;
		np = new NegativityPlayer(getPlayer(uuid, call));
		np.load();
		NEGATIVITY_PLAYERS.put(uuid, np);
		return np;
	}

	/**
	 * Get the negativity player in cache of the given UUID
	 * 
	 * @param playerId the player UUID
	 * @return the negativity player
	 */
	public static NegativityPlayer getCached(UUID playerId) {
		return NEGATIVITY_PLAYERS.get(playerId);
	}

	/**
	 * Get all uuid and their negativity players
	 * 
	 * @return negativity players
	 */
	public static Map<UUID, NegativityPlayer> getAllPlayers() {
		return NEGATIVITY_PLAYERS;
	}

	public static List<NegativityPlayer> getAllNegativityPlayers() {
		return new ArrayList<>(NEGATIVITY_PLAYERS.values());
	}

	/**
	 * Remove the player from cache
	 * 
	 * @param playerId the player UUID
	 */
	public static void removeFromCache(UUID playerId) {
		NegativityPlayer cached = NEGATIVITY_PLAYERS.remove(playerId);
		if (cached != null) {
			cached.destroy();
		}
		PLAYERS.remove(playerId);
	}

	public static Player getPlayer(UUID playerId) {
		return PLAYERS.get(playerId);
	}

	public static Player getPlayer(UUID playerId, Callable<Player> call) {
		return PLAYERS.computeIfAbsent(playerId, (a) -> {
			try {
				return call.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
	}
}
