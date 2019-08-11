package com.elikill58.negativity.sponge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.FireworkEffectData;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.VelocityData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Firework;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Location;

import com.elikill58.negativity.sponge.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.sponge.precogs.NegativityBypassTicket;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.flowpowered.math.vector.Vector3d;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class SpongeNegativityPlayer extends NegativityPlayer {

	private static final Map<UUID, SpongeNegativityPlayer> PLAYERS_CACHE = new HashMap<>();

	public static ArrayList<Player> INJECTED = new ArrayList<>();
	private ArrayList<Cheat> ACTIVE_CHEAT = new ArrayList<>();
	public HashMap<Cheat, Integer> WARNS = new HashMap<>();
	public HashMap<String, String> MODS = new HashMap<>();
	public ArrayList<PotionEffect> POTION_EFFECTS = new ArrayList<>();
	private Player p = null;
	private UUID uuid = null;
	// Packets
	public int FLYING = 0, MAX_FLYING = 0, POSITION_LOOK = 0, KEEP_ALIVE = 0, POSITION = 0, BLOCK_PLACE = 0,
			BLOCK_DIG = 0, ARM = 0, USE_ENTITY = 0, ENTITY_ACTION = 0, ALL = 0;
	// warns & other
	public int ONLY_KEEP_ALIVE = 0, NO_PACKET = 0, BETTER_CLICK = 0, LAST_CLICK = 0, ACTUAL_CLICK = 0, SEC_ACTIVE = 0;
	// setBack
	public int NO_FALL_DAMAGE = 0, BYPASS_SPEED = 0, IS_LAST_SEC_BLINK = 0, LAST_SLOT_CLICK = -1;
	public double lastY = -3.142654;
	public long TIME_OTHER_KEEP_ALIVE = 0, TIME_INVINCIBILITY = 0, LAST_SHOT_BOW = 0, LAST_REGEN = 0,
			LAST_CLICK_INV = 0, LAST_BLOCK_PLACE = 0, TIME_REPORT = 0;
	public String LAST_OTHER_KEEP_ALIVE;
	public boolean isInWater = false, isOnWater = false, IS_LAST_SEC_SNEAK = false, bypassBlink = false,
			isFreeze = false, slime_block = false, already_blink = false,
			isJumpingWithBlock = false, isOnLadders = false, lastClickInv = false, haveClick = false;
	public FlyingReason flyingReason = FlyingReason.REGEN;
	public ItemType eatMaterial = ItemTypes.AIR;
	public File file, directory, proofFile;
	private ConfigurationNode config;
	private HoconConfigurationLoader configLoader;
	public List<String> proof = new ArrayList<>();
	public Minerate mineRate;
	public boolean isInFight = false;
	public Task fightTask = null;

	public SpongeNegativityPlayer(Player p) {
		super(p.getUniqueId());
		this.p = p;
		this.uuid = p.getUniqueId();
		this.mineRate = new Minerate(this);

		try {
			directory = new File(SpongeNegativity.getInstance().getDataFolder().toAbsolutePath() + File.separator
					+ "user" + File.separator + "proof" + File.separator);
			file = new File(SpongeNegativity.getInstance().getDataFolder().toAbsolutePath() + File.separator + "user"
					+ File.separator + uuid + ".yml");
			proofFile = new File(SpongeNegativity.getInstance().getDataFolder().toAbsolutePath() + File.separator
					+ "user" + File.separator + "proof" + File.separator + uuid + ".txt");
			directory.mkdirs();
			if (!proofFile.exists())
				proofFile.createNewFile();
			config = (configLoader = HoconConfigurationLoader.builder().setFile(file).build()).load();
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

	public boolean hasDetectionActive(Cheat c) {
		return ACTIVE_CHEAT.contains(c) && !hasBypassTicket(c);
	}

	public ArrayList<Cheat> getActiveCheat() {
		return ACTIVE_CHEAT;
	}

	public void logProof(Timestamp stamp, String msg) {
		proof.add(msg);
		try {
			Files.write(proofFile.toPath(), ("\n" + msg).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if (!Cheat.ALL.isActive())
			return;
		for (Cheat c : Cheat.values())
			startAnalyze(c);
	}

	private void destroy(boolean isBan) {
		saveData();
		if (isBan) {
			FireworkEffect fireEffect = FireworkEffect.builder().shape(FireworkShapes.CREEPER).color(Color.GREEN)
					.build();
			Location<?> loc = p.getLocation().copy();
			loc.add(0, 1, 0);
			double more = 0.1, max = 1.5;
			for (double d = 0; d < max; d += more) {
				spawnCircle(1, loc, p);
				loc.sub(0, more, 0);
				Firework fire = (Firework) p.getWorld().createEntity(EntityTypes.FIREWORK, loc.getPosition());
				fire.getOrCreate(FireworkEffectData.class).get().addElement(fireEffect);
				p.getWorld().spawnEntity(fire);
				fire.detonate();
			}
		}
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
		for (PotionEffect pe : getActiveEffects())
			if (pe.getType().equals(type))
				return true;
		return false;
	}

	public Vector3d getVelocity() {
		return p.getOrCreate(VelocityData.class).get().velocity().get();
	}

	public boolean isFlying() {
		return p.getOrCreate(FlyingData.class).get().flying().get();
	}

	public boolean has(Location<?> loc, ItemType... ms) {
		List<ItemType> m = Arrays.asList(ms);
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getItem().get()))
			return true;
		if (m.contains(loc.add(1, 0, 0).getBlock().getType().getItem().get()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType().getItem().get()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType().getItem().get()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType().getItem().get()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType().getItem().get()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType().getItem().get()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getItem().get()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getItem().get()))
			return true;
		return false;
	}

	public void spawnCircle(double d, Location<?> loc, Player p) {
		for (double u = 0; u < 360; u += d) {
			double z = Math.cos(u) * d, x = Math.sin(u) * d;
			loc.add(x, 1, z);
			// EFFECT
			// p.getWorld().playEffect(loc, Effect.TILE_DUST, 1);
			loc.sub(x, 1, z);
		}
	}

	public boolean hasAntiKnockbackByPass() {
		PotionEffectData potionData = p.getOrCreate(PotionEffectData.class).get();
		if (potionData.contains(PotionEffect.of(PotionEffectTypes.SLOWNESS, 1, 1))
				|| potionData.contains(PotionEffect.of(PotionEffectTypes.MINING_FATIGUE, 1, 1)))
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

	public static SpongeNegativityPlayer getNegativityPlayer(Player player) {
		SpongeNegativityPlayer nPlayer = PLAYERS_CACHE.get(player.getUniqueId());
		if (nPlayer == null) {
			nPlayer = new SpongeNegativityPlayer(player);
			PLAYERS_CACHE.put(player.getUniqueId(), nPlayer);
		}

		return nPlayer;
	}

	public static void removeFromCache(Player player) {
		removeFromCache(player.getUniqueId(), false);
	}

	public static void removeFromCache(UUID playerId, boolean isBan) {
		SpongeNegativityPlayer nPlayer = PLAYERS_CACHE.remove(playerId);
		if (nPlayer != null) {
			nPlayer.destroy(isBan);
			Adapter.getAdapter().getNegativityAccount(playerId).loadBanRequest(true);
		}
	}

	@Override
	public boolean isOp() {
		return false;
	}
}
