package com.elikill58.negativity.spigot;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.elikill58.negativity.spigot.inventories.CheckMenuInventory;
import com.elikill58.negativity.spigot.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.spigot.protocols.ForceFieldProtocol;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;

public class SpigotNegativityPlayer extends NegativityPlayer {

	private static HashMap<UUID, SpigotNegativityPlayer> players = new HashMap<>();

	public static ArrayList<UUID> INJECTED = new ArrayList<>();
	public ArrayList<Cheat> ACTIVE_CHEAT = new ArrayList<>();
	public ArrayList<FakePlayer> FAKE_PLAYER = new ArrayList<>();
	public HashMap<Cheat, Integer> WARNS = new HashMap<>();
	public HashMap<String, String> MODS = new HashMap<>();
	public HashMap<String, Double> jesusLastY = new HashMap<>();
	public HashMap<Cheat, Integer> ALERT_NOT_SHOWED = new HashMap<>();
	public ArrayList<PotionEffect> POTION_EFFECTS = new ArrayList<>();
	private WeakReference<Player> p;
	private OfflinePlayer op = null;
	private UUID uuid = null;
	// Packets
	public int FLYING = 0, MAX_FLYING = 0, POSITION_LOOK = 0, KEEP_ALIVE = 0, POSITION = 0, BLOCK_PLACE = 0,
			BLOCK_DIG = 0, ARM = 0, USE_ENTITY = 0, ENTITY_ACTION = 0, ALL = 0;
	// warns & other
	public int ONLY_KEEP_ALIVE = 0, NO_PACKET = 0, BETTER_CLICK = 0, LAST_CLICK = 0, ACTUAL_CLICK = 0, SEC_ACTIVE = 0;
	// setBack
	public int NO_FALL_DAMAGE = 0, BYPASS_SPEED = 0, IS_LAST_SEC_BLINK = 0, LAST_SLOT_CLICK = -1;
	public double lastY = -3.141592654;
	public long TIME_OTHER_KEEP_ALIVE = 0, TIME_INVINCIBILITY = 0, LAST_SHOT_BOW = 0, LAST_REGEN = 0,
			LAST_CLICK_INV = 0, LAST_BLOCK_PLACE = 0, LAST_DAMAGE_RECEIVE = 0, TIME_REPORT = 0;
	public String LAST_OTHER_KEEP_ALIVE;
	public boolean PACKET_ANALYZE_STARTED = false/*, isInWater = false, isOnWater = false*/, FALL = false,
			KEEP_ALIVE_BEFORE = false, IS_LAST_SEC_SNEAK = false, bypassBlink = false, isFreeze = false,
			isInvisible = false, slime_block = false, already_blink = false, isJumpingWithBlock = false,
			isOnLadders = false, lastClickInv = false, already_jigsaw = false, jesusState = true;
	public FlyingReason flyingReason = FlyingReason.REGEN;
	public Material eatMaterial = Material.AIR, lastClick = Material.AIR;
	public YamlConfiguration file;
	public Location lastSpiderLoc;
	public double lastSpiderDistance;
	public File directory, configFile;
	public List<String> proof = new ArrayList<>();
	public Minerate mineRate;
	public boolean isInFight = false;
	public BukkitTask fightTask = null;
	public int fakePlayerTouched = 0;
	public long timeStartFakePlayer = 0, launchFirework = 0;

