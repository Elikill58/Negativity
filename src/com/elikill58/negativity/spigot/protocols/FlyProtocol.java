package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;

public class FlyProtocol extends Cheat implements Listener {

	public FlyProtocol() {
		super(CheatKeys.FLY, true, ItemUtils.FIREWORK, CheatCategory.MOVEMENT, true, "flyhack");
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || e.isCancelled())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (np.hasElytra() || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;

		if (p.hasPotionEffect(PotionEffectType.SPEED)) {
			int speed = 0;
			for (PotionEffect pe : p.getActivePotionEffects())
				if (pe.getType().equals(PotionEffectType.SPEED))
					speed += pe.getAmplifier() + 1;
			if (speed > 5)
				return;
		}
		if (p.getAllowFlight() || p.getEntityId() == 100 || Utils.isSwimming(p))
			return;
		boolean mayCancel = false;
		double y = e.getFrom().getY() - e.getTo().getY();
		Location loc = p.getLocation().clone(),
				locUnder = p.getLocation().clone().subtract(0, 1, 0),
				locUnderUnder = p.getLocation().clone().subtract(0, 2, 0);
		Material type = loc.getBlock().getType(), typeUpper = loc.getBlock().getRelative(BlockFace.UP).getType();
		boolean isInWater = loc.getBlock().getType().name().contains("WATER"), isOnWater = locUnder.getBlock().getType().name().contains("WATER");
		if(String.valueOf(y).contains("E") && !String.valueOf(y).equalsIgnoreCase("2.9430145066276694E-4") && !p.isInsideVehicle()
				&& !np.isInFight && !LocationUtils.hasBoatAroundHim(p.getLocation()) && !(isInWater || isOnWater)){
			mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, 97, "Suspicious Y: " + y);
		}
		double i = e.getTo().toVector().distance(e.getFrom().toVector());
		if (!(p.isSprinting() && (e.getTo().getY() - e.getFrom().getY()) > 0)
				&& locUnder.getBlock().getType().equals(Material.AIR)
				&& locUnderUnder.getBlock().getType().equals(Material.AIR)
				&& (p.getFallDistance() == 0.0F || Utils.isInBoat(p))
				&& typeUpper.equals(Material.AIR) && i > 0.8
				&& !np.isOnGround()) {
			mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, parseInPorcent((int) i * 50),
					"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this),
					Utils.isInBoat(p) ? hoverMsg("boat") : null);
		}

		if (!np.isUsingSlimeBlock && !LocationUtils.hasOtherThanExtended(p.getLocation(), "AIR")
				&& !LocationUtils.hasOtherThanExtended(locUnder, "AIR") && !np.contentBoolean.getOrDefault("boat-falling", false)
				&& !LocationUtils.hasOtherThanExtended(locUnderUnder, "AIR")
				&& (e.getFrom().getY() <= e.getTo().getY() || Utils.isInBoat(p))) {
			double nbTimeAirBelow = np.contentDouble.getOrDefault("fly-air-below", 0.0);
			np.contentDouble.put("fly-air-below", nbTimeAirBelow + 1);
			if(nbTimeAirBelow > 6) { // we don't care when player jump
				double d = e.getTo().getY() - e.getFrom().getY();
				int nb = LocationUtils.getNbAirBlockDown(p), porcent = parseInPorcent(nb * 15 + d);
				if (LocationUtils.hasOtherThan(p.getLocation().add(0, -3, 0), Material.AIR))
					porcent = parseInPorcent(porcent - 15);
				mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
						this, porcent, "Player not in ground (" + nb + " air blocks down), distance Y: " + d
								+ ". Warn for fly: " + np.getWarn(this),
								hoverMsg(Utils.isInBoat(p) ? "boat_air_below" : "air_below", "%nb%", nb));
			}
		} else
			np.contentDouble.remove("fly-air-below");
		
		Location to = e.getTo().clone();
		to.setY(e.getFrom().getY());
		double distanceWithoutY = to.distance(e.getFrom());
		if (distanceWithoutY == i && !np.isOnGround() && i != 0
				&& typeUpper.equals(Material.AIR) && !p.isInsideVehicle()
				&& !type.name().contains("WATER") && distanceWithoutY > 0.1) {
			if (np.contentBoolean.getOrDefault("fly-not-moving-y", false))
				mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, 98, "Player not in ground but not moving Y. DistanceWithoutY: " + distanceWithoutY);
			np.contentBoolean.put("fly-not-moving-y", true);
		} else
			np.contentBoolean.put("fly-not-moving-y", false);
		if (isSetBack() && mayCancel) {
			Utils.teleportPlayerOnGround(p);
		}
	}

	@EventHandler
	public void boatManager(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		boolean nextValue = np.contentBoolean.getOrDefault("boat-falling", false);
		if(p.isInsideVehicle() && p.getVehicle().getType().equals(EntityType.BOAT)) {
			Location from = e.getFrom().clone(), to = e.getTo().clone();
			double moveY = (to.getY() - from.getY());
			
			boolean wasWaterBelow = from.subtract(0, 1, 0).getBlock().getType().name().contains("WATER");
			boolean willWaterBelow = to.subtract(0, 1, 0).getBlock().getType().name().contains("WATER");
			if(wasWaterBelow && !willWaterBelow)
				nextValue = true;
			
			if(nextValue && !willWaterBelow && moveY >= 0)
				nextValue = false;
		} else {
			if(!nextValue)
				return; // already set to false, don't need to save it while put it in map
			nextValue = false;
		}
		
		np.contentBoolean.put("boat-falling", nextValue);
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
