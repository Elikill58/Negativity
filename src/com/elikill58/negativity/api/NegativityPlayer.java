package com.elikill58.negativity.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatCategory;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.bypass.WorldRegionBypass;
import com.elikill58.negativity.universal.support.EssentialsSupport;
import com.elikill58.negativity.universal.support.FloodGateSupport;
import com.elikill58.negativity.universal.support.GadgetMenuSupport;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NegativityPlayer {

	private static final Map<UUID, NegativityPlayer> players = new HashMap<>();
	public static ArrayList<UUID> INJECTED = new ArrayList<>();

	private final UUID playerId;
	private final Player p;
	
	public ArrayList<Cheat> ACTIVE_CHEAT = new ArrayList<>();
	public ArrayList<String> proof = new ArrayList<>();
	public HashMap<Cheat, List<PlayerCheatAlertEvent>> ALERT_NOT_SHOWED = new HashMap<>();
	public HashMap<String, String> MODS = new HashMap<>();
	
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
			isInvisible = false;
	private boolean mustToBeSaved = false, isBedrockPlayer = false;
	private Timer fightTimer;

	public NegativityPlayer(Player p) {
		this.p = p;
		this.playerId = p.getUniqueId();
		Adapter ada = Adapter.getAdapter();
		getAccount().setPlayerName(p.getName());
		ada.getAccountManager().save(playerId);
		ACTIVE_CHEAT.clear();
		//boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive()) {
				startAnalyze(c);
				//if (c.needPacket())
					//needPacket = true;
			}
		this.isBedrockPlayer = Negativity.floodGateSupport ? FloodGateSupport.isBedrockPlayer(p.getUniqueId()) : false;
	}

	public NegativityAccount getAccount() {
		return NegativityAccount.get(playerId);
	}
	
	public UUID getUUID() {
		return playerId;
	}
	
	public String getName() {
		return getPlayer().getName();
	}
	
	public boolean isBedrockPlayer() {
		return isBedrockPlayer;
	}
	
	public boolean hasDetectionActive(Cheat c) {
		if (!c.isActive())
			return false;
		if (!ACTIVE_CHEAT.contains(c))
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
		if (Negativity.gadgetMenuSupport && c.getCheatCategory().equals(CheatCategory.MOVEMENT) &&  GadgetMenuSupport.checkGadgetsMenuPreconditions(p))
			return false;
		if (Negativity.essentialsSupport && c.getKey().equals(CheatKeys.FLY) && p.hasPermission("essentials.fly") && EssentialsSupport.checkEssentialsPrecondition(p))
			return false;
		if(WorldRegionBypass.hasBypass(c, p.getLocation()))
			return false;
		return p.getPing() < c.getMaxAlertPing();
	}

	public int getWarn(Cheat c) {
		return getAccount().getWarn(c);
	}

	public int getAllWarn(Cheat c) {
		return getAccount().getWarn(c);
	}

	public Player getPlayer() {
		return p;
	}
	
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
	
	public void kickPlayer(String reason, String time, String by, boolean def) {
		getPlayer().kick(Messages.getMessage(getPlayer(), "ban.kick_" + (def ? "def" : "time"), "%reason%",
					reason, "%time%", String.valueOf(time), "%by%", by));
	}

	public void addWarn(Cheat c, int reliability) {
		addWarn(c, reliability, 1);
	}

	public void addWarn(Cheat c, int reliability, int amount) {
		if (System.currentTimeMillis() < TIME_INVINCIBILITY || c.getReliabilityAlert() > reliability)
			return;
		NegativityAccount account = getAccount();
		account.setWarnCount(c, account.getWarn(c) + amount);
		mustToBeSaved = true;
	}

	public void setWarn(Cheat c, int cheats) {
		NegativityAccount account = getAccount();
		account.setWarnCount(c, cheats);
		Adapter.getAdapter().getAccountManager().save(account.getPlayerId());
	}
	
	public void startAnalyze(Cheat c) {
		ACTIVE_CHEAT.add(c);
		if (c.needPacket() && !INJECTED.contains(getPlayer().getUniqueId()))
			INJECTED.add(getPlayer().getUniqueId());
		if (c.getKey().equalsIgnoreCase(CheatKeys.FORCEFIELD)) {
			/*if (timeStartFakePlayer == 0)
				timeStartFakePlayer = 1; // not on the player connection
			else
				makeAppearEntities();*/
		}
	}

	public void startAllAnalyze() {
		INJECTED.add(getPlayer().getUniqueId());
		for (Cheat c : Cheat.values())
			startAnalyze(c);
	}

	public void stopAnalyze(Cheat c) {
		ACTIVE_CHEAT.remove(c);
	}
	
	public void clearPackets() {
		EventManager.callEvent(new PlayerPacketsClearEvent(getPlayer(), this));
		PACKETS.clear();
	}
	
	public String getReason(Cheat c) {
		String n = "";
		for(Cheat all : Cheat.values())
			if(getAllWarn(all) > 5 && all.isActive())
				n = n + (n.equals("") ? "" : ", ") + all.getName();
		if(!n.contains(c.getName()))
			n = n + (n.equals("") ? "" : ", ") + c.getName();
		return n;
	}

	public void logProof(String msg) {
		proof.add(msg);
	}
	
	public void saveProof() {
		if(mustToBeSaved) {
			mustToBeSaved = false;
			Adapter.getAdapter().getAccountManager().save(getUUID());
		}
		if (proof.isEmpty())
			return;
		try {
			File temp = new File(Adapter.getAdapter().getDataFolder().getAbsolutePath() + File.separator
					+ "user" + File.separator + "proof" + File.separator + getUUID() + ".txt");
			if (!temp.exists())
				temp.createNewFile();
			String msg = "";
			for (String s : proof)
				msg += s + "\n";
			Files.write(temp.toPath(), msg.getBytes(), StandardOpenOption.APPEND);
			proof.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<PlayerCheatAlertEvent> getAlertForAllCheat(){
		final List<PlayerCheatAlertEvent> list = new ArrayList<>();
		ALERT_NOT_SHOWED.forEach((c, listAlerts) -> {
			if(!listAlerts.isEmpty())
				list.add(getAlertForCheat(c, listAlerts));
		});
		return list;
	}
	
	public PlayerCheatAlertEvent getAlertForCheat(Cheat c, List<PlayerCheatAlertEvent> list) {
		int nb = 0, nbConsole = 0;
		HashMap<Integer, Integer> relia = new HashMap<>();
		HashMap<Integer, Integer> ping = new HashMap<>();
		ReportType type = ReportType.NONE;
		boolean hasRelia = false;
		CheatHover hoverProof = null;
		for(PlayerCheatAlertEvent e : list) {
			nb += e.getNbAlert();
			
			relia.put(e.getReliability(), relia.getOrDefault(e.getReliability(), 0) + 1);

			ping.put(e.getPing(), ping.getOrDefault(e.getPing(), 0) + 1);

			if(type == ReportType.NONE || (type == ReportType.WARNING && e.getReportType() == ReportType.VIOLATION))
				type = e.getReportType();

			hasRelia = e.hasManyReliability() ? true : hasRelia;
			
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

	public void destroy() {
		saveProof();
		NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
		accountManager.save(playerId);
		accountManager.dispose(playerId);
	}

	public void fight() {
		isInFight = true;
		if(fightTimer != null)
			fightTimer.cancel();
		fightTimer = new Timer();
		fightTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				unfight();
			}
		}, 5000);
	}

	public void unfight() {
		isInFight = false;
	}

	public boolean isTargetByIronGolem() {
		for(Entity et : p.getWorld().getEntities())
			if(et instanceof IronGolem)
				if(((IronGolem) et).getTarget() != null && ((IronGolem) et).getTarget().equals(getPlayer()))
					return true;
		return false;
	}

	public void makeAppearEntities() {
		
	}
	
	public void banEffect() {
		
	}
	
	public static NegativityPlayer getNegativityPlayer(Player p) {
		return players.computeIfAbsent(p.getUniqueId(), id -> new NegativityPlayer(p));
	}

	public static NegativityPlayer getNegativityPlayer(UUID uuid, Callable<Player> call) {
		return players.computeIfAbsent(uuid, id -> {
			try {
				return new NegativityPlayer(call.call());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	public static NegativityPlayer getCached(UUID playerId) {
		return players.get(playerId);
	}
	
	public static Map<UUID, NegativityPlayer> getAllPlayers(){
		return players;
	}

	public static void removeFromCache(UUID playerId) {
		NegativityPlayer cached = players.remove(playerId);
		if (cached != null) {
			cached.destroy();
		}
	}
}