	public SpigotNegativityPlayer(Player p) {
		super(p.getUniqueId());
		this.p = new WeakReference<>(p);
		this.uuid = p.getUniqueId();
		this.mineRate = new Minerate(this);
		players.put(p.getUniqueId(), this);
		File directory = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator
				+ "user" + File.separator + "proof" + File.separator);
		directory.mkdirs();
		try {
			file = YamlConfiguration.loadConfiguration(
					configFile = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath()
							+ File.separator + "user" + File.separator + uuid + ".yml"));
			file.set("playername", p.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Cheat c : Cheat.values())
			WARNS.put(c, file.getInt("cheats." + c.getKey().toLowerCase()));
		initMods(p);
	}

	public SpigotNegativityPlayer(OfflinePlayer op) {
		super(op.getUniqueId());
		this.op = op;
		this.uuid = op.getUniqueId();
		this.mineRate = new Minerate(this);
		players.put(this.uuid, this);
		File tempfile = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator
				+ "user" + File.separator + uuid + ".txt");
		File directory = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator
				+ "user" + File.separator + "proof" + File.separator);
		directory.mkdirs();
		try {
			if (!tempfile.exists())
				tempfile.createNewFile();
			file = YamlConfiguration.loadConfiguration(
					configFile = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath()
							+ File.separator + "user" + File.separator + uuid + ".yml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Cheat c : Cheat.values())
			WARNS.put(c, file.getInt("cheats." + c.getKey().toLowerCase()));
		for (MinerateType mt : MinerateType.values())
			mineRate.setMine(mt, file.getInt("minerate." + mt.getName().toLowerCase(), 0));
	}

	public void initMods(Player p) {
		Plugin pl = SpigotNegativity.getInstance();
		String channelName = Version.getVersion().isNewerOrEquals(Version.V1_13) ? "test:fml" : "FML|HS";
		p.sendPluginMessage(pl, channelName, new byte[] { -2, 0 });
		p.sendPluginMessage(pl, channelName, new byte[] { 0, 2, 0, 0, 0, 0 });
		p.sendPluginMessage(pl, channelName, new byte[] { 2, 0, 0, 0, 0 });
	}

	@Override
	public Player getPlayer() {
		Player cached = p != null ? p.get() : null;
		if (cached == null) {
			cached = Bukkit.getPlayer(uuid);
			if (p != null)
				p.clear();

			p = new WeakReference<>(cached);
		}
		return cached;
	}

	public OfflinePlayer getOfflinePlayer() {
		return op;
	}

	public String getIP() {
		return p.get().getAddress().getAddress().getHostAddress();
	}
	
	public void updateCheckMenu() {
		for (Player p : Inv.CHECKING.keySet()) {
			if (p.getOpenInventory() != null) {
				if (Utils.getInventoryTitle(p.getOpenInventory()).equals(Inv.NAME_CHECK_MENU))
					CheckMenuInventory.actualizeCheckMenu(p, Inv.CHECKING.get(p));
			}
		}
	}

	@Override
	public int getWarn(Cheat c) {
		return WARNS.containsKey(c) ? WARNS.get(c) : 0;
	}

	@Override
	public int getAllWarn(Cheat c) {
		return file.getInt("cheats." + c.getKey().toLowerCase());
	}

	@Deprecated
	public void addWarn(Cheat c) {
		addWarn(c, 100);
	}

	public void addWarn(Cheat c, int reliability) {
		if (System.currentTimeMillis() < TIME_INVINCIBILITY || c.getReliabilityAlert() > reliability)
			return;
		setWarn(c, WARNS.containsKey(c) ? WARNS.get(c) + 1 : 1);
	}

	public void setWarn(Cheat c, int cheats) {
		try {
			file.set("cheats." + c.getKey().toLowerCase(), cheats);
			// Temporary workaround to save language until we refactor player data
			// loading/saving
			file.set("lang", getAccount().getLang());
			file.save(configFile);
			WARNS.put(c, cheats);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setLang(String newLang) {
		try {
			file.set("lang", newLang);
			file.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateMinerateInFile() {
		try {
			for (MinerateType mt : MinerateType.values())
				file.set("minerate." + mt.getName().toLowerCase(), mineRate.getMinerateType(mt));
			file.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clearPackets() {
		PlayerPacketsClearEvent event = new PlayerPacketsClearEvent(getPlayer(), this);
		Bukkit.getPluginManager().callEvent(event);
		if (FLYING > MAX_FLYING)
			MAX_FLYING = FLYING;
		FLYING = 0;
		POSITION_LOOK = 0;
		KEEP_ALIVE = 0;
		POSITION = 0;
		BLOCK_PLACE = 0;
		BLOCK_DIG = 0;
		ARM = 0;
		USE_ENTITY = 0;
		ENTITY_ACTION = 0;
		ALL = 0;
	}
	
	@Override
	public boolean isOp() {
		return getPlayer().isOp();
	}
	
	@Override
	public void startAnalyze(Cheat c) {
		ACTIVE_CHEAT.add(c);
		if (c.needPacket() && !INJECTED.contains(getPlayer().getUniqueId()))
			INJECTED.add(getPlayer().getUniqueId());
		if (c.getKey().equalsIgnoreCase("FORCEFIELD")) {
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
	public String getReason(Cheat c) {
		String n = "";
		for(Cheat all : Cheat.values())
			if(getAllWarn(all) > 5)
				n = n + (n.equals("") ? "" : ", ") + all.getName();
		if(!n.contains(c.getName()))
			n = n + (n.equals("") ? "" : ", ") + c.getName();
		return n;
	}

	public void makeAppearEntities() {
		if (!ACTIVE_CHEAT.contains(Cheat.fromString("FORCEFIELD").get())
				|| SpigotNegativity.getInstance().getConfig().getBoolean("cheats.forcefield.ghost_disabled"))
			return;
		timeStartFakePlayer = System.currentTimeMillis();

		spawnRight();
		spawnLeft();
		spawnBehind();
	}

	public void spawnRandom() {
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
		} else
			return online.get(new Random().nextInt(online.size())).getName();
	}

	public List<FakePlayer> getFakePlayers() {
		return new ArrayList<>(FAKE_PLAYER);
	}

	public void removeFakePlayer(FakePlayer fp, boolean detected) {
		if (!FAKE_PLAYER.contains(fp))
			return;
		FAKE_PLAYER.remove(fp);
		if(!detected)
			return;
		fakePlayerTouched++;
		System.out.println("[Negativity - SpigotNegativityPlayer] Removing FP " + fp.getEntityId() + " > " + fakePlayerTouched);
		long diff = System.currentTimeMillis() - timeStartFakePlayer;
		double diffSec = diff / 1000;
		if(fakePlayerTouched >= 20 && fakePlayerTouched >= diffSec) {
			SpigotNegativity.alertMod(ReportType.VIOLATION, getPlayer(), Cheat.fromString("FORCEFIELD").get(), Utils.parseInPorcent(fakePlayerTouched * 10 * (1 / diffSec)), fakePlayerTouched + " touched in " + diffSec + " seconde(s)", fakePlayerTouched + " hit in " + (int) (diffSec) + " seconde(s)", fakePlayerTouched + " hit in " + (int) (diffSec) + " seconde(s)");
		} else if(fakePlayerTouched >= 5 && fakePlayerTouched >= diffSec) {
			SpigotNegativity.alertMod(ReportType.WARNING, getPlayer(), Cheat.fromString("FORCEFIELD").get(), Utils.parseInPorcent(fakePlayerTouched * 10 * (1 / diffSec)), fakePlayerTouched + " touched in " + diffSec + " seconde(s)", fakePlayerTouched + " hit in " + (int) (diffSec) + " seconde(s)", fakePlayerTouched + " hit in " + (int) (diffSec) + " seconde(s)");
		}
		long l = (System.currentTimeMillis() - timeStartFakePlayer);
		if (l >= 3000) {
			if (FAKE_PLAYER.size() == 0) {
				timeStartFakePlayer = 0;
				ForceFieldProtocol.manageForcefieldForFakeplayer(getPlayer(), this);
				fakePlayerTouched = 0;
			}
		} else if(fakePlayerTouched < 100) {
			spawnRandom();
		} else {
			timeStartFakePlayer = 0;
			ForceFieldProtocol.manageForcefieldForFakeplayer(getPlayer(), this);
			fakePlayerTouched = 0;
		}
	}

	public void logProof(String msg) {
		proof.add(msg);
	}

	public void saveProof() {
		if (proof.size() == 0)
			return;
		try {
			File temp = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator
					+ "user" + File.separator + "proof" + File.separator + uuid + ".txt");
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

	public void sendMessage(String msg, String... arg) {
		String message = Messages.getMessage(getPlayer(), msg, arg);
		if (!message.equalsIgnoreCase(msg))
			getPlayer().sendMessage(message);
	}

	public void destroy(boolean isBan) {
		players.remove(uuid);
		saveProof();
		Adapter.getAdapter().invalidateAccount(getUUID());
		if (isBan) {
			Entity et = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), EntityType.FIREWORK);
			Firework fire = (Firework) et;
			FireworkMeta fireMeta = fire.getFireworkMeta();
			fireMeta.addEffect(FireworkEffect.builder().with(Type.CREEPER).withColor(Color.GREEN).build());
			fireMeta.setPower(2);
			fire.setFireworkMeta(fireMeta);
			fire.detonate();
			Location loc = getPlayer().getLocation();
			loc.add(0, 1, 0);
			double more = 0.1, max = 1.5;
			for (double d = 0; d < max; d += more) {
				spawnCircle(1, loc);
				loc.subtract(0, more, 0);
			}
		}
	}

	public boolean hasOtherThanExtended(Location loc, Material m) {
		Location tempLoc = loc.clone();
		if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().equals(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
			return true;
		loc = tempLoc;
		if (!loc.add(0, 0, 2).getBlock().getType().equals(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
			return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, -1).getBlock().getType().equals(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(-1, 0, 0).getBlock().getType().equals(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
				return true;
		if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
			return true;
		return false;
	}

	public boolean hasOtherThan(Location loc, Material m) {
		return hasOtherThan(loc, m.name());
	}
	
	public boolean hasOtherThan(Location loc, String name) {
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(name))
			return true;
		return false;
	}

	public boolean has(Location loc, Material... ms) {
		List<Material> m = Arrays.asList(ms);
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		return false;
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

	public boolean hasAntiKnockbackByPass() {
		if ((getPlayer().hasPotionEffect(PotionEffectType.SLOW) && getPlayer().getWalkSpeed() < 3.0F)
				|| (getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING) && getPlayer().getWalkSpeed() < 3.0F))
			return true;
		return false;
	}

	public boolean isBlock(Material m) {
		// for Last version blocks
		String mn = m.name();
		if (mn.equals("PRISMARINE") || mn.contains("_SHULKER_BOX") || mn.contains("BLOCK") || mn.contains("WOOD")
				|| mn.contains("LOG") || mn.contains("WOOL") || mn.equals("PURPUR_BLOCK") || mn.equals("END_BRICKS")
				|| mn.equals("BEETROOT_BLOCK") || mn.equals("BONE_BLOCK") || mn.contains("STAINED")
				|| mn.contains("CLAY"))
			return true;
		switch (m) {
		case ANVIL:
		case APPLE:
		case ARROW:
		case BEACON:
		case BRICK:
		case COAL_BLOCK:
		case COBBLESTONE:
		case DIRT:
		case EMERALD_BLOCK:
		case FURNACE:
		case GOLD_BLOCK:
		case GRASS:
		case HAY_BLOCK:
		case HOPPER:
		case IRON_AXE:
		case IRON_BLOCK:
		case IRON_ORE:
		case JACK_O_LANTERN:
		case JUKEBOX:
		case LADDER:
		case LAPIS_BLOCK:
		case MOSSY_COBBLESTONE:
		case NETHER_BRICK:
		case NOTE_BLOCK:
		case OBSIDIAN:
		case QUARTZ_BLOCK:
		case REDSTONE:
		case REDSTONE_BLOCK:
		case REDSTONE_ORE:
		case RED_MUSHROOM:
		case SADDLE:
		case SAND:
		case SANDSTONE:
		case SPONGE:
		case STONE:
		case TNT:
			break;
		default:
			return false;
		}
		return true;
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
		getPlayer().kickPlayer(Messages.getMessage(getPlayer(), "ban.kick_" + (def ? "def" : "time"), "%reason%",
				reason, "%time%", String.valueOf(time), "%by%", by));
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
		for (ItemStack item : getPlayer().getInventory().getArmorContents())
			if (item != null && item.getType().name().contains("ELYTRA"))
				return true;
		return false;
	}

	public boolean isTargetByIronGolem() {
		for(Entity et : getPlayer().getWorld().getEntities())
			if(et instanceof IronGolem)
				if(((IronGolem) et).getTarget() != null && ((IronGolem) et).getTarget().equals((LivingEntity) getPlayer()))
					return true;
		return false;
	}
	
	public static SpigotNegativityPlayer getNegativityPlayer(Player p) {
		if (players.containsKey(p.getUniqueId()))
			return players.get(p.getUniqueId());
		else
			return new SpigotNegativityPlayer(p);
	}

	public static SpigotNegativityPlayer getNegativityPlayer(OfflinePlayer p) {
		if (players.containsKey(p.getUniqueId()))
			return players.get(p.getUniqueId());
		else
			return new SpigotNegativityPlayer(p);
	}

	public static boolean contains(Player p) {
		return players.containsKey(p.getUniqueId());
	}

	public static void removeFromCache(UUID playerId, boolean isBan) {
		SpigotNegativityPlayer cached = players.get(playerId);
		if (cached != null) {
			cached.destroy(isBan);
		}
	}
}
