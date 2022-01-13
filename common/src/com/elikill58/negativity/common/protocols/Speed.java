package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.api.utils.LocationUtils.hasMaterialsAround;
import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.universal.CheatKeys.SPEED;

import java.text.NumberFormat;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Speed extends Cheat implements Listeners {

	private NumberFormat numberFormat = NumberFormat.getInstance();
	
	public Speed() {
		super(SPEED, CheatCategory.MOVEMENT, Materials.BEACON, true, false, "speed", "speedhack");
		numberFormat.setMaximumFractionDigits(4);
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if(p.hasElytra() || LocationUtils.isUsingElevator(p))
			return;
		
		Location from = e.getFrom(), to = e.getTo();
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Materials.SPONGE)
				|| p.getVehicle() != null || p.getAllowFlight()
				|| p.getFlySpeed() > 3.0F || p.getWalkSpeed() > 2.0F
				|| p.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE) || p.isInsideVehicle()
				|| hasEnderDragonAround(p) || p.getItemInHand().getType().getId().contains("TRIDENT"))
			return;
		for (Entity entity : p.getNearbyEntities(5, 5, 5))
			if (entity.getType().equals(EntityType.CREEPER))
				return;
		if (np.BYPASS_SPEED != 0) {
			np.BYPASS_SPEED--;
			return;
		}
		Location loc = p.getLocation().clone();
		Block under = loc.clone().sub(0, 1, 0).getBlock();
		Location locDown = under.getLocation(), locUp = loc.clone().add(0, 1, 0);
		boolean onGround = p.isOnGround();
		double dif = to.getY() - from.getY();
		boolean hasIceBelow = under.getType().getId().contains("ICE") || locUp.getBlock().getType().getId().contains("ICE");
		if(hasIceBelow) {
			np.booleans.set(CheatKeys.ALL, "speed-has-ice", true);
			Adapter.getAdapter().debug("Has ice below " + p.getName());
		} else
			hasIceBelow = np.booleans.get(CheatKeys.ALL, "speed-has-ice", false);
		
		if(onGround && dif < 0) {
			int firstIce = np.ints.get(CheatKeys.ALL, "speed-has-ice-first", 5);
			if(firstIce <= 0) {
				Adapter.getAdapter().debug("Removing ice bypass for " + p.getName());
				np.booleans.remove(CheatKeys.ALL, "speed-has-ice");
				np.ints.remove(CheatKeys.ALL, "speed-has-ice-first");
			} else {
				np.ints.set(CheatKeys.ALL, "speed-has-ice-first", firstIce - 1);
			}
		}
		
		if (hasIceBelow || hasMaterialsAround(locUp, "TRAPDOOR", "SLAB", "STAIRS", "CARPET")
				|| hasMaterialsAround(loc.clone().add(0, 2, 0), "TRAPDOOR", "SLAB", "STAIRS", "CARPET")
				|| hasMaterialsAround(locDown, "TRAPDOOR", "SLAB", "STAIRS", "CARPET"))
			return;
		double amplifierSpeed = p.getPotionEffect(PotionEffectType.SPEED).orElseGet(() -> new PotionEffect(PotionEffectType.SPEED, 0, 0)).getAmplifier();
		double y = to.toVector().clone().setY(0).distance(from.toVector().clone().setY(0));
		double distance = from.distance(to);
		boolean mayCancel = false;
		if(onGround && checkActive("distance-ground") && amplifierSpeed < 5) {
			Vector direction = p.getVelocity().clone();
			double disWithDir = from.clone().add(direction).distanceSquared(to);
			double disWithDirY = from.clone().add(direction).toVector().setY(0).distanceSquared(to.toVector().setY(0));
			double walkSpeed = (p.getWalkSpeed() - getEssentialsRealMoveSpeed(p)); // TODO rewrite without converting to essentials values
			boolean walkTest = y > walkSpeed * 3.1 && y > 0.65D, walkWithEssTest = (y - walkSpeed > (walkSpeed * 2.5));
			if((((walkWithEssTest || (p.getWalkSpeed() < 0.35 && y >= 0.75D))) || walkTest) && (y < (disWithDir + disWithDirY))){
				int porcent = UniversalUtils.parseInPorcent(y * 50 + UniversalUtils.getPorcentFromBoolean(walkTest, 20)
						+ UniversalUtils.getPorcentFromBoolean(walkWithEssTest == walkTest, 20)
						+ UniversalUtils.getPorcentFromBoolean(walkWithEssTest, 10));
				mayCancel = Negativity.alertMod(np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p, this, porcent, "distance-ground",
						"Player in ground. WalkSpeed: " + walkSpeed + ", Distance between from/to location: " + y + ", walkTest: " + walkTest +
						", walkWithEss: " + walkWithEssTest + ", y: " + y + ", disDir: " + disWithDir + ", disDirY: " + disWithDirY, hoverMsg("distance_ground", "%distance%", numberFormat.format(y)));
			}
		}
		if(onGround && checkActive("calculated")) {
			double calculatedSpeedWith = getSpeed(from, to);
			double calculatedSpeedWithoutY = getSpeed(from, to, p.getVelocity()), velocity = p.getVelocity().getY();
			if(calculatedSpeedWithoutY > (p.getWalkSpeed() + 0.01) && velocity < calculatedSpeedWithoutY && velocity > 0.1
					&& !hasOtherThan(from.clone().add(0, 1, 0), "AIR")) { // "+0.01" if to prevent lag"
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 90, "calculated",
						"Calculated speed: " + calculatedSpeedWithoutY + ", Walk Speed: " + p.getWalkSpeed() + ", Velocity Y: " + velocity + ", speed: " + calculatedSpeedWith);
			}
		}
		if(checkActive("distance-jumping") && !onGround && (y - (amplifierSpeed / 10)) >= 0.85D && !hasIceBelow && !np.isInFight && p.getVelocity().length() < 1) {
			mayCancel = Negativity.alertMod(np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(y * 100 * 2), "distance-jumping", "NOT in ground. WS: " + p.getWalkSpeed()
						+ ", fallDis: " + p.getFallDistance() + " Dis from/to: " + y + ", ySpeed: " + (y - (amplifierSpeed / 10))
						+ ", vel: " + p.getVelocity().toShowableString() + ", vel len: " + p.getVelocity().length(),
						hoverMsg("distance_jumping", "%distance%", numberFormat.format(y)));
		}
		if(checkActive("high-speed") && !onGround && y < 0.85D && !np.booleans.get(CheatKeys.ALL, "jump-boost-use", false)) {
			if (!under.getType().getId().contains("STEP") && !np.isUsingSlimeBlock && !(under.getType().getId().contains("WATER") || under.isWaterLogged() || under.isLiquid() || p.isSwimming())) {
				to.setY(from.getY());
				double yy = to.distance(from);
				if (distance > 0.45 && (distance > (yy * 2)) && p.getFallDistance() < 1) {
					np.SPEED_NB++;
					if (np.SPEED_NB > 4)
						mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(86 + np.SPEED_NB), "high-speed",
								"HighSpeed - Block under: " + under.getType().getId() + ", Speed: " + distance + ", nb: " + np.SPEED_NB + ", fallDistance: " + p.getFallDistance());
				} else
					np.SPEED_NB = 0;
			}
		}
		if(checkActive("same-diff")) {
			double d = np.doubles.get(SPEED, "dif-y", 0.0);
			if(dif != 0.0 && d != 0.0) {
				if (dif == d || dif == -d) {
					mayCancel = Negativity.alertMod(np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p,
							this, 95, "same-diff", "Differences : " + dif + " / " + d);
				}
				np.doubles.set(SPEED, "dif-y", dif);
			}
		}
		if(checkActive("walk-speed") && Adapter.getAdapter().getPlatformID().equals(Platform.SPIGOT)) {
			double distanceWithSpeed = distance - (amplifierSpeed / 10);
			if(dif == 0 && distanceWithSpeed >= (p.getWalkSpeed() * (p.isSprinting() ? 2.5 : 2))) {
				mayCancel = Negativity.alertMod(np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p,
						this, 95, "walk-speed", "Differences : " + dif + ", distance: " + String.format("%.4f", distance) + ", withSpeed: "
						+ String.format("%.4f", distanceWithSpeed) + ", speedAmplifier: " + amplifierSpeed
						+ ", walkSpeed: " + p.getWalkSpeed() + ", onGround: " + onGround);
			}
		}
		if (mayCancel && isSetBack())
			e.setCancelled(true);
	}

	private static float getEssentialsRealMoveSpeed(Player p) {
		final float defaultSpeed = p.isFlying() ? 0.1f : 0.2f;
		float maxSpeed = 1f;
		if (p.getWalkSpeed() < 1f)
			return defaultSpeed * p.getWalkSpeed();
		else
			return ((p.getWalkSpeed() - 1) / 9) * (maxSpeed - defaultSpeed) + defaultSpeed;
	}

	private boolean hasEnderDragonAround(Player p) {
		for (Entity et : p.getWorld().getEntities())
			if (et.getType().equals(EntityType.ENDER_DRAGON) && et.getLocation().distance(p.getLocation()) < 15)
				return true;
		return false;
	}
	
	@Check(name = "move-amount", description = "Amount of move", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_ELYTRA })
	public void onMoveAmount(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		np.MOVE_TIME++;
		if (np.MOVE_TIME > 60) {
			boolean b = Negativity.alertMod(np.MOVE_TIME > 100 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, UniversalUtils.parseInPorcent(np.MOVE_TIME * 2), "move-amount", "Move " + np.MOVE_TIME + " times.", new CheatHover.Literal("Move too many times: " + np.MOVE_TIME + " (should be 20)"));
			if (b && isSetBack())
				e.setCancelled(true);
		}
	}

	@EventListener
	public void onEntityDamage(PlayerDamagedByEntityEvent e) {
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).BYPASS_SPEED = 3;
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
	
	public static double getSpeed(Location from, Location to) {
		double x = to.getX() - from.getX();
		double z = to.getZ() - from.getZ();

		return x * x + z * z;
	}
	
	public static double getSpeed(Location from, Location to, Vector vec) {
		double x = to.getX() - from.getX() - vec.getX();
		double z = to.getZ() - from.getZ() - vec.getZ();

		return x * x + z * z;
	}
}
