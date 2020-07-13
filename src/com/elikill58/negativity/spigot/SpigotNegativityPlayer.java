package com.elikill58.negativity.spigot;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.spigot.protocols.ForceFieldProtocol;
import com.elikill58.negativity.spigot.support.ProtocolSupportSupport;
import com.elikill58.negativity.spigot.support.ViaVersionSupport;
import com.elikill58.negativity.spigot.utils.InventoryUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.SimpleAccountManager;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpigotNegativityPlayer extends NegativityPlayer {

	private static final Map<UUID, SpigotNegativityPlayer> players = new HashMap<>();

	public static ArrayList<UUID> INJECTED = new ArrayList<>();
	public ArrayList<Cheat> ACTIVE_CHEAT = new ArrayList<>();
	public ArrayList<FakePlayer> FAKE_PLAYER = new ArrayList<>();
	public HashMap<PacketType, Integer> PACKETS = new HashMap<>();
	public HashMap<String, String> MODS = new HashMap<>();
	public HashMap<String, Double> contentDouble = new HashMap<>();
	public HashMap<String, Boolean> contentBoolean = new HashMap<>();
	public HashMap<Cheat, List<PlayerCheatAlertEvent>> ALERT_NOT_SHOWED = new HashMap<>();
	public ArrayList<PotionEffect> POTION_EFFECTS = new ArrayList<>();
	public ArrayList<Integer> TIMER_COUNT = new ArrayList<>();
	private WeakReference<Player> p;
	// Packets
	public int ALL = 0, MAX_FLYING;
	// warns & other
	public int LAST_CLICK = 0, ACTUAL_CLICK = 0, SEC_ACTIVE = 0, SPIDER_SAME_DIST = 0;
	// setBack
	public int NO_FALL_DAMAGE = 0, BYPASS_SPEED = 0, IS_LAST_SEC_BLINK = 0, LAST_SLOT_CLICK = -1, LAST_CHAT_MESSAGE_NB = 0, SPEED_NB = 0, MOVE_TIME = 0;
	public double lastYDiff = -3.141592654;
	public long TIME_OTHER_KEEP_ALIVE = 0, TIME_INVINCIBILITY = 0, LAST_SHOT_BOW = 0, LAST_REGEN = 0,
			LAST_CLICK_INV = 0, LAST_BLOCK_PLACE = 0, TIME_REPORT = 0, LAST_BLOCK_BREAK = 0, LAST_USE_ENTITY = 0;
	public String LAST_OTHER_KEEP_ALIVE, LAST_CHAT_MESSAGE = "";
	public boolean IS_LAST_SEC_SNEAK = false, bypassBlink = false, isFreeze = false, disableShowingAlert = false,
			isInvisible = false, isUsingSlimeBlock = false, already_blink = false, isJumpingWithBlock = false,
			isOnLadders = false, lastClickInv = false;
	private boolean mustToBeSaved = false, isOnGround = true;
	public PacketType lastPacketType = null;
	public FlyingReason flyingReason = FlyingReason.REGEN;
	public Material eatMaterial = Material.AIR, lastClick = Material.AIR;
	public Location lastSpiderLoc;
	public List<String> proof = new ArrayList<>();
	public boolean isInFight = false;
	public BukkitTask fightTask = null;
	public int fakePlayerTouched = 0;
	public long timeStartFakePlayer = 0;
	private final Version playerVersion;

	public SpigotNegativityPlayer(Player p) {
		super(null);
		this.p = new WeakReference<>(p);
		initMods(p);
		playerVersion = SpigotNegativity.viaVersionSupport ? ViaVersionSupport.getPlayerVersion(p) : (SpigotNegativity.protocolSupportSupport ? ProtocolSupportSupport.getPlayerVersion(p) : Version.getVersion());
	}

	public void initMods(Player p) {
		Plugin pl = SpigotNegativity.getInstance();
		if(pl.isEnabled()) {
			p.sendPluginMessage(pl, SpigotNegativity.CHANNEL_NAME_FML, new byte[] { -2, 0 });
			p.sendPluginMessage(pl, SpigotNegativity.CHANNEL_NAME_FML, new byte[] { 0, 2, 0, 0, 0, 0 });
			p.sendPluginMessage(pl, SpigotNegativity.CHANNEL_NAME_FML, new byte[] { 2, 0, 0, 0, 0 });
		}
	}

	public String getIP() {
		return p.get().getAddress().getAddress().getHostAddress();
	}
	
	public boolean hasDetectionActive(Cheat c) {
		if(!c.isActive())
			return false;
		if(!ACTIVE_CHEAT.contains(c))
			return false;
		return Utils.getPing(getPlayer()) < c.getMaxAlertPing();
	}
	
	public void updateCheckMenu() {
		for (Player p : Inv.CHECKING.keySet()) {
			if (p.getOpenInventory() != null) {
				if (InventoryUtils.getInventoryTitle(p.getOpenInventory()).equals(Inv.NAME_CHECK_MENU)){
					AbstractInventory.getInventory(InventoryType.CHECK_MENU).ifPresent((inv) -> inv.actualizeInventory(p, Inv.CHECKING.get(p)));
					//CheckMenuInventory.actualizeCheckMenu(p, Inv.CHECKING.get(p));
				}
			}
		}
	}
	
	public void setBetterClick(int click) {
		NegativityAccount account = getAccount();
		account.setMostClicksPerSecond(click);
		Adapter.getAdapter().getAccountManager().save(account.getPlayerId());
	}

	@Deprecated
	public void addWarn(Cheat c) {
		addWarn(c, 100);
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
		Bukkit.getPluginManager().callEvent(event);
		int flying = PACKETS.getOrDefault(PacketType.Client.FLYING, 0);
		if (flying > MAX_FLYING)
			MAX_FLYING = flying;
		ALL = 0;
		PACKETS.clear();
	}

	@Override
	public void startAnalyze(Cheat c) {
		ACTIVE_CHEAT.add(c);
		if (c.needPacket() && !INJECTED.contains(getPlayer().getUniqueId()))
			INJECTED.add(getPlayer().getUniqueId());
		if (c.getKey().equalsIgnoreCase(CheatKeys.FORCEFIELD)) {
			if (timeStartFakePlayer == 0)
				timeStartFakePlayer = 1; // not on the player connection
			else
				makeAppearEntities();
		}
	}

	@Override
	public void startAllAnalyze() {
		INJECTED.add(getPlayer().getUniqueId());
		for (Cheat c : Cheat.values())
			startAnalyze(c);
	}

	@Override
	public void stopAnalyze(Cheat c) {
		ACTIVE_CHEAT.remove(c);
	}
	
	@Override
	public String getReason(Cheat c) {
		String n = "";
		for(Cheat all : Cheat.values())
			if(getAllWarn(all) > 5 && all.isActive())
				n = n + (n.equals("") ? "" : ", ") + all.getName();
		if(!n.contains(c.getName()))
			n = n + (n.equals("") ? "" : ", ") + c.getName();
		return n;
	}

	public void makeAppearEntities() {
		if (!ACTIVE_CHEAT.contains(Cheat.forKey(CheatKeys.FORCEFIELD))
				|| SpigotNegativity.getInstance().getConfig().getBoolean("cheats.forcefield.ghost_disabled"))
			return;
		timeStartFakePlayer = System.currentTimeMillis();
		fakePlayerTouched = 0;

		spawnRight();
		spawnLeft();
		spawnBehind();
	}

	public void spawnRandom() {
		if(fakePlayerTouched > 20) // limit to prevent player freeze
			return;
		int choice = new Random().nextInt(3);
		if (choice == 0)
			spawnRight();
		else if (choice == 1)
			spawnBehind();
		else
			spawnLeft();
	}

	private void spawnRight() {
		Location loc = getPlayer().getLocation().clone();
		Vector dir = getPlayer().getEyeLocation().getDirection();
		double x = dir.getX(), z = dir.getZ();
		if (x >= 0 && z >= 0) {
			loc.add(-1, 0, 1);
		} else if (x >= 0 && z <= 0) {
			loc.add(-1, 0, 0);
		} else if (x <= 0 && z >= 0) {
			loc.add(-1, 0, 0);
		} else if (x <= 0 && z <= 0) {
			loc.add(-1, 0, 1);
		}
		loc.add(0, 1, 0);
		FakePlayer fp = new FakePlayer(loc, getRandomFakePlayerName()).show(getPlayer());
		FAKE_PLAYER.add(fp);
	}

	private void spawnLeft() {
		Location loc = getPlayer().getLocation().clone();
		Vector dir = getPlayer().getEyeLocation().getDirection();
		double x = dir.getX(), z = dir.getZ();
		if (x >= 0 && z >= 0) {
			loc.add(0, 0, -1);
		} else if (x >= 0 && z <= 0) {
			loc.add(-1, 0, 1);
		} else if (x <= 0 && z >= 0) {
			loc.add(1, 0, -1);
		} else if (x <= 0 && z <= 0) {
			loc.add(1, 0, 1);
		}
		loc.add(0, 1, 0);
		FakePlayer fp = new FakePlayer(loc, getRandomFakePlayerName()).show(getPlayer());
		FAKE_PLAYER.add(fp);
	}

	public Version getPlayerVersion() {
		return playerVersion;
	}

	private void spawnBehind() {
		Location loc = getPlayer().getLocation().clone();
		Vector dir = getPlayer().getEyeLocation().getDirection();
		double x = dir.getX(), z = dir.getZ();
		if (x >= 0 && z >= 0) {
			loc.add(1, 0, -1);
		} else if (x >= 0 && z <= 0) {
			loc.add(1, 0, 1);
		} else if (x <= 0 && z >= 0) {
			loc.add(1, 0, 1);
		} else if (x <= 0 && z <= 0) {
			loc.add(1, 0, -1);
		}
		loc.add(0, 1, 0);
		FakePlayer fp = new FakePlayer(loc, getRandomFakePlayerName()).show(getPlayer());
		FAKE_PLAYER.add(fp);
	}

	private String getRandomFakePlayerName() {
		List<Player> online = Utils.getOnlinePlayers();
		if (online.size() <= 1) {
			return new Random().nextBoolean() ? "Elikill58" : "RedNesto";
		}
		return online.get(new Random().nextInt(online.size())).getName();
	}

	public List<FakePlayer> getFakePlayers() {
		return new ArrayList<>(FAKE_PLAYER);
	}

	public void removeFakePlayer(FakePlayer fp, boolean detected) {
		if (!FAKE_PLAYER.contains(fp))
			return;
		FAKE_PLAYER.remove(fp);
		if(!detected) {
			if(fakePlayerTouched > 0)
				ForceFieldProtocol.manageForcefieldForFakeplayer(getPlayer(), this);
			if(FAKE_PLAYER.size() == 0)
				fakePlayerTouched = 0;
			return;
		}
		fakePlayerTouched++;
		long l = (System.currentTimeMillis() - timeStartFakePlayer);
		if (l >= 3000) {
			if (FAKE_PLAYER.size() == 0) {
				ForceFieldProtocol.manageForcefieldForFakeplayer(getPlayer(), this);
				fakePlayerTouched = 0;
			}
		} else {
			ForceFieldProtocol.manageForcefieldForFakeplayer(getPlayer(), this);
			if(fakePlayerTouched < 100) {
				spawnRandom();
				spawnRandom();
			}
		}
	}

	public void logProof(String msg) {
		proof.add(msg);
	}

	public void saveProof() {
		if(mustToBeSaved) {
			mustToBeSaved = false;
			Adapter.getAdapter().getAccountManager().save(getUUID());
		}
		if (proof.size() == 0)
			return;
		try {
			File temp = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator
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

	private void destroy() {
		saveProof();
		UUID playerId = getUUID();
		NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
		accountManager.save(playerId);
		accountManager.dispose(playerId);
	}

	public void spawnCircle(double d, Location loc) {
		for (double u = 0; u < 360; u += d) {
			double z = Math.cos(u) * d, x = Math.sin(u) * d;
			loc.add(x, 1, z);
			// EFFECT
			// p.getWorld().playEffect(loc, Effect.TILE_DUST, 1);
			loc.subtract(x, 1, z);
		}
	}

	@Override
	public String getName() {
		return getPlayer().getName();
	}

	@Override
	public void kickPlayer(String reason, String time, String by, boolean def) {
		getPlayer().kickPlayer(Messages.getMessage(getPlayer(), "ban.kick_" + (def ? "def" : "time"), "%reason%",
				reason, "%time%", String.valueOf(time), "%by%", by));
	}

	public boolean isOnGround() {
		return SpigotNegativity.isBuggedGroundVersion ? isOnGround : p.get().isOnGround();
	}
	
	public void setOnGround(boolean b) {
		this.isOnGround = b;
	}
	
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
		for (ItemStack item : getPlayer().getInventory().getArmorContents())
			if (item != null && item.getType().name().contains("ELYTRA"))
				return true;
		return false;
	}

	public boolean isTargetByIronGolem() {
		for(Entity et : getPlayer().getWorld().getEntities())
			if(et instanceof IronGolem)
				if(((IronGolem) et).getTarget() != null && ((IronGolem) et).getTarget().equals(getPlayer()))
					return true;
		return false;
	}
	
	public boolean hasPotionEffect(String effectName) {
		try {
			PotionEffectType potionEffect = PotionEffectType.getByName(effectName);
			if(potionEffect != null) // If optionEffect is null, it doesn't exist in this version
				return getPlayer().hasPotionEffect(potionEffect);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static SpigotNegativityPlayer getNegativityPlayer(Player p) {
		return players.computeIfAbsent(p.getUniqueId(), id -> new SpigotNegativityPlayer(p));
	}

	public static SpigotNegativityPlayer getCached(UUID playerId) {
		return players.get(playerId);
	}

	public static void removeFromCache(UUID playerId) {
		SpigotNegativityPlayer cached = players.remove(playerId);
		if (cached != null) {
			cached.destroy();
		}
	}
}
