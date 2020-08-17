package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.spigot.utils.LocationUtils.hasMaterialsAround;
import static com.elikill58.negativity.spigot.utils.LocationUtils.hasOtherThan;

import java.text.NumberFormat;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.support.EssentialsSupport;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings("deprecation")
public class SpeedProtocol extends Cheat implements Listener {

	private NumberFormat numberFormat = NumberFormat.getInstance();
	
	public SpeedProtocol() {
		super(CheatKeys.SPEED, false, Material.BEACON, CheatCategory.MOVEMENT, true, "speed", "speedhack");
		numberFormat.setMaximumFractionDigits(4);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if(p.hasPotionEffect(PotionEffectType.SPEED) || np.hasElytra())
			return;
		
		np.MOVE_TIME++;
		if (np.MOVE_TIME > 60) {
			boolean b = SpigotNegativity.alertMod(np.MOVE_TIME > 100 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, UniversalUtils.parseInPorcent(np.MOVE_TIME * 2), "Move " + np.MOVE_TIME + " times. Ping: "
							+ Utils.getPing(p) + " Warn for Speed: " + np.getWarn(this));
			if (b && isSetBack())
				e.setCancelled(true);
		}
		Location from = e.getFrom().clone(), to = e.getTo().clone();
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SPONGE)
				|| p.getEntityId() == 100 || p.getVehicle() != null || p.getAllowFlight()
				|| p.getFlySpeed() > 3.0F || p.getWalkSpeed() > 2.0F
				|| np.hasPotionEffect("DOLPHINS_GRACE") || p.isInsideVehicle()
				|| hasEnderDragonAround(p) || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;
		if (np.BYPASS_SPEED != 0) {
			np.BYPASS_SPEED--;
			return;
		}
		Location loc = p.getLocation().clone();
		if (hasMaterialsAround(loc.getBlock().getRelative(BlockFace.UP).getLocation(), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET")
				|| hasMaterialsAround(loc.add(0, 1, 0).getBlock().getRelative(BlockFace.UP).getLocation(), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET")
				|| hasMaterialsAround(loc.subtract(0, 1, 0), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET"))
			return;
		double y = to.toVector().clone().setY(0).distance(from.toVector().clone().setY(0));
		boolean mayCancel = false;
		if (np.isOnGround()) {
			double walkSpeed = SpigotNegativity.essentialsSupport ? (p.getWalkSpeed() - EssentialsSupport.getEssentialsRealMoveSpeed(p)) : p.getWalkSpeed();
			boolean walkTest = y > walkSpeed * 3.1 && y > 0.65D, walkWithEssTest = (y - walkSpeed > (walkSpeed * 2.5));
			if((SpigotNegativity.essentialsSupport ? (walkWithEssTest || (p.getWalkSpeed() < 0.35 && y >= 0.75D)) : y >= 0.75D) || walkTest){
				int porcent = UniversalUtils.parseInPorcent(y * 50 + UniversalUtils.getPorcentFromBoolean(walkTest, 20)
						+ UniversalUtils.getPorcentFromBoolean(walkWithEssTest == walkTest, 20)
						+ UniversalUtils.getPorcentFromBoolean(walkWithEssTest, 10));
				ReportType type = np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING;
				mayCancel = SpigotNegativity.alertMod(type, p, this, porcent,
						"Player in ground. WalkSpeed: " + walkSpeed + ", Distance between from/to location: " + y + ", walkTest: " + walkTest +
						", walkWithEssentialsTest: " + walkWithEssTest, hoverMsg("distance_ground", "%distance%", numberFormat.format(y)));
			}
			double calculatedSpeedWithoutY = Utils.getSpeed(from, to);
			if(calculatedSpeedWithoutY > (p.getWalkSpeed() + 0.01) && p.getVelocity().getY() < calculatedSpeedWithoutY && hasOtherThan(from.clone().add(0, 1, 0), "AIR")) { // "+0.01" if to prevent lag"
				mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, 90, "Calculated speed: " + calculatedSpeedWithoutY + ", Walk Speed: " + p.getWalkSpeed() + ", Velocity Y: " + p.getVelocity().getY());
			}
		} else if (!np.isOnGround()) {
			for (Entity entity : p.getNearbyEntities(5, 5, 5))
				if (entity instanceof Creeper || entity.getType().equals(EntityType.CREEPER))
					return;
			if (!mayCancel) {
				if (y >= 0.85D) {
					mayCancel = SpigotNegativity.alertMod(
							np.getWarn(this) > 7 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(y * 100 * 2),
							"Player NOT in ground. WalkSpeed: " + p.getWalkSpeed()
									+ " Distance between from/to location: " + y,
									hoverMsg("distance_jumping", "%distance%", numberFormat.format(y)));
				} else {
					if(p.hasPotionEffect(PotionEffectType.JUMP))
						return;
					Material under = e.getTo().clone().subtract(0, 1, 0).getBlock().getType();
					if (!under.name().contains("STEP") && !np.isUsingSlimeBlock && !(under.name().contains("WATER") || Utils.isSwimming(p))) {
						double distance = from.distance(to);
						to.setY(from.getY());
						double yy = to.distance(from);
						if (distance > 0.45 && (distance > (yy * 2)) && p.getFallDistance() < 1) {
							np.SPEED_NB++;
							if (np.SPEED_NB > 4)
								mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p,
										Cheat.forKey(CheatKeys.SPEED), UniversalUtils.parseInPorcent(86 + np.SPEED_NB), "HighSpeed - Block under: "
												+ under.name() + ", Speed: " + distance + ", nb: " + np.SPEED_NB + ", fallDistance: " + p.getFallDistance());
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

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player)
			SpigotNegativityPlayer.getNegativityPlayer((Player) e.getEntity()).BYPASS_SPEED = 3;
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
