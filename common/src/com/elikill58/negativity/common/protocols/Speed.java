package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.api.utils.LocationUtils.hasMaterialsAround;
import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.universal.detections.keys.CheatKeys.SPEED;

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
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
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
		if (np.bypassSpeed != 0) {
			np.bypassSpeed--;
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
		double y = to.toVector().clone().setY(0).distance(from.toVector().clone().setY(0)), velocity = p.getVelocity().getY(), velLen = p.getVelocity().length();
		double distance = from.distance(to);
		boolean mayCancel = false;
		if(onGround && checkActive("distance-ground") && amplifierSpeed < 5) {
			Vector direction = p.getVelocity().clone();
			double disWithDir = from.clone().add(direction).distanceSquared(to);
			double disWithDirY = from.clone().add(direction).toVector().setY(0).distanceSquared(to.toVector().setY(0));
			double walkSpeed = (p.getWalkSpeed() - getEssentialsRealMoveSpeed(p)); // TODO rewrite without converting to essentials values
			boolean walkTest = y > walkSpeed * 3.1 && y > 0.65D, walkWithEssTest = (y - walkSpeed > (walkSpeed * 2.5));
			if((((walkWithEssTest || (p.getWalkSpeed() < 0.35 && y >= 0.75D))) || walkTest) && (y < (disWithDir + disWithDirY))){
				int porcent = UniversalUtils.parseInPorcent(y * 50 + (walkTest ? 10 : 0) + (walkWithEssTest == walkTest ? 10 : 0) + (walkWithEssTest ? 10 : 0));
				mayCancel = Negativity.alertMod(np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p, this, porcent, "distance-ground",
						"Ground. WS: " + walkSpeed + ", Dis from/to: " + y + ", walkTest: " + walkTest +
						", walkWithEss: " + walkWithEssTest + ", y: " + y + ", disDir: " + disWithDir + ", disDirY: " + disWithDirY, hoverMsg("distance_ground", "%distance%", numberFormat.format(y)));
			}
		}
		if(onGround && checkActive("calculated")) {
			double calculatedSpeedWith = getSpeed(from, to);
			double calculatedSpeedWithoutY = getSpeed(from, to, p.getVelocity());
			if(calculatedSpeedWithoutY > (p.getWalkSpeed() + 0.01) && velocity < calculatedSpeedWithoutY && velocity > 0.1
					&& !hasOtherThan(from.clone().add(0, 1, 0), "AIR") && velocity % 0.16477328182606651 != 0) { // "+0.01" if to prevent lag"
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 90, "calculated",
						"Calculated speed: " + calculatedSpeedWithoutY + ", WS: " + p.getWalkSpeed() + ", Velocity Y: " + velocity + ", speed: " + calculatedSpeedWith);
			}
		}
		if(checkActive("distance-jumping") && !onGround && (y - (amplifierSpeed / 10) - (velLen > 0.5 ? velLen : 0)) >= 0.85D
				&& !hasIceBelow && !np.isInFight && p.getTheoricVelocity().length() < 0.85D) { // theoric length to when the new high velocity is actually taken
			mayCancel = Negativity.alertMod(np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(y * 190), "distance-jumping", "WS: " + p.getWalkSpeed()
						+ ", fd: " + p.getFallDistance() + ", from/to: " + String.format("%.10f", y) + ", ySpeed: " + String.format("%.10f", y - (amplifierSpeed / 10) - (velLen > 0.5 ? velLen : 0))
						+ ", vel: " + p.getVelocity() + ", thvel: " + p.getTheoricVelocity(), hoverMsg("distance_jumping", "%distance%", numberFormat.format(y)));
		}
		if(checkActive("high-speed") && !onGround && y < 0.85D && !np.booleans.get(CheatKeys.ALL, "jump-boost-use", false)) {
			if (!under.getType().getId().contains("STEP") && !np.isUsingSlimeBlock && !(under.getType().getId().contains("WATER") || under.isWaterLogged() || under.isLiquid() || p.isSwimming())) {
				Location toHigh = to.clone();
				toHigh.setY(from.getY());
				double yy = toHigh.distance(from);
				if (distance > 0.45 && (distance > (yy * 2)) && p.getFallDistance() < 1) {
					int nb = np.ints.get(getKey(), "high-speed-amount", 0) + 1;
					if (nb > 4)
						mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(86 + nb), "high-speed",
								"HighSpeed - Under: " + under.getType().getId() + ", Speed: " + distance + ", nb: " + nb + ", FD: " + p.getFallDistance() + ", y: " + yy + ", vel " + p.getVelocity());
					np.ints.set(getKey(), "high-speed-amount", nb);
				} else
					np.ints.remove(getKey(), "high-speed-amount");
			}
		}
		if(checkActive("same-diff")) {
			double d = np.doubles.get(SPEED, "dif-y", 0.0);
			if(dif != 0.0 && d != 0.0) {
				if (dif == Math.abs(d)) {
					mayCancel = Negativity.alertMod(np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p,
							this, 95, "same-diff", "Differences : " + dif + " / " + d);
				}
				np.doubles.set(SPEED, "dif-y", dif);
			}
		}
		if(checkActive("walk-speed") && Adapter.getAdapter().getPlatformID().equals(Platform.SPIGOT)) {
			double distanceWithSpeed = distance - (amplifierSpeed / 10);
			if(dif == 0 && distanceWithSpeed >= (p.getWalkSpeed() * (p.isSprinting() ? 2.5 : 2) * 1.01)) {
				mayCancel = Negativity.alertMod(np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p,
						this, UniversalUtils.parseInPorcent(distanceWithSpeed * 100), "walk-speed", "Differences : " + dif + ", distance: " + String.format("%.4f", distance) + ", withSpeed: "
						+ String.format("%.4f", distanceWithSpeed) + ", amplifier: " + amplifierSpeed
						+ ", ws: " + p.getWalkSpeed() + ", ground: " + onGround + ", distanceXZ: " + from.distanceXZ(to));
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

	@EventListener
	public void onEntityDamage(PlayerDamagedByEntityEvent e) {
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).bypassSpeed = 3;
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
