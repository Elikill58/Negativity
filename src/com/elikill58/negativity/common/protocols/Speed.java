package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.api.utils.LocationUtils.hasMaterialsAround;
import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThan;

import java.text.NumberFormat;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerDamageByEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.support.EssentialsSupport;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Speed extends Cheat implements Listeners {

	private NumberFormat numberFormat = NumberFormat.getInstance();
	
	public Speed() {
		super(CheatKeys.SPEED, false, Materials.BEACON, CheatCategory.MOVEMENT, true, "speed", "speedhack");
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
		if(p.hasPotionEffect(PotionEffectType.SPEED) || p.hasElytra())
			return;
		
		int ping = p.getPing();
		np.MOVE_TIME++;
		if (np.MOVE_TIME > 60) {
			boolean b = Negativity.alertMod(np.MOVE_TIME > 100 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, UniversalUtils.parseInPorcent(np.MOVE_TIME * 2), "Move " + np.MOVE_TIME + " times. Ping: "
							+ ping + " Warn for Speed: " + np.getWarn(this));
			if (b && isSetBack())
				e.setCancelled(true);
		}
		Location from = e.getFrom().clone(), to = e.getTo().clone();
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Materials.SPONGE)
				|| p.getEntityId() == 100 || p.getVehicle() != null || p.getAllowFlight()
				|| p.getFlySpeed() > 3.0F || p.getWalkSpeed() > 2.0F
				|| p.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE) || p.isInsideVehicle()
				|| hasEnderDragonAround(p) || p.getItemInHand().getType().getId().contains("TRIDENT"))
			return;
		if (np.BYPASS_SPEED != 0) {
			np.BYPASS_SPEED--;
			return;
		}
		Location loc = p.getLocation().clone();
		if (hasMaterialsAround(loc.getBlock().getRelative(BlockFace.UP).getLocation(), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET")
				|| hasMaterialsAround(loc.add(0, 1, 0).getBlock().getRelative(BlockFace.UP).getLocation(), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET")
				|| hasMaterialsAround(loc.sub(0, 1, 0), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET"))
			return;
		double y = to.toVector().clone().setY(0).distance(from.toVector().clone().setY(0));
		boolean mayCancel = false;
		if (p.isOnGround()) {
			double walkSpeed = SpigotNegativity.essentialsSupport ? (p.getWalkSpeed() - EssentialsSupport.getEssentialsRealMoveSpeed(p)) : p.getWalkSpeed();
			boolean walkTest = y > walkSpeed * 3.1 && y > 0.65D, walkWithEssTest = (y - walkSpeed > (walkSpeed * 2.5));
			if((SpigotNegativity.essentialsSupport ? (walkWithEssTest || (p.getWalkSpeed() < 0.35 && y >= 0.75D)) : y >= 0.75D) || walkTest){
				int porcent = UniversalUtils.parseInPorcent(y * 50 + UniversalUtils.getPorcentFromBoolean(walkTest, 20)
						+ UniversalUtils.getPorcentFromBoolean(walkWithEssTest == walkTest, 20)
						+ UniversalUtils.getPorcentFromBoolean(walkWithEssTest, 10));
				ReportType type = np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING;
				mayCancel = Negativity.alertMod(type, p, this, porcent,
						"Player in ground. WalkSpeed: " + walkSpeed + ", Distance between from/to location: " + y + ", walkTest: " + walkTest +
						", walkWithEssentialsTest: " + walkWithEssTest, hoverMsg("distance_ground", "%distance%", numberFormat.format(y)));
			}
			double calculatedSpeedWithoutY = getSpeed(from, to);
			if(calculatedSpeedWithoutY > (p.getWalkSpeed() + 0.01) && p.getVelocity().getY() > 0 && hasOtherThan(from.clone().add(0, 1, 0), "AIR")) { // "+0.01" if to prevent lag"
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 90, "Calculated speed: " + calculatedSpeedWithoutY + ", Walk Speed: " + p.getWalkSpeed() + ", Velocity Y: " + p.getVelocity().getY());
			}
		} else if (!p.isOnGround()) {
			for (Entity entity : p.getNearbyEntities(5, 5, 5))
				if (entity.getType().equals(EntityType.CREEPER))
					return;
			if (!mayCancel) {
				if (y >= 0.85D) {
					mayCancel = Negativity.alertMod(
							np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(y * 100 * 2),
							"Player NOT in ground. WalkSpeed: " + p.getWalkSpeed()
									+ " Distance between from/to location: " + y,
									hoverMsg("distance_jumping", "%distance%", numberFormat.format(y)));
				} else {
					if(p.hasPotionEffect(PotionEffectType.JUMP))
						return;
					Material under = e.getTo().clone().sub(0, 1, 0).getBlock().getType();
					if (!under.getId().contains("STEP") && !np.isUsingSlimeBlock && !(under.getId().contains("WATER") || p.isSwimming())) {
						double distance = from.distance(to);
						to.setY(from.getY());
						double yy = to.distance(from);
						if (distance > 0.45 && (distance > (yy * 2)) && p.getFallDistance() < 1) {
							np.SPEED_NB++;
							if (np.SPEED_NB > 4)
								mayCancel = Negativity.alertMod(ReportType.WARNING, p,
										Cheat.forKey(CheatKeys.SPEED), UniversalUtils.parseInPorcent(86 + np.SPEED_NB), "HighSpeed - Block under: "
												+ under.getId() + ", Speed: " + distance + ", nb: " + np.SPEED_NB + ", fallDistance: " + p.getFallDistance());
						} else
							np.SPEED_NB = 0;
					}
				}
			}
		}
		if (isSetBack() && mayCancel)
			e.setCancelled(true);
	}

	private boolean hasEnderDragonAround(Player p) {
		for (Entity et : p.getWorld().getEntities())
			if (et.getType().equals(EntityType.ENDER_DRAGON) && et.getLocation().distance(p.getLocation()) < 15)
				return true;
		return false;
	}

	@EventListener
	public void onEntityDamage(PlayerDamageByEntityEvent e) {
		NegativityPlayer.getNegativityPlayer(e.getEntity()).BYPASS_SPEED = 3;
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
}
