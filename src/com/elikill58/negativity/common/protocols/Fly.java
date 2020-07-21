package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.FLY;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;

public class Fly extends Cheat implements Listeners {

	public Fly() {
		super(FLY, true, Materials.FIREWORK, CheatCategory.MOVEMENT, true, "flyhack");
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || e.isCancelled())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.hasElytra() || p.getItemInHand().getType().getId().contains("TRIDENT"))
			return;

		if (p.hasPotionEffect(PotionEffectType.SPEED)) {
			int speed = 0;
			for (PotionEffect pe : p.getActivePotionEffect())
				if (pe.getType().equals(PotionEffectType.SPEED))
					speed += pe.getAmplifier() + 1;
			if (speed > 5)
				return;
		}
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if (p.getAllowFlight() || p.getEntityId() == 100 || p.isSwimming())
			return;
		boolean mayCancel = false;
		double y = e.getFrom().getY() - e.getTo().getY();
		Location loc = p.getLocation().clone(),
				locUnder = p.getLocation().clone().sub(0, 1, 0),
				locUnderUnder = p.getLocation().clone().sub(0, 2, 0);
		Material type = loc.getBlock().getType(), typeUpper = loc.getBlock().getRelative(BlockFace.UP).getType();
		boolean isInWater = loc.getBlock().getType().getId().contains("WATER"), isOnWater = locUnder.getBlock().getType().getId().contains("WATER");
		if(String.valueOf(y).contains("E") && !String.valueOf(y).equalsIgnoreCase("2.9430145066276694E-4") && !p.isInsideVehicle()
				&& !np.isInFight && !LocationUtils.hasBoatAroundHim(p.getLocation()) && !(isInWater || isOnWater)
				&& !LocationUtils.hasMaterialsAround(loc, "SCAFFOLD")){
			mayCancel = Negativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, 97, "Suspicious Y: " + y);
		}
		double i = e.getTo().toVector().distance(e.getFrom().toVector());
		if (!(p.isSprinting() && (e.getTo().getY() - e.getFrom().getY()) > 0)
				&& locUnder.getBlock().getType().equals(Materials.AIR)
				&& locUnderUnder.getBlock().getType().equals(Materials.AIR)
				&& (p.getFallDistance() == 0.0F || Utils.isInBoat(p))
				&& typeUpper.equals(Materials.AIR) && i > 0.8
				&& !p.isOnGround()) {
			mayCancel = Negativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, parseInPorcent((int) i * 50),
					"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this),
					Utils.isInBoat(p) ? hoverMsg("boat") : null);
		}

		if (!np.isUsingSlimeBlock && !LocationUtils.hasOtherThanExtended(p.getLocation(), "AIR")
				&& !LocationUtils.hasOtherThanExtended(locUnder, "AIR") && !np.booleans.get(FLY, "boat-falling", false)
				&& !LocationUtils.hasOtherThanExtended(locUnderUnder, "AIR")
				&& (e.getFrom().getY() <= e.getTo().getY() || Utils.isInBoat(p))) {
			double nbTimeAirBelow = np.doubles.get(FLY, "air-below", 0.0);
			np.doubles.set(FLY, "air-below", nbTimeAirBelow + 1);
			if(nbTimeAirBelow > 6) { // we don't care when player jump
				double d = e.getTo().getY() - e.getFrom().getY();
				int nb = LocationUtils.getNbAirBlockDown(p), porcent = parseInPorcent(nb * 15 + d);
				if (LocationUtils.hasOtherThan(p.getLocation().add(0, -3, 0), Materials.AIR))
					porcent = parseInPorcent(porcent - 15);
				mayCancel = Negativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
						this, porcent, "Player not in ground (" + nb + " air blocks down), distance Y: " + d
								+ ". Warn for fly: " + np.getWarn(this),
								hoverMsg(Utils.isInBoat(p) ? "boat_air_below" : "air_below", "%nb%", nb));
			}
		} else
			np.doubles.remove(FLY, "air-below");
		
		Location to = e.getTo().clone();
		to.setY(e.getFrom().getY());
		double distanceWithoutY = to.distance(e.getFrom());
		if (distanceWithoutY == i && !p.isOnGround() && i != 0
				&& typeUpper.equals(Materials.AIR) && !p.isInsideVehicle()
				&& !type.getId().contains("WATER") && distanceWithoutY > 0.3) {
			if (np.booleans.get(FLY, "not-moving-y", false))
				mayCancel = Negativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, 98, "Player not in ground but not moving Y. DistanceWithoutY: " + distanceWithoutY);
			np.booleans.set(FLY, "not-moving-y", true);
		} else
			np.booleans.set(FLY, "not-moving-y", false);
		if (isSetBack() && mayCancel) {
			LocationUtils.teleportPlayerOnGround(p);
		}
	}

	@EventListener
	public void boatManager(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		boolean nextValue = np.booleans.get(FLY, "boat-falling", false);
		if(p.isInsideVehicle() && p.getVehicle().getType().equals(EntityType.BOAT)) {
			Location from = e.getFrom().clone(), to = e.getTo().clone();
			double moveY = (to.getY() - from.getY());
			
			boolean wasWaterBelow = from.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
			boolean willWaterBelow = to.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
			if(wasWaterBelow && !willWaterBelow)
				nextValue = true;
			
			if(nextValue && !willWaterBelow && moveY >= 0)
				nextValue = false;
		} else {
			if(!nextValue)
				return; // already set to false, don't need to save it while put it in map
			nextValue = false;
		}
		
		np.booleans.set(FLY, "boat-falling", nextValue);
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
