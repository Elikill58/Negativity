package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.spigot.utils.ItemUtils.STATIONARY_WATER;
import static com.elikill58.negativity.spigot.utils.LocationUtils.hasMaterialsAround;
import static com.elikill58.negativity.spigot.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.spigot.utils.LocationUtils.hasOtherThanExtended;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.blocks.SpigotLocation;
import com.elikill58.negativity.spigot.listeners.NegativityPlayerMoveEvent;
import com.elikill58.negativity.spigot.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;

public class JesusProtocol extends Cheat implements Listener {

	public JesusProtocol() {
		super(CheatKeys.JESUS, false, Material.WATER_BUCKET, CheatCategory.MOVEMENT, true, "waterwalk", "water",
				"water walk");
	}

	@EventHandler
	public void onPlayerMove(NegativityPlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if (!np.hasDetectionActive(this))
			return;
		if (np.hasElytra() || p.isInsideVehicle() || Utils.isSwimming(p) || np.isUsingTrident())
			return;
		SpigotLocation loc = new SpigotLocation(p.getLocation()), to = e.getTo(), from = e.getFrom(),
				under = loc.clone().subtract(0, 1, 0);
		if (to.getWorld() != from.getWorld() || hasMaterialsAround(loc, "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET", "LILY", "STEP", "FENCE", "BED")
				|| hasMaterialsAround(under, "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET", "LILY", "STEP", "FENCE",
						"BED"))
			return;
		Material type = loc.getBlock().getType(), underType = under.getBlock().getType();
		boolean isInWater = type.name().contains("WATER"), isOnWater = underType.name().contains("WATER");
		boolean mayCancel = false;
		double dif = e.getFrom().getY() - e.getTo().getY();
		if (type.name().equalsIgnoreCase("AIR") && isOnWater && !LocationUtils.hasBoatAroundHim(loc) && !p.isFlying()) {
			if (!hasOtherThanExtended(under, STATIONARY_WATER)) {
				double reliability = 0;
				if (dif < 0.0005 && dif > 0.00000005)
					reliability = dif * 10000000 - 1;
				else if (dif < 0.1 && dif > 0.089)
					reliability = dif * 1000;
				else if (dif == 0.5)
					reliability = 75;
				else if (dif < 0.30001 && dif > 0.3000)
					reliability = dif * 100 * 2.5;
				else if (dif < 0.002 && dif > -0.002 && dif != 0.0)
					reliability = Math.abs(dif * 5000);
				else if (dif == 0.0)
					reliability = 90;
				mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(reliability),
						"(Stationary_water aroud him) Diff: " + dif + " and ping: " + np.ping);
			}
		}
		if (dif == -0.5 && (isInWater || isOnWater) && !type.name().contains("FENCE")) {
			mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(98),
					"dif: -0.5, isIn: " + isInWater + ", isOn: " + isOnWater + ", type: " + type.name()
							+ ", type Under: " + underType.name());
		}

		boolean jesusState = np.contentBoolean.getOrDefault("jesus-state", false);
		if (dif == np.contentDouble.getOrDefault("jesus-last-y-" + jesusState, 0.0) && isInWater && !np.isInFight) {
			if (!hasOtherThan(under, "AIR", "WATER")) {
				mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 10 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, parseInPorcent((dif + 5) * 10),
						"(Stationary_water aroud him) Difference between 2 y: " + dif + " (other: "
								+ np.contentDouble.getOrDefault("jesus-last-y-" + (!jesusState), 0.0) + ") and ping: "
								+ np.ping);
			}
		}
		np.contentDouble.put("jesus-last-y-" + jesusState, dif);
		np.contentBoolean.put("jesus-state", !jesusState);

		double distanceAbs = to.distance(from) - Math.abs(from.getY() - to.getY());
		SpigotLocation upper = loc.clone().add(0, 1, 0);
		float distanceFall = p.getFallDistance();
		if (isInWater && isOnWater && distanceFall < 1 && distanceAbs > p.getWalkSpeed() && !upper.getBlock().isLiquid()
				&& !p.isFlying()) {
			if (!hasMaterialsAround(loc, "WATER_LILY") && !hasMaterialsAround(upper, "WATER_LILY")
					&& !hasOtherThan(under, "WATER")) {
				mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.JESUS), 98,
						"In water, distance: " + distanceAbs + ", ping: " + np.ping,
						hoverMsg("main", "%distance%", distanceAbs));
			}
		}

		if (isSetBack() && mayCancel)
			p.teleport(p.getLocation().subtract(0, 1, 0));
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
