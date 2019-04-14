package com.elikill58.negativity.spigot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.elikill58.negativity.spigot.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.spigot.protocols.ForceFieldProtocol;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.Version;

public class SpigotNegativityPlayer extends NegativityPlayer {

	private static HashMap<UUID, SpigotNegativityPlayer> players = new HashMap<>();

	public static ArrayList<UUID> INJECTED = new ArrayList<>();
	public ArrayList<Cheat> ACTIVE_CHEAT = new ArrayList<>();
	public ArrayList<FakePlayer> FAKE_PLAYER = new ArrayList<>();
	public HashMap<Cheat, Integer> WARNS = new HashMap<>();
	public HashMap<String, String> MODS = new HashMap<>();
	public ArrayList<PotionEffect> POTION_EFFECTS = new ArrayList<>();
	private Player p = null;
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
	public boolean PACKET_ANALYZE_STARTED = false, isInWater = false, isOnWater = false, FALL = false,
			KEEP_ALIVE_BEFORE = false, IS_LAST_SEC_SNEAK = false, bypassBlink = false, isFreeze = false,
			isInvisible = false, slime_block = false, already_blink = false, isJumpingWithBlock = false,
			isOnLadders = false, lastClickInv = false;
	public FlyingReason flyingReason = FlyingReason.REGEN;
	public Material eatMaterial = Material.AIR, lastClick = Material.AIR;
	public YamlConfiguration file;
	public File directory, configFile;
	public List<String> proof = new ArrayList<>();
	public Minerate mineRate;
	public boolean isInFight = false;
	public BukkitTask fightTask = null;
	public int fakePlayerTouched = 0;
	public long timeStartFakePlayer = 0;

	public SpigotNegativityPlayer(Player p) {
		super(p.getUniqueId());
		this.p = p;
		this.uuid = p.getUniqueId();
		this.mineRate = new Minerate();
		players.put(p.getUniqueId(), this);
		loadBanRequest();
		File directory = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator + "user"
				+ File.separator + "proof" + File.separator);
		directory.mkdirs();
		try {
			file = YamlConfiguration.loadConfiguration(configFile = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator + "user"
					+ File.separator + uuid + ".yml"));
			file.addDefault("lang", TranslatedMessages.DEFAULT_LANG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLang(file.getString("lang"));
		for(Cheat c : Cheat.values())
			WARNS.put(c, file.getInt("cheats." + c.name().toLowerCase()));
		initMods(p);
	}

