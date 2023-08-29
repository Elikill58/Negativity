package com.elikill58.negativity.spigot;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.spigot.inventories.holders.CheckMenuHolder;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.spigot.protocols.InventoryMoveProtocol.InventoryMoveData;
import com.elikill58.negativity.spigot.support.FloodGateSupportManager;
import com.elikill58.negativity.spigot.support.GadgetMenuSupport;
import com.elikill58.negativity.spigot.support.ProtocolSupportSupport;
import com.elikill58.negativity.spigot.support.ViaVersionSupport;
import com.elikill58.negativity.spigot.utils.HandUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatCategory;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.SimpleAccountManager;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpigotNegativityPlayer extends NegativityPlayer {

	private static final Map<UUID, SpigotNegativityPlayer> players = new HashMap<>();

	public static ArrayList<UUID> INJECTED = new ArrayList<>();
	public HashMap<PacketType, Integer> PACKETS = new HashMap<>();
	public HashMap<String, String> MODS = new HashMap<>();
	public HashMap<String, Double> contentDouble = new HashMap<>();
	public HashMap<String, Boolean> contentBoolean = new HashMap<>();
	public HashMap<String, Integer> contentInts = new HashMap<>();
	public HashMap<Cheat, List<PlayerCheatAlertEvent>> ALERT_NOT_SHOWED = new HashMap<>();
	public ArrayList<PotionEffect> POTION_EFFECTS = new ArrayList<>();
	public ArrayList<Integer> TIMER_COUNT = new ArrayList<>();
	public ArrayList<Double> flyMoveAmount = new ArrayList<>();
	private WeakReference<Player> p;
	// Packets
	public int ALL = 0, MAX_FLYING;
	// warns & other
	public int LAST_CLICK = 0, ACTUAL_CLICK = 0, SEC_ACTIVE = 0, SPIDER_SAME_DIST = 0, LAST_PING = -1;
	// setBack
	public int NO_FALL_DAMAGE = 0, BYPASS_SPEED = 0, LAST_SLOT_CLICK = -1, LAST_CHAT_MESSAGE_NB = 0, SPEED_NB = 0;
	public double lastYDiff = -3.141592654;
	public long TIME_INVINCIBILITY = 0, LAST_SHOT_BOW = 0, LAST_REGEN = 0, TIME_LAST_MESSAGE = 0, LAST_CLICK_INV = 0, LAST_BLOCK_PLACE = 0, TIME_REPORT = 0, LAST_BLOCK_BREAK = 0,
			TIME_INVINCIBILITY_SPEED = 0, lastAlertCommandRan = 0;
	public String LAST_CHAT_MESSAGE = "";
	public boolean IS_LAST_SEC_SNEAK = false, isFreeze = false, isInvisible = false, isUsingSlimeBlock = false, isJumpingWithBlock = false, isOnLadders = false, lastClickInv = false,
			useAntiNoFallSystem = false, canPingSpoof = false;
	public boolean mustToBeSaved = false, wasFlying = false;
	private boolean isOnGround = true, isBedrockPlayer = false;
	public PacketType lastPacketType = null;
	public FlyingReason flyingReason = FlyingReason.REGEN;
	public Material eatMaterial = Material.AIR, lastClick = Material.AIR;
	public Location lastSpiderLoc;
	public InventoryMoveData inventoryMoveData = new InventoryMoveData();
	public List<String> proof = new ArrayList<>();
	public boolean isInFight = false;
	public BukkitTask fightTask = null;
	public String clientName;
	public int ping = 0, protocolVersion = 0;
	private Version playerVersion;

	public SpigotNegativityPlayer(Player p) {
		super(p.getUniqueId(), p.getName());
		this.p = new WeakReference<>(p);
		this.ping = Utils.getPing(p);
		initMods(p);
		this.clientName = "Not loaded";
		isBedrockPlayer = FloodGateSupportManager.isBedrockPlayer(p.getUniqueId());
		playerVersion = SpigotNegativity.viaVersionSupport ? ViaVersionSupport.getPlayerVersion(p)
				: (SpigotNegativity.protocolSupportSupport ? ProtocolSupportSupport.getPlayerVersion(p) : Version.getVersion());
	}

	public SpigotNegativityPlayer(OfflinePlayer p) {
		super(p.getUniqueId(), p.getName());
		this.p = new WeakReference<>(null);
		isBedrockPlayer = FloodGateSupportManager.isBedrockPlayer(p.getUniqueId());
		playerVersion = Version.getVersion();
	}

	public void initMods(Player p) {
		Plugin pl = SpigotNegativity.getInstance();
		if (pl.isEnabled() && pl.getServer().getMessenger().isOutgoingChannelRegistered(pl, SpigotNegativity.CHANNEL_NAME_FML)) { // if
																																	// not,
																																	// we
																																	// ignore
																																	// it
			p.sendPluginMessage(pl, SpigotNegativity.CHANNEL_NAME_FML, new byte[] { -2, 0 });
			p.sendPluginMessage(pl, SpigotNegativity.CHANNEL_NAME_FML, new byte[] { 0, 2, 0, 0, 0, 0 });
			p.sendPluginMessage(pl, SpigotNegativity.CHANNEL_NAME_FML, new byte[] { 2, 0, 0, 0, 0 });
		} else
			MODS.put("Not checked", "0.0");
	}

	@Override
	public Player getPlayer() {
		Player cached = p != null ? p.get() : null;
		if (cached == null) {
			cached = Bukkit.getPlayer(getUUID());
			if (p != null)
				p.clear();

			p = new WeakReference<>(cached);
		}
		return cached;
	}

	public String getIP() {
		return getPlayer().getAddress().getAddress().getHostAddress();
	}

	@Override
	public Version getPlayerVersion() {
		return playerVersion;
	}

	public int getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(int protocolVersion) {
		this.protocolVersion = protocolVersion;
		this.playerVersion = Version.getVersionByProtocolID(protocolVersion);
	}

	public boolean isBedrockPlayer() {
		return isBedrockPlayer;
	}

	public boolean hasDetectionActive(Cheat c) {
		if (!c.isActive() || UniversalUtils.TPS_DROP)
			return false;
		if (TIME_INVINCIBILITY > System.currentTimeMillis())
			return false;
		if (isInFight && c.isBlockedInFight())
			return false;
		if (SpigotNegativity.tps_alert_stop > Utils.getLastTPS()) // to make TPS go upper
			return false;
		Player p = getPlayer();
		if (SpigotNegativity.gadgetMenuSupport && c.getCheatCategory().equals(CheatCategory.MOVEMENT) && GadgetMenuSupport.checkGadgetsMenuPreconditions(p))
			return false;
		if (c.getCheatCategory().equals(CheatCategory.MOVEMENT) && isUsingRiptide())
			return false;
		if (WorldRegionBypass.hasBypass(c, p.getLocation()))
			return false;
		if (SpigotNegativity.hasBypass && (Perm.hasPerm(this, "bypass." + c.getKey().toLowerCase(Locale.ROOT)) || Perm.hasPerm(this, "bypass.all")))
			return false;
		if (isBedrockPlayer && FloodGateSupportManager.disabledCheat.contains(c.getKey()))
			return false;
		return ping < c.getMaxAlertPing();
	}

	public String getWhyDetectionNotActive(Cheat c) {
		if (!c.isActive())
			return "Cheat disabled";
		if (UniversalUtils.TPS_DROP)
			return "TPS drop";
		if (TIME_INVINCIBILITY > System.currentTimeMillis())
			return "Player invincibility";
		if (isInFight && c.isBlockedInFight())
			return "In fight";
		if (SpigotNegativity.tps_alert_stop > Utils.getLastTPS()) // to make TPS go upper
			return "Low TPS";
		Player p = getPlayer();
		if (SpigotNegativity.gadgetMenuSupport && c.getCheatCategory().equals(CheatCategory.MOVEMENT) && GadgetMenuSupport.checkGadgetsMenuPreconditions(p))
			return "GadgetMenu bypass";
		if (c.getCheatCategory().equals(CheatCategory.MOVEMENT) && isUsingRiptide())
			return "Riptide bypass";
		if (WorldRegionBypass.hasBypass(c, p.getLocation()))
			return "World bypass";
		if (SpigotNegativity.hasBypass && (Perm.hasPerm(this, "bypass." + c.getKey().toLowerCase(Locale.ROOT)) || Perm.hasPerm(this, "bypass.all")))
			return "Permission bypass";
		if (ping > c.getMaxAlertPing())
			return "Too high ping (" + ping + " > " + c.getMaxAlertPing() + ")";
		if (isBedrockPlayer && FloodGateSupportManager.disabledCheat.contains(c.getKey()))
			return "Bedrock player";
		return "Unknown";
	}

	public void updateCheckMenu() {
		for (Player p : Utils.getOnlinePlayers()) {
			if (p.getOpenInventory() != null) {
				Inventory topInv = p.getOpenInventory().getTopInventory();
				if (topInv == null || !topInv.getType().equals(org.bukkit.event.inventory.InventoryType.CHEST)) {
					continue;
				}
				InventoryHolder holder = topInv.getHolder();
				if (holder instanceof CheckMenuHolder) {
					AbstractInventory.getInventory(InventoryType.CHECK_MENU).ifPresent((inv) -> inv.actualizeInventory(p, ((CheckMenuHolder) holder).getCible()));
				}
			}
		}
	}

	public void setBetterClick(int click) {
		NegativityAccount account = getAccount();
		account.setMostClicksPerSecond(click);
		Adapter.getAdapter().getAccountManager().save(account.getPlayerId());
	}

	public void addWarn(Cheat c, int reliability) {
		addWarn(c, reliability, 1);
	}

	public int addWarn(Cheat c, int reliability, int amount) {
		if (System.currentTimeMillis() < TIME_INVINCIBILITY || c.getReliabilityAlert() > reliability)
			return -1;
		NegativityAccount account = getAccount();
		int old = account.getWarn(c);
		account.setWarnCount(c, old + amount);
		mustToBeSaved = true;
		return old;
	}

	public void setWarn(Cheat c, int cheats) {
		NegativityAccount account = getAccount();
		account.setWarnCount(c, cheats);
		Adapter.getAdapter().getAccountManager().save(account.getPlayerId());
	}

	public void setLang(String newLang) {
		NegativityAccount account = getAccount();
		account.setLang(newLang);
		NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
		accountManager.save(account.getPlayerId());
		if (accountManager instanceof SimpleAccountManager.Server) {
			try {
				((SimpleAccountManager.Server) accountManager).sendAccountToProxy(account);
			} catch (IOException e) {
				SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not send account update to proxy", e);
			}
		}
	}

	public void clearPackets() {
		PlayerPacketsClearEvent event = new PlayerPacketsClearEvent(getPlayer(), this);
		SpigotNegativity.callSyncEvent(event);
		int flying = PACKETS.getOrDefault(PacketType.Client.FLYING, 0);
		if (flying > MAX_FLYING)
			MAX_FLYING = flying;
		ALL = 0;
		PACKETS.clear();
	}

	public boolean getAllowFlight() {
		return wasFlying || getPlayer().getAllowFlight();
	}

	@Override
	public boolean isOp() {
		return getPlayer().isOp();
	}

	@Override
	public void startAnalyze(Cheat c) {
		if (c.needPacket() && !INJECTED.contains(getPlayer().getUniqueId()))
			INJECTED.add(getPlayer().getUniqueId());
	}

	@Override
	public void startAllAnalyze() {
		INJECTED.add(getPlayer().getUniqueId());
	}

	@Override
	public void stopAnalyze(Cheat c) {

	}

	@Override
	public String getReason(Cheat c) {
		String n = "";
		for (Cheat all : Cheat.values())
			if (getAllWarn(all) > 5 && all.isActive())
				n = n + (n.equals("") ? "" : ", ") + all.getName();
		if (!n.contains(c.getName()))
			n = n + (n.equals("") ? "" : ", ") + c.getName();
		return n;
	}

	public List<PlayerCheatAlertEvent> getAlertForAllCheat() {
		final List<PlayerCheatAlertEvent> list = new ArrayList<>();
		ALERT_NOT_SHOWED.forEach((c, listAlerts) -> {
			if (!listAlerts.isEmpty())
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
		for (PlayerCheatAlertEvent e : list) {
			nb += e.getNbAlert();

			relia.put(e.getReliability(), relia.getOrDefault(e.getReliability(), 0) + 1);

			ping.put(e.getPing(), ping.getOrDefault(e.getPing(), 0) + 1);

			if (type == ReportType.NONE || (type == ReportType.WARNING && e.getReportType() == ReportType.VIOLATION))
				type = e.getReportType();

			hasRelia = e.hasManyReliability() ? true : hasRelia;

			if (hoverProof == null)
				hoverProof = e.getHover();

			nbConsole += e.getNbAlertConsole();
			e.clearNbAlertConsole();
		}
		// Don't to 100% each times that there is more than 2 alerts, we made a summary,
		// and a the nb of alert to upgrade it
		int newRelia = UniversalUtils.parseInPorcent(UniversalUtils.sum(relia) + nb);
		int newPing = UniversalUtils.sum(ping);
		// we can ignore "proof" and "stats_send" because they have been already saved
		// and they are NOT showed to player
		return new PlayerCheatAlertEvent(type, getPlayer(), c, newRelia, hasRelia, newPing, "", hoverProof, nb, nbConsole);
	}

	public void logProof(String msg) {
		proof.add(msg);
	}

	public void saveProof() {
		if (mustToBeSaved) {
			mustToBeSaved = false;
			Adapter.getAdapter().getAccountManager().save(getUUID());
		}
		if (proof.size() == 0)
			return;
		try {
			File folder = new File(new File(SpigotNegativity.getInstance().getDataFolder(), "user"), "proof");
			if (!folder.exists())
				folder.mkdirs();
			File temp = new File(folder, getUUID() + ".txt");
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

	private void destroy() {
		saveProof();
		UUID playerId = getUUID();
		NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
		accountManager.save(playerId);
		accountManager.dispose(playerId);
	}

	@Override
	public boolean hasDefaultPermission(String s) {
		return getPlayer().hasPermission(s);
	}

	@Override
	public double getLife() {
		return ((Damageable) getPlayer()).getHealth();
	}

	@Override
	public String getName() {
		return getPlayer().getName();
	}

	@Override
	public String getGameMode() {
		return getPlayer().getGameMode().name();
	}

	@Override
	public float getWalkSpeed() {
		return getPlayer().getWalkSpeed();
	}

	@Override
	public int getLevel() {
		return getPlayer().getLevel();
	}

	@Override
	public void kickPlayer(String reason, String time, String by, boolean def) {
		getPlayer().kickPlayer(Messages.getMessage(getPlayer(), "ban.kick_" + (def ? "def" : "time"), "%reason%", reason, "%time%", String.valueOf(time), "%by%", by));
	}

	@SuppressWarnings("deprecation")
	public boolean isOnGround() {
		return isOnGround || getPlayer().isOnGround();
	}

	public void setOnGround(boolean b) {
		this.isOnGround = b;
	}

	@Override
	public void banEffect() {
		int i = 2;
		Location loc = getPlayer().getLocation();
		World w = getPlayer().getWorld();
		w.spawnEntity(loc, EntityType.FIREWORK);
		w.spawnEntity(loc, EntityType.FIREWORK);
		w.spawnEntity(loc, EntityType.FIREWORK);
		w.spawnEntity(loc, EntityType.FIREWORK);
		double baseY = loc.getY();
		for (double y = baseY + 1.5; y > baseY; y = y - 0.05) {
			for (int u = 0; u < 360; u += i) {
				Location flameloc = loc.clone();
				flameloc.setY(y);
				flameloc.setZ(flameloc.getZ() + Math.cos(u) * i);
				flameloc.setX(flameloc.getX() + Math.sin(u) * i);
				if (Version.isNewerOrEquals(Version.getVersion(), Version.V1_13)) {
					Particle.DustOptions dustOptions = new Particle.DustOptions(Color.ORANGE, 1);
					w.spawnParticle(Particle.REDSTONE, flameloc, 1, 0, 0, 0, 0, dustOptions);
				} else {
					w.playEffect(flameloc.add(0, 1, 0), Utils.getEffect("COLOURED_DUST"), 1);
				}
			}
		}
	}

	public void fight() {
		isInFight = true;
		if (fightTask != null)
			fightTask.cancel();
		fightTask = Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				unfight();
			}
		}, 80);
	}

	public void unfight() {
		isInFight = false;
		if (fightTask != null)
			fightTask.cancel();
		fightTask = null;
	}

	public boolean hasElytra() {
		return Version.getVersion().isNewerOrEquals(Version.V1_9) && getPlayer().isGliding();
	}

	public boolean isUsingTrident() {
		return Version.getVersion().isNewerOrEquals(Version.V1_13) && (getPlayer().isRiptiding() || HandUtils.handUseItem(getPlayer(), "TRIDENT"));
	}

	public boolean isTargetByIronGolem() {
		for (Entity et : getPlayer().getWorld().getEntities())
			if (et instanceof IronGolem)
				if (((IronGolem) et).getTarget() != null && ((IronGolem) et).getTarget().equals(getPlayer()))
					return true;
		return false;
	}

	public boolean hasPotionEffect(String effectName) {
		try {
			PotionEffectType potionEffect = PotionEffectType.getByName(effectName);
			if (potionEffect != null) // If optionEffect is null, it doesn't exist in this version
				return getPlayer().hasPotionEffect(potionEffect);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isUsingRiptide() {
		return Version.getVersion().isNewerOrEquals(Version.V1_13) && getPlayer().isRiptiding();
	}

	public PotionEffect getPotionEffect(PotionEffectType type) {
		return getPlayer().getActivePotionEffects().stream().filter((pe) -> pe.getType().equals(type)).findAny().orElse(null);
	}

	public static SpigotNegativityPlayer getNegativityPlayer(Player p) {
		synchronized (players) {
			return players.computeIfAbsent(p.getUniqueId(), id -> new SpigotNegativityPlayer(p));
		}
	}

	public static SpigotNegativityPlayer getNegativityPlayer(OfflinePlayer p) {
		synchronized (players) {
			return players.computeIfAbsent(p.getUniqueId(), id -> new SpigotNegativityPlayer(p));
		}
	}

	public static Map<UUID, SpigotNegativityPlayer> getAllPlayers() {
		return players;
	}

	public static void removeFromCache(UUID playerId) {
		SpigotNegativityPlayer cached = players.remove(playerId);
		if (cached != null) {
			cached.destroy();
		}
	}
}
