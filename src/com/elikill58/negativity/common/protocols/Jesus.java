package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.utils.LocationUtils;

import static com.elikill58.negativity.api.item.Materials.STATIONARY_WATER;
import static com.elikill58.negativity.api.utils.LocationUtils.hasMaterialsAround;
import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThanExtended;
import static com.elikill58.negativity.universal.CheatKeys.JESUS;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;

public class Jesus extends Cheat implements Listeners {

	public Jesus() {
		super(JESUS, false, Materials.WATER_BUCKET, CheatCategory.MOVEMENT, true, "waterwalk", "water",
				"water walk");
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (p.hasElytra() || p.isInsideVehicle() || p.isSwimming())
			return;
		Location loc = p.getLocation(), to = e.getTo(), from = e.getFrom();
		if (hasMaterialsAround(loc, "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET", "WATER_LILY", "LILY")
				|| hasMaterialsAround(loc.clone().sub(0, 1, 0), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET", "WATER_LILY", "LILY"))
			return;
		Location under = loc.clone().sub(0, 1, 0);
		Material type = loc.getBlock().getType(), underType = under.getBlock().getType();
		boolean isInWater = type.getId().contains("WATER"), isOnWater = underType.getId().contains("WATER");
		boolean mayCancel = false;
		double dif = e.getFrom().getY() - e.getTo().getY();
		if(checkActive("water-around")) {
			if (!isInWater && isOnWater && !LocationUtils.hasBoatAroundHim(loc) && !p.isFlying()) {
				if (!hasOtherThanExtended(under, STATIONARY_WATER)) {
					double reliability = 0;
					if (dif < 0.0005 && dif > 0.00000005)
						reliability = dif * 10000000 - 1;
					else if (dif < 0.1 && dif > 0.08)
						reliability = dif * 1000;
					else if (dif == 0.5)
						reliability = 75;
					else if (dif < 0.30001 && dif > 0.3000)
						reliability = dif * 100 * 2.5;
					else if (dif < 0.002 && dif > -0.002 && dif != 0.0)
						reliability = Math.abs(dif * 5000);
					else if (dif == 0.0)
						reliability = 90;
					mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(reliability), "water-around",
							"Stationary_water aroud him. Diff: " + dif);
				}
			}
		}
		if (checkActive("dif") && dif == -0.5 && (isInWater || isOnWater) && !LocationUtils.hasMaterialsAround(under, "FENCE")) {
			mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(98), "dif", "dif: -0.5, isIn: " + isInWater + ", isOn: " + isOnWater + ", type: " + type.getId() + ", type Under: " + underType.getId());
		}
		
		if(checkActive("dif-y-2-move")) {
			boolean jesusState = np.booleans.get(JESUS, "state", false);
			if (dif == np.doubles.get(JESUS, "last-y-" + jesusState, 0.0) && isInWater && !np.isInFight) {
				if (!hasOtherThan(under, STATIONARY_WATER) && !p.isSwimming()) {
					mayCancel = Negativity.alertMod(np.getWarn(this) > 10 ? ReportType.VIOLATION : ReportType.WARNING,
							p, this, parseInPorcent((dif + 5) * 10), "dif-y-2-move",
							"Stationary_water aroud him. Difference between 2 y: " + dif
							+ " (other: " + np.doubles.get(JESUS, "last-y-" + (!jesusState), 0.0) + ")");
				}
			}
			np.doubles.set(JESUS, "last-y-" + jesusState, dif);
			np.booleans.set(JESUS, "state", !jesusState);
		}
		
		if(checkActive("distance-in")) {
			double distanceAbs = to.distance(from) - Math.abs(from.getY() - to.getY());
			Location upper = loc.clone().add(0, 1, 0);
			float distanceFall = p.getFallDistance();
			if (isInWater && isOnWater && distanceFall < 1 && distanceAbs > p.getWalkSpeed()
					&& !upper.getBlock().isLiquid() && !p.isFlying()) {
				if (!hasMaterialsAround(loc, "WATER_LILY") && !hasMaterialsAround(upper, "WATER_LILY")
						&& !hasOtherThan(under, "WATER")) {
					mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 98, "distance-in",
							"In water, distance: " + distanceAbs,
							hoverMsg("main", "%distance%", distanceAbs));
				}
			}
		}

		if (isSetBack() && mayCancel)
			p.teleport(p.getLocation().sub(0, 1, 0));
	}
	
	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