	public SpigotNegativityPlayer(OfflinePlayer op) {
		super(op.getUniqueId());
		this.op = op;
		this.uuid = op.getUniqueId();
		this.mineRate = new Minerate();
		players.put(this.uuid, this);
		loadBanRequest();
		File tempfile = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator + "user"
				+ File.separator + uuid + ".txt");
		File directory = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator + "user"
				+ File.separator + "proof" + File.separator);
		directory.mkdirs();
		try {
			if(!tempfile.exists())
				tempfile.createNewFile();
			file = YamlConfiguration.loadConfiguration(configFile = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator + "user"
					+ File.separator + uuid + ".yml"));
			file.addDefault("lang", TranslatedMessages.DEFAULT_LANG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(Cheat c : Cheat.values())
			WARNS.put(c, file.getInt("cheats." + c.name().toLowerCase()));
		setLang(file.getString("lang"));
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
		return p;
	}

	public OfflinePlayer getOfflinePlayer() {
		return op;
	}

	@Override
	public int getWarn(Cheat c) {
		return WARNS.containsKey(c) ? WARNS.get(c) : 0;
	}

	public int getAllWarn(Cheat c) {
		return file.getInt("cheats." + c.name().toLowerCase()) + getWarn(c);
	}

	public void addWarn(Cheat c) {
		if (System.currentTimeMillis() < TIME_INVINCIBILITY)
			return;
		if (WARNS.containsKey(c))
			WARNS.put(c, WARNS.get(c) + 1);
		else
			WARNS.put(c, 1);
		setWarn(c, WARNS.get(c));
	}

	public void setWarn(Cheat c, int cheats) {
		try {
			file.set("cheats." + c.name().toLowerCase(), cheats);
			file.save(configFile);
			WARNS.put(c, cheats);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clearPackets() {
		PlayerPacketsClearEvent event = new PlayerPacketsClearEvent(p, this);
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
	public String getUUID() {
		return uuid.toString();
	}

	public void startAnalyze(Cheat c) {
		ACTIVE_CHEAT.add(c);
		if(c.needPacket() && !INJECTED.contains(p.getUniqueId()))
			INJECTED.add(p.getUniqueId());
		if(c.name().equalsIgnoreCase("FORCEFIELD"))
			makeAppearEntities();
	}

	public void startAllAnalyze() {
		INJECTED.add(p.getUniqueId());
		for (Cheat c : Cheat.values())
			startAnalyze(c);
	}

	public void makeAppearEntities() {
		if(!ACTIVE_CHEAT.contains(Cheat.getCheatFromString("FORCEFIELD").get()))
			return;
		timeStartFakePlayer = System.currentTimeMillis();

		spawnRight();
		spawnLeft();
		spawnBehind();
	}

	public void spawnRandom() {
		int choice = new Random().nextInt(3);
		if(choice == 0)
			spawnRight();
		else if(choice == 1)
			spawnBehind();
		else
			spawnLeft();
	}

	private void spawnRight() {
		Location loc = p.getLocation().clone();
		Vector dir = p.getEyeLocation().getDirection();
		double x = dir.getX(), z = dir.getZ();
		if(x >= 0 && z >= 0) {
			loc.add(-1, 0, 1);
		} else if(x >= 0 && z <= 0) {
			loc.add(-1, 0, 0);
		} else if(x <= 0 && z >= 0) {
			loc.add(-1, 0, 0);
		} else if(x <= 0 && z <= 0) {
			loc.add(-1, 0, 1);
		}
		loc.add(0, 1, 0);
		FakePlayer fp = new FakePlayer(loc, Utils.getOnlinePlayers().get(new Random().nextInt(Utils.getOnlinePlayers().size() - 1)).getName()).show(p);
		FAKE_PLAYER.add(fp);
	}

	private void spawnLeft() {
		Location loc = p.getLocation().clone();
		Vector dir = p.getEyeLocation().getDirection();
		double x = dir.getX(), z = dir.getZ();
		if(x >= 0 && z >= 0) {
			loc.add(0, 0, -1);
		} else if(x >= 0 && z <= 0) {
			loc.add(-1, 0, 1);
		} else if(x <= 0 && z >= 0) {
			loc.add(1, 0, -1);
		} else if(x <= 0 && z <= 0) {
			loc.add(1, 0, 1);
		}
		loc.add(0, 1, 0);
		FakePlayer fp = new FakePlayer(loc, Utils.getOnlinePlayers().get(new Random().nextInt(Utils.getOnlinePlayers().size())).getName()).show(p);
		FAKE_PLAYER.add(fp);
	}

	private void spawnBehind() {
		Location loc = p.getLocation().clone();
		Vector dir = p.getEyeLocation().getDirection();
		double x = dir.getX(), z = dir.getZ();
		if(x >= 0 && z >= 0) {
			loc.add(1, 0, -1);
		} else if(x >= 0 && z <= 0) {
			loc.add(1, 0, 1);
		} else if(x <= 0 && z >= 0) {
			loc.add(1, 0, 1);
		} else if(x <= 0 && z <= 0) {
			loc.add(1, 0, -1);
		}
		loc.add(0, 1, 0);
		FakePlayer fp = new FakePlayer(loc, Utils.getOnlinePlayers().get(new Random().nextInt(Utils.getOnlinePlayers().size())).getName()).show(p);
		FAKE_PLAYER.add(fp);
	}

	public void removeFakePlayer(FakePlayer fp) {
		if(!FAKE_PLAYER.contains(fp))
			return;

		FAKE_PLAYER.remove(fp);
		long l = (System.currentTimeMillis() - timeStartFakePlayer);
		if(l >= 3000) {
			if(FAKE_PLAYER.size() == 0) {
				timeStartFakePlayer = 0;
				ForceFieldProtocol.manageForcefieldForFakeplayer(p, this);
			}
		} else {
			spawnRandom();
			spawnRandom();
		}
	}

	public void logProof(Timestamp stamp, String msg) {
		proof.add(msg);
		try {
			File temp = new File(SpigotNegativity.getInstance().getDataFolder().getAbsolutePath() + File.separator + "user"
					+ File.separator + "proof" + File.separator + uuid + ".txt");
			if(!temp.exists())
				temp.createNewFile();
			Files.write(temp.toPath(), (msg + "\n").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg, String... arg) {
		String message = Messages.getMessage(p, msg, arg);
		if(!message.equalsIgnoreCase(msg))
			p.sendMessage(message);
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

	public void destroy(boolean isBan) {
		players.remove(p.getUniqueId());
		if (isBan) {
			Entity et = p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
			Firework fire = (Firework) et;
			FireworkMeta fireMeta = fire.getFireworkMeta();
			fireMeta.addEffect(FireworkEffect.builder().with(Type.CREEPER).withColor(Color.GREEN).build());
			fireMeta.setPower(2);
			fire.setFireworkMeta(fireMeta);
			fire.detonate();
			Location loc = p.getLocation();
			loc.add(0, 1, 0);
			double more = 0.1, max = 1.5;
			for (double d = 0; d < max; d += more) {
				spawnCircle(1, loc, p);
				loc.subtract(0, more, 0);
			}
		}
	}

	public boolean hasOtherThan(Location loc, Material m) {
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

	public void spawnCircle(double d, Location loc, Player p) {
		for (double u = 0; u < 360; u += d) {
			double z = Math.cos(u) * d, x = Math.sin(u) * d;
			loc.add(x, 1, z);
			// EFFECT
			// p.getWorld().playEffect(loc, Effect.TILE_DUST, 1);
			loc.subtract(x, 1, z);
		}
	}

	public boolean hasAntiKnockbackByPass() {
		if ((p.hasPotionEffect(PotionEffectType.SLOW) && p.getWalkSpeed() < 3.0F)
				|| (p.hasPotionEffect(PotionEffectType.SLOW_DIGGING) && p.getWalkSpeed() < 3.0F))
			return true;
		return false;
	}

	public boolean isBlock(Material m) {
		// for Last version blocks
		String mn = m.name();
		if (mn.equals("PRISMARINE") || mn.contains("_SHULKER_BOX") || mn.contains("BLOCK") || mn.contains("WOOD") || mn.contains("LOG") || mn.contains("WOOL") || mn.equals("PURPUR_BLOCK")
				|| mn.equals("END_BRICKS") || mn.equals("BEETROOT_BLOCK") || mn.equals("BONE_BLOCK") || mn.contains("STAINED") || mn.contains("CLAY"))
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

	public static boolean contains(Player p) {
		return players.containsKey(p.getUniqueId());
	}

	@Override
	public boolean hasDefaultPermission(String s) {
		return p.hasPermission(s);
	}

	@Override
	public double getLife() {
		return ((Damageable) p).getHealth();
	}

	@Override
	public String getName() {
		return p.getName();
	}

	@Override
	public String getGameMode() {
		return p.getGameMode().name();
	}

	@Override
	public float getWalkSpeed() {
		return p.getWalkSpeed();
	}

	@Override
	public int getLevel() {
		return p.getLevel();
	}

	@Override
	public void kickPlayer(String reason, String time, String by, boolean def) {
		p.kickPlayer(Messages.getMessage(p, "ban.kick_" + (def ? "def" : "time"), "%reason%", reason, "%time%",
				String.valueOf(time), "%by%", by));
	}

	public enum FlyingReason {
		POTION(Cheat.getCheatFromString("ANTIPOTION").get()), REGEN(Cheat.getCheatFromString("AUTOREGEN").get()), EAT(Cheat.getCheatFromString("AUTOEAT").get()), BOW(Cheat.getCheatFromString("FASTBOW").get());

		private Cheat c;

		FlyingReason(Cheat c) {
			this.c = c;
		}

		public Cheat getCheat() {
			return c;
		}
	}

	@Override
	public void banEffect() {
		int i = 2;
		Location loc = p.getLocation();
		World w = p.getWorld();
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
				if(Version.isNewerOrEquals(Version.getVersion(), Version.V1_13)) {
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
		if(fightTask != null)
			fightTask.cancel();
		fightTask = Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				isInFight = false;
			}
		}, 40);
	}

	public void unfight() {
		isInFight = false;
		if(fightTask != null)
			fightTask.cancel();
		fightTask = null;
	}
}
