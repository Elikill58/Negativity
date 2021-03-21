package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.WeakHashMap;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.events.packets.PacketEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageByEntityEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class ForceField extends Cheat implements Listeners {

	public static final DataType<Double> HIT_DISTANCE = new DataType<Double>("hit_distance", "Hit Distance",
			() -> new DoubleDataCounter());
	public static final DataType<Integer> FAKE_PLAYERS = new DataType<Integer>("fake_players", "Fake Players",
			() -> new IntegerDataCounter());

	private NumberFormat nf = NumberFormat.getInstance();

	public ForceField() {
		super(CheatKeys.FORCEFIELD, CheatCategory.COMBAT, Materials.DIAMOND_SWORD, true, true, "ff", "killaura");
		nf.setMaximumIntegerDigits(2);
	}

	@EventListener
	public void onPacketClear(PlayerPacketsClearEvent e) {

		NegativityPlayer np = e.getNegativityPlayer();
		if (np.hasDetectionActive(this) && checkActive("packet")) {
			int arm = e.getPackets().getOrDefault(PacketType.Client.ARM_ANIMATION, 0);
			int useEntity = e.getPackets().getOrDefault(PacketType.Client.USE_ENTITY, 0);
			if (arm > 16 && useEntity > 20) {
				ReportType type = ReportType.WARNING;
				if (np.getWarn(this) > 5)
					type = ReportType.VIOLATION;
				Negativity.alertMod(type, np.getPlayer(), this,
						UniversalUtils.parseInPorcent(arm + useEntity + np.getWarn(this)), "packet",
						"ArmAnimation (Attack in one second): " + arm + ", UseEntity (interaction with other entity): "
								+ useEntity);
			}
		}
	}

	@EventListener
	public void onEntityDamageByEntity(PlayerDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || e.isCancelled())
			return;
		Player p = (Player) e.getDamager();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		np.setLastHitted(e.getEntity());
		if (!np.hasDetectionActive(this) || e.getEntity() == null)
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		boolean mayCancel = false;
		Entity cible = e.getEntity();
		if (checkActive("line-sight") && !p.hasLineOfSight(cible) && p != cible) {
			mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, parseInPorcent(90 + np.getWarn(this)),
					"line-sight", "Hit " + cible.getType().name() + " but cannot see it",
					hoverMsg("line_sight", "%name%", cible.getType().name().toLowerCase(Locale.ROOT)));
		}
		if (Utils.hasThorns(p)) {
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
			return;
		}
		ItemStack inHand = p.getItemInHand();
		if (inHand == null || !inHand.getType().equals(Materials.BOW)) {
			Location tempLoc = e.getEntity().getLocation().clone();
			tempLoc.setY(p.getLocation().getY());
			double dis = tempLoc.distance(p.getLocation());
			recordData(p.getUniqueId(), HIT_DISTANCE, dis);
			if (checkActive("reach") && dis > getConfig().getDouble("check.reach.value", 3.9)
					&& !e.getDamager().getType().equals(EntityType.ENDER_DRAGON)
					&& !p.getLocation().getBlock().getType().getId().contains("WATER")) {
				String entityName = Version.getVersion().equals(Version.V1_7)
						? e.getEntity().getType().name().toLowerCase(Locale.ROOT)
						: e.getEntity().getName();
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10), "reach",
						"Big distance with: " + e.getEntity().getType().name().toLowerCase(Locale.ROOT)
								+ ". Exact distance: " + dis + ", without thorns",
						hoverMsg("distance", "%name%", entityName, "%distance%", nf.format(dis)));
			}
		}
		if (isSetBack() && mayCancel)
			e.setCancelled(true);

	}

	/*
	 * @EventListener public void onPacket(PacketReceiveEvent e) { AbstractPacket
	 * packet = e.getPacket();
	 * if(!packet.getPacketType().equals(PacketType.Client.USE_ENTITY)) return;
	 * Player p = e.getPlayer(); if(p.getGameMode().equals(GameMode.CREATIVE))
	 * return; ItemStack inHand = p.getItemInHand(); if(inHand != null &&
	 * inHand.getType().equals(Materials.BOW)) return; try { Object nmsPacket =
	 * packet.getPacket(); Location loc = p.getLocation(); Object nmsEntity =
	 * nmsPacket.getClass().getDeclaredMethod("a",
	 * PacketUtils.getNmsClass("World")).invoke(nmsPacket,
	 * PacketUtils.getWorldServer(loc)); if(nmsEntity == null) return; Location
	 * entityLoc = getLocationNMSEntity(nmsEntity, p.getWorld()); double dis =
	 * loc.distance(entityLoc); recordData(p.getUniqueId(), HIT_DISTANCE, dis); if
	 * (dis < Adapter.getAdapter().getConfig().getDouble("cheats.forcefield.reach"))
	 * return; Class<?> entityClass = PacketUtils.getNmsClass("Entity"); Entity
	 * cible = (Entity)
	 * entityClass.getDeclaredMethod("getBukkitEntity").invoke(nmsEntity);
	 * EntityType type = cible.getType(); if(type.equals(EntityType.ENDER_DRAGON) ||
	 * type.equals(EntityType.ENDERMAN)) return; boolean mayCancel =
	 * Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 *
	 * 10), "[Packet] Big distance with: " + type.name().toLowerCase() +
	 * ". Exact distance: " + dis + ", without thorns" + ". Ping: " + p.getPing(),
	 * hoverMsg("distance", "%name%", PacketUtils.getNmsEntityName(nmsEntity),
	 * "%distance%", nf.format(dis))); if(mayCancel) packet.setCancelled(true); }
	 * catch (Exception exc) { exc.printStackTrace(); } }
	 * 
	 * public Location getLocationNMSEntity(Object src, World baseWorld) { return
	 * Adapter.getAdapter().createLocation(baseWorld, getFieldOrMethod(src, "locX"),
	 * getFieldOrMethod(src, "locY"), getFieldOrMethod(src, "locZ")); }
	 * 
	 * private double getFieldOrMethod(Object src, String name) { Class<?>
	 * entityClass = PacketUtils.getNmsClass("Entity"); try { Method m =
	 * entityClass.getDeclaredMethod(name); m.setAccessible(true); return (double)
	 * m.invoke(src); } catch (NoSuchMethodException e) { try { Field f =
	 * entityClass.getDeclaredField(name); f.setAccessible(true); return
	 * f.getDouble(src); } catch (Exception exc) { exc.printStackTrace(); } } catch
	 * (Exception e) { e.printStackTrace(); } return 0.0; }
	 */

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		double av = data.getData(HIT_DISTANCE).getAverage();
		int nb = data.getData(FAKE_PLAYERS).getSize();
		String color = (av > 3 ? (av > 4 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("Hit distance : " + color + String.format("%.3f", av)
				+ (nb > 0 ? " &7and &c" + nb + " &7fake players touched." : ""));
	}

	public static void manageForcefieldForFakeplayer(Player p, NegativityPlayer np) {
		if (np.fakePlayerTouched == 0)
			return;
		Cheat forcefield = Cheat.forKey(CheatKeys.FORCEFIELD);
		forcefield.recordData(p.getUniqueId(), FAKE_PLAYERS, 1);
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		Negativity.alertMod(np.fakePlayerTouched > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, forcefield,
				parseInPorcent(np.fakePlayerTouched * 10), "ghost",
				"Hitting fake entities. " + np.fakePlayerTouched + " entites touch in " + timeBehindStart
						+ " millisecondes",
				forcefield.hoverMsg("fake_players", "%nb%", np.fakePlayerTouched, "%time%", timeBehindStart));
	}

	// NoSwing with packets:

	// map where i stock player and was last arm animation
	public WeakHashMap<NegativityPlayer, Boolean> wasLastArmAnimation = new WeakHashMap<>();
	// for avoid false flags

	public WeakHashMap<NegativityPlayer, Integer> preVL = new WeakHashMap<>();

	@EventListener
	public void onPacket(PacketEvent event) {
		// get the cheat
		Cheat forcefield = Cheat.forKey(CheatKeys.FORCEFIELD);

		// get player's data
		NegativityPlayer data = NegativityPlayer.getNegativityPlayer(event.getPlayer());

		if (event.getPacket().getPacketType() == PacketType.Client.USE_ENTITY) {
			if (wasLastArmAnimation.get(data) == false) {

				// CHEATER (can false if player/server is lagging
				Negativity.alertMod(ReportType.VIOLATION, event.getPlayer(), forcefield, 56, "NoSwing",
						"last packet before hit was not an arm animation packet");
				preVL.remove(preVL, data);

			}

		} else if (event.getPacket().getPacketType() == PacketType.Client.ARM_ANIMATION) {
			wasLastArmAnimation.put(data, true);
		} else if (event.getPacket().getPacketType() == PacketType.Client.FLYING) {
			wasLastArmAnimation.put(data, false);
		}

	}

	public static Location getEyeLocation(Player player) {
		Location eye = player.getLocation();
		eye.setY(eye.getY() + player.getEyeHeight());
		return eye;
	}

	@EventListener
	public void onPacketReach(PacketEvent event) {
		NegativityPlayer data = NegativityPlayer.getNegativityPlayer(event.getPlayer());
		Entity victim = data.getLastHitted();
		double diffEye = getEyeLocation(data.getPlayer()).distance(victim.getLocation());
		double maxReach = 3.1;
		maxReach += data.getPlayer().getWalkSpeed() / 4;
		maxReach += ((Player) victim).getWalkSpeed() / 4;
		double lastTps = Adapter.getAdapter().getLastTPS();
		if (lastTps >= 18.5) {
			maxReach -= 0.035;
		} else if (lastTps <= 17) {
			maxReach += 0.065;
		} else if (lastTps <= 15) {
			maxReach += 0.2;
		} else if (lastTps <= 13) {
			maxReach += 0.329;
		} else if (lastTps <= 10) {
			maxReach += 0.489;
		} else if (lastTps <= 5) {
			return;
		}

		maxReach += ((Player) victim).getVelocity().length() * 1.5;
		maxReach += (data.getPlayer()).getVelocity().length() * 1.5;
		if (data.getPlayer().hasPotionEffect(PotionEffectType.SPEED)) {
			maxReach += (data.getPlayer().getPotionEffect(PotionEffectType.SPEED)
					.orElse(new PotionEffect(PotionEffectType.SPEED)).getAmplifier() + 1) / 4;

		}

		if (event.getPacket().getPacketType() == PacketType.Client.USE_ENTITY) {
			try {
				if (event.getPacket().getContent()
						.getSpecificModifier(Class.forName(
								"net.minecraft.server." + Adapter.getAdapter().getServerVersion() + ".EntityUseAction"))
						.read(0).toString().equalsIgnoreCase("ATTACK")) {
					if (diffEye > maxReach) {
						Negativity.alertMod(ReportType.VIOLATION, data.getPlayer(), this, (int) diffEye * 16,
								"ReachDiffEye", "Player's reach (theorical) " + diffEye + " Max Reach: " + maxReach);
					}

				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
