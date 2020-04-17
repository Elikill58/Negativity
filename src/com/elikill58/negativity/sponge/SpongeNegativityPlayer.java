package com.elikill58.negativity.sponge;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.VelocityData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.listeners.PlayerCheatEvent;
import com.elikill58.negativity.sponge.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.sponge.precogs.NegativityBypassTicket;
import com.elikill58.negativity.sponge.protocols.ForceFieldProtocol;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class SpongeNegativityPlayer extends NegativityPlayer {

	private static final Map<UUID, SpongeNegativityPlayer> PLAYERS_CACHE = new HashMap<>();

	public static ArrayList<Player> INJECTED = new ArrayList<>();
	private ArrayList<Cheat> ACTIVE_CHEAT = new ArrayList<>();
	public HashMap<Cheat, Integer> WARNS = new HashMap<>();
	public HashMap<String, String> MODS = new HashMap<>();
	public ArrayList<PotionEffect> POTION_EFFECTS = new ArrayList<>();
	public ArrayList<FakePlayer> FAKE_PLAYER = new ArrayList<>();
	public Map<Cheat, List<PlayerCheatEvent.Alert>> pendingAlerts = new HashMap<>();
	private Player p = null;
	// Packets
	public int FLYING = 0, MAX_FLYING = 0, POSITION_LOOK = 0, KEEP_ALIVE = 0, POSITION = 0, BLOCK_PLACE = 0,
			BLOCK_DIG = 0, ARM = 0, USE_ENTITY = 0, ENTITY_ACTION = 0, ALL = 0;
	// warns & other
	public int BETTER_CLICK = 0, LAST_CLICK = 0, ACTUAL_CLICK = 0, SEC_ACTIVE = 0;
	public int movementsOnWater;
	// setBack
	public int NO_FALL_DAMAGE = 0, BYPASS_SPEED = 0, IS_LAST_SEC_BLINK = 0, SPEED_NB = 0, SPIDER_SAME_DIST = 0;
	public double lastYDiff = -3.142654, lastSpiderDistance, lastDistanceFastStairs = 0;
	public long TIME_OTHER_KEEP_ALIVE = 0, TIME_INVINCIBILITY = 0, LAST_SHOT_BOW = 0, LAST_REGEN = 0, LAST_BLOCK_BREAK = 0,
			LAST_CLICK_INV = 0, LAST_BLOCK_PLACE = 0, TIME_REPORT = 0;
	public String LAST_OTHER_KEEP_ALIVE;
	public boolean IS_LAST_SEC_SNEAK = false, bypassBlink = false,
			isFreeze = false, isUsingSlimeBlock = false, already_blink = false, wasSneaking = false,
			isJumpingWithBlock = false, isOnLadders = false, lastClickInv = false, flyNotMovingY = false;
	public FlyingReason flyingReason = FlyingReason.REGEN;
	public ItemType eatMaterial = ItemTypes.AIR;
	public Path proofFile;
	private ConfigurationNode config;
	private HoconConfigurationLoader configLoader;
	private final List<String> proofs = new ArrayList<>();
	public Minerate mineRate;
	public boolean isInFight = false;
	public Task fightTask = null;
	public int fakePlayerTouched = 0;
	public long timeStartFakePlayer = 0;
	public Location<World> lastSpiderLoc = null;

	public boolean justDismounted = false;

	public SpongeNegativityPlayer(Player p) {
		super(p.getUniqueId());
		this.p = p;
		this.mineRate = new Minerate(this);

		String uuidString = p.getUniqueId().toString();
		try {
			Path userDir = SpongeNegativity.getInstance().getDataFolder().resolve("user");
			Path proofDir = userDir.resolve("proof");
			proofFile = proofDir.resolve(uuidString + ".txt");
			Files.createDirectories(proofDir);
			Path userFile = userDir.resolve(uuidString + ".yml");
			config = (configLoader = HoconConfigurationLoader.builder().setPath(userFile).build()).load();
			ConfigurationNode cheatsNode = config.getNode("cheats");
			for (Cheat cheat : Cheat.values()) {
				String cheatId = cheat.getKey().toLowerCase();
				ConfigurationNode cheatNode = cheatsNode.getNode(cheatId);
				if (cheatNode.isVirtual()) {
					cheatNode.setValue(0);
					WARNS.put(cheat, 0);
				} else {
					WARNS.put(cheat, cheatNode.getInt());
				}
			}
			ConfigurationNode minerateNode = config.getNode("minerate");
			for(MinerateType mt : MinerateType.values()) {
				ConfigurationNode tempNode = minerateNode.getNode(mt.getName().toLowerCase());
				if (tempNode.isVirtual()) {
					tempNode.setValue(0);
					mineRate.setMine(mt, 0);
				} else {
					mineRate.setMine(mt, tempNode.getInt());
				}
			}
			configLoader.save(config);
		} catch (AccessDeniedException e) {
			System.out.println("[Negativity - SpongeNegativityPlayer] The access is denied for file: " + e.getFile() + " (specific reason: " + e.getReason() + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initFmlMods() {
		if (SpongeForgeSupport.isOnSpongeForge) {
			MODS = SpongeForgeSupport.getClientMods(p);
		} else {
			sendFmlPacket((byte) -2, (byte) 0);
			sendFmlPacket((byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
			sendFmlPacket((byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
		}
	}

	private void sendFmlPacket(byte... data) {
		if(SpongeNegativity.fmlChannel == null)
			return;
		SpongeNegativity.fmlChannel.sendTo(p, (payload) -> {
			payload.writeBytes(data);
		});
	}

	public Player getPlayer() {
		return p;
	}

	public String getIP() {
		return getPlayer().getConnection().getAddress().getAddress().getHostAddress();
	}

	public boolean hasDetectionActive(Cheat c) {
		return ACTIVE_CHEAT.contains(c) && !hasBypassTicket(c);
	}

	public ArrayList<Cheat> getActiveCheat() {
		return ACTIVE_CHEAT;
	}

	public void logProof(String msg) {
		proofs.add(msg);
	}

	public void saveData() {
		ConfigurationNode cheatsNode = config.getNode("cheats");
		for (Map.Entry<Cheat, Integer> warn : WARNS.entrySet()) {
			String cheatId = warn.getKey().getKey().toLowerCase();
			cheatsNode.getNode(cheatId).setValue(warn.getValue());
		}
		ConfigurationNode minerateNode = config.getNode("minerate");
		for (MinerateType mt : MinerateType.values()) {
			minerateNode.getNode(mt.getName().toLowerCase()).setValue(mineRate.getMinerateType(mt));
		}

		config.getNode("lang").setValue(getAccount().getLang());
		try {
			configLoader.save(config);
		} catch (IOException e) {
			SpongeNegativity.getInstance().getLogger().error("Unable to save data of player " + p.getName(), e);
		}

		if (!proofs.isEmpty()) {
			try {
				Files.createDirectories(proofFile.getParent());
				Files.write(proofFile, proofs, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			} catch (IOException e) {
				SpongeNegativity.getInstance().getLogger().error("Unable to save proofs of player " + p.getName(), e);
			}
		}
	}

	public void updateMinerateInFile() {
		saveData();
	}

	public boolean hasBypassTicket(Cheat c) {
		if (!SpongeNegativity.hasPrecogs)
			return false;
		return NegativityBypassTicket.hasBypassTicket(c, p);
	}

	public int getWarn(Cheat c) {
		return WARNS.containsKey(c) ? WARNS.get(c) : 0;
	}

	public int getAllWarn(Cheat c) {
		return config.getNode("cheats").getNode(c.getKey().toLowerCase()).getInt() + getWarn(c);
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
			config.getNode("cheats").getNode(c.getKey().toLowerCase()).setValue(cheats);
			configLoader.save(config);
			WARNS.put(c, cheats);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clearPackets() {
		PlayerPacketsClearEvent event = new PlayerPacketsClearEvent(p, this);
		Sponge.getEventManager().post(event);
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

	public void startAnalyze(Cheat c) {
		if (!c.isActive())
			return;
		if (ACTIVE_CHEAT.contains(c))
			return;
		ACTIVE_CHEAT.add(c);
	}

	public void startAllAnalyze() {
		for (Cheat c : Cheat.values())
			startAnalyze(c);
	}

	private void destroy() {
		saveData();
	}

	public void makeAppearEntities() {
		if (!ACTIVE_CHEAT.contains(Cheat.forKey(CheatKeys.FORCEFIELD))
				|| SpongeNegativity.getConfig().getNode("cheats").getNode("forcefield").getNode("ghost_disabled").getBoolean())
			return;
		timeStartFakePlayer = System.currentTimeMillis();

		spawnRight();
		spawnLeft();
		spawnBehind();
	}

	public void removeFakePlayer(FakePlayer fp) {
		if (!FAKE_PLAYER.contains(fp))
			return;

		FAKE_PLAYER.remove(fp);
		long l = (System.currentTimeMillis() - timeStartFakePlayer);
		if (l >= 3000) {
			if (FAKE_PLAYER.size() == 0) {
				timeStartFakePlayer = 0;
				ForceFieldProtocol.manageForcefieldForFakeplayer(getPlayer(), this);
				fakePlayerTouched = 0;
			}
		} else {
			spawnRandom();
			spawnRandom();
		}
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
		Location<World> loc = getPlayer().getLocation().copy();
		Vector3d dir = getPlayer().getHeadRotation();
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
		Location<World> loc = getPlayer().getLocation().copy();
		Vector3d dir = getPlayer().getHeadRotation();
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
		Location<World> loc = getPlayer().getLocation().copy();
		Vector3d dir = getPlayer().getHeadRotation();
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
		Collection<Player> online = Sponge.getServer().getOnlinePlayers();
		if (online.size() <= 1) {
			return new Random().nextBoolean() ? "Elikill58" : "RedNesto";
		} else
			return online.stream().skip(new Random().nextInt(online.size())).findFirst().get().getName();
	}

	public boolean hasOtherThan(Location<?> loc, BlockType m) {
		try {
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
		} catch (Exception e) {

		}
		return false;
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

	public boolean hasOtherThanExtended(Location<World> loc, BlockType m) {
		Location<World> tempLoc = loc.copy();
		if (!loc.getBlock().getType().equals(m))
			return true;
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

	public boolean hasExtended(Location<World> loc, String m) {
		Location<World> tempLoc = loc.copy();
		if (loc.getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		loc = tempLoc;
		if (loc.add(0, 0, 2).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
				return true;
		if (loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		return false;
	}

	public float getFallDistance() {
		return p.getOrCreate(FallDistanceData.class).get().fallDistance().get();
	}

	public List<PotionEffect> getActiveEffects() {
		return p.getOrCreate(PotionEffectData.class).get().asList();
	}

	public ItemType getItemTypeInHand() {
		Optional<ItemStack> item = p.getItemInHand(HandTypes.MAIN_HAND);
		return item.isPresent() ? item.get().getType() : ItemTypes.AIR;
	}

	public boolean hasPotionEffect(PotionEffectType type) {
		return hasPotionEffect(type.getId());
	}

	public boolean hasPotionEffect(String typeName) {
		for (PotionEffect pe : getActiveEffects())
			if (pe.getType().getId().equals(typeName))
				return true;
		return false;
	}

	public Vector3d getVelocity() {
		return p.getOrCreate(VelocityData.class).get().velocity().get();
	}

	public boolean isFlying() {
		return p.getOrCreate(FlyingData.class).get().flying().get();
	}

	public boolean has(Location<?> loc, String... ms) {
		List<String> m = Arrays.asList(ms);
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(1, 0, 0).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getId()))
			return true;
		return false;
	}

	public boolean isBlock(ItemType m) {
		return m.getBlock().isPresent();
	}

	@Override
	public boolean hasDefaultPermission(String s) {
		return p.hasPermission(s);
	}

	@Override
	public double getLife() {
		return p.get(HealthData.class).get().health().get();
	}

	@Override
	public String getName() {
		return p.getName();
	}

	@Override
	public String getGameMode() {
		return p.gameMode().get().getName();
	}

	@Override
	public float getWalkSpeed() {
		return (float) (double) p.get(Keys.WALKING_SPEED).get();
	}

	@Override
	public int getLevel() {
		return p.get(Keys.EXPERIENCE_LEVEL).get();
	}

	@Override
	public void kickPlayer(String reason, String time, String by, boolean def) {
		p.kick(Messages.getMessage(p, "ban.kick_" + (def ? "def" : "time"), "%reason%", reason, "%time%",
				String.valueOf(time), "%by%", by));
	}

	@Override
	public void banEffect() {
		System.out.println("[SpongeNegativityPlayer] SOOOON");
	}

	public void fight() {
		isInFight = true;
		if (fightTask != null)
			fightTask.cancel();
		fightTask = Task.builder().delayTicks(40).execute(new Runnable() {
			@Override
			public void run() {
				isInFight = false;
			}
		}).submit(SpongeNegativity.INSTANCE);
	}

	public void unfight() {
		isInFight = false;
		if (fightTask != null)
			fightTask.cancel();
		fightTask = null;
	}
	
	public boolean hasThorns(Player p) {
		if(hasThornsForItem(p.getHelmet().orElse(null)))
			return true;
		if(hasThornsForItem(p.getChestplate().orElse(null)))
			return true;
		if(hasThornsForItem(p.getLeggings().orElse(null)))
			return true;
		if(hasThornsForItem(p.getBoots().orElse(null)))
			return true;
		return false;
	}
	
	private boolean hasThornsForItem(ItemStack item) {
		if(item == null)
			return false;
		Optional<EnchantmentData> opt = item.get(EnchantmentData.class);
		if(opt.isPresent())
			if(opt.get().contains(Enchantment.of(EnchantmentTypes.THORNS, 1)))
				return true;
		return false;
	}

	@Override
	public boolean isOp() {
		return false;
	}
	
	public boolean hasLineOfSight(Entity entity) {
		World w = p.getWorld();
		Vector3d vec3d = Utils.getPlayerVec(p), vec3d1 = Utils.getEntityVec(entity);
		if (!Double.isNaN(vec3d.getX()) && !Double.isNaN(vec3d.getY()) && !Double.isNaN(vec3d.getZ())) {
			if (!Double.isNaN(vec3d1.getX()) && !Double.isNaN(vec3d1.getY()) && !Double.isNaN(vec3d1.getZ())) {
				int posX = UniversalUtils.floor(vec3d.getX());
				int posY = UniversalUtils.floor(vec3d.getY());
				int posZ = UniversalUtils.floor(vec3d.getZ());
				int vecX = UniversalUtils.floor(vec3d1.getX());
				int vecY = UniversalUtils.floor(vec3d1.getY());
				int vecZ = UniversalUtils.floor(vec3d1.getZ());
				Vector3i vector = new Vector3i(posX, posY, posZ);
				if (!w.getBlock(vector).getType().equals(BlockTypes.AIR))
					if(hasMovingPosition(w, vector, vec3d, vec3d1))
						return false;
				
				int i = 200;
				while (i-- >= 0) {
					if (Double.isNaN(vec3d.getX()) || Double.isNaN(vec3d.getY()) || Double.isNaN(vec3d.getZ()))
						return true;
					
					if ((posX == vecX) && (posY == vecY) && (posZ == vecZ))
						return true;
					
					boolean movingX = true, movingY = true, movingZ = true;
					double d0 = 999.0D, d1 = 999.0D, d2 = 999.0D;
					double d3 = 999.0D, d4 = 999.0D, d5 = 999.0D;
					double d6 = vec3d1.getX() - vec3d.getX();
					double d7 = vec3d1.getY() - vec3d.getY();
					double d8 = vec3d1.getZ() - vec3d.getZ();
					if (vecX > posX) {
						d0 = posX + 1.0D;
					} else if (vecX < posX) {
						d0 = posX + 0.0D;
					} else
						movingX = false;
					
					if (vecY > posY) {
						d1 = posY + 1.0D;
					} else if (vecY < posY) {
						d1 = posY + 0.0D;
					} else
						movingY = false;
					
					if (vecZ > posZ) {
						d2 = posZ + 1.0D;
					} else if (vecZ < posZ) {
						d2 = posZ + 0.0D;
					} else
						movingZ = false;

					if (movingX) {
						d3 = (d0 - vec3d.getX()) / d6;
						if (d3 == -0.0D)
							d3 = -1.0E-4D;
					}
					if (movingY) {
						d4 = (d1 - vec3d.getY()) / d7;
						if (d4 == -0.0D)
							d4 = -1.0E-4D;
					}
					if (movingZ) {
						d5 = (d2 - vec3d.getZ()) / d8;
						if (d5 == -0.0D)
							d5 = -1.0E-4D;
					}
					Direction direction;
					if ((d3 < d4) && (d3 < d5)) {
						direction = vecX > posX ? Direction.WEST : Direction.EAST;
						vec3d = new Vector3d(d0, vec3d.getY() + d7 * d3, vec3d.getZ() + d8 * d3);
					} else if (d4 < d5) {
						direction = vecY > posY ? Direction.DOWN : Direction.UP;
						vec3d = new Vector3d(vec3d.getX() + d6 * d4, d1, vec3d.getZ() + d8 * d4);
					} else {
						direction = vecZ > posZ ? Direction.NORTH : Direction.SOUTH;
						vec3d = new Vector3d(vec3d.getX() + d6 * d5, vec3d.getY() + d7 * d5, d2);
					}
					posX = UniversalUtils.floor(vec3d.getX()) - (direction == Direction.EAST ? 1 : 0);
					posY = UniversalUtils.floor(vec3d.getY()) - (direction == Direction.UP ? 1 : 0);
					posZ = UniversalUtils.floor(vec3d.getZ()) - (direction == Direction.SOUTH ? 1 : 0);
					vector = new Vector3i(posX, posY, posZ);
					if (!w.getBlock(vector).getType().equals(BlockTypes.AIR))
						if(hasMovingPosition(w, vector, vec3d, vec3d1))
							return false;
				}
				return true;
			}
			return true;
		}
		return true;
	}
	
	protected boolean hasMovingPosition(World world, Vector3i position, Vector3d vec3d, Vector3d vec3d1) {
		vec3d = vec3d.add(-position.getX(), -position.getY(), -position.getZ());
		vec3d1 = vec3d1.add(-position.getX(), -position.getY(), -position.getZ());
		Vector3d vec3minX = getVectorX(vec3d, vec3d1, 0);
		Vector3d vec3maxX = getVectorX(vec3d, vec3d1, 1);
		Vector3d vec3minY = getVectorY(vec3d, vec3d1, 0);
		Vector3d vec3maxY = getVectorY(vec3d, vec3d1, 1);
		Vector3d vec3minZ = getVectorZ(vec3d, vec3d1, 0);
		Vector3d vec3maxZ = getVectorZ(vec3d, vec3d1, 1);
		Vector3d objDirection = null;
		if ((vec3minX != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3minX) < vec3d.distanceSquared(objDirection)))) {
			objDirection = vec3minX;
		}
		if ((vec3maxX != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3maxX) < vec3d.distanceSquared(objDirection)))) {
			objDirection = vec3maxX;
		}
		if ((vec3minY != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3minY) < vec3d.distanceSquared(objDirection)))) {
			objDirection = vec3minY;
		}
		if ((vec3maxY != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3maxY) < vec3d.distanceSquared(objDirection)))) {
			objDirection = vec3maxY;
		}
		if ((vec3minZ != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3minZ) < vec3d.distanceSquared(objDirection)))) {
			objDirection = vec3minZ;
		}
		if ((vec3maxZ != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3maxZ) < vec3d.distanceSquared(objDirection)))) {
			objDirection = vec3maxZ;
		}
		if (objDirection == null)
			return false;
		return true;
	}

	private Vector3d getVectorX(Vector3d main, Vector3d vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d1 * d1 < 1.0000000116860974E-7D) {
			return null;
		}
		double d4 = (paramDouble - main.getX()) / d1;
		if ((d4 < 0.0D) || (d4 > 1.0D)) {
			return null;
		}
		return new Vector3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	private Vector3d getVectorY(Vector3d main, Vector3d vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d2 * d2 < 1.0000000116860974E-7D) {
			return null;
		}
		double d4 = (paramDouble - main.getY()) / d2;
		if ((d4 < 0.0D) || (d4 > 1.0D)) {
			return null;
		}
		return new Vector3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	private Vector3d getVectorZ(Vector3d main, Vector3d vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d3 * d3 < 1.0000000116860974E-7D) {
			return null;
		}
		double d4 = (paramDouble - main.getZ()) / d3;
		if ((d4 < 0.0D) || (d4 > 1.0D)) {
			return null;
		}
		return new Vector3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	public static SpongeNegativityPlayer getNegativityPlayer(Player player) {
		return PLAYERS_CACHE.computeIfAbsent(player.getUniqueId(), id -> new SpongeNegativityPlayer(player));
	}

	public static void removeFromCache(Player player) {
		removeFromCache(player.getUniqueId());
	}

	public static void removeFromCache(UUID playerId) {
		SpongeNegativityPlayer nPlayer = PLAYERS_CACHE.remove(playerId);
		if (nPlayer != null) {
			nPlayer.destroy();
		}
	}
}
