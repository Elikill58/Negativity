package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThanExtended;
import static com.elikill58.negativity.universal.detections.keys.CheatKeys.AIR_JUMP;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AirJump extends Cheat {

	public AirJump() {
		super(AIR_JUMP, CheatCategory.MOVEMENT, Materials.FEATHER, false, false, "airjump", "air", "jump");
	}

	@Check(name = "diff-y", description = "Y difference", conditions = { CheckConditions.SURVIVAL,
			CheckConditions.NO_FLY, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_USE_TRIDENT })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		Location loc = p.getLocation().clone(), locDown = loc.clone().sub(0, 1, 0),
				locDownDown = locDown.clone().sub(0, 1, 0);

		Scheduler.getInstance().runDelayed(() -> {
			if (hasOtherThanExtended(loc, "AIR") || hasOtherThan(locDown, "AIR") || hasOtherThan(locDownDown, "AIR"))
				return;
			String idDown = locDown.getBlock().getType().getId(), idDownDown = locDownDown.getBlock().getType().getId();
			if (idDownDown.contains("STAIR") || idDown.contains("STAIR"))
				return;

			double diffYtoFromBasic = e.getTo().getY() - e.getFrom().getY();
			double diffYtoFrom = diffYtoFromBasic - Math.abs(e.getTo().getDirection().getY());
			// double diffYtoFrom = e.getTo().getY() - e.getFrom().getY() -
			// Math.abs(e.getTo().getDirection().getY());
			double lastDiffY = np.doubles.get(AIR_JUMP, "diff-y", 0.0);
			if (!np.booleans.get(CheatKeys.ALL, "jump-boost-use", false)) {
				double velY = p.getVelocity().getY();
				if (diffYtoFrom - (velY > 0 ? velY : 0) > 0.35 && lastDiffY < diffYtoFrom && lastDiffY > velY
						&& lastDiffY > p.getTheoricVelocity().getY()
						&& !hasOtherThanExtended(loc.clone().sub(0, 2, 0), "AIR")) {
					boolean mayCancel = Negativity.alertMod(
							diffYtoFrom > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
							this, UniversalUtils.parseInPorcent((int) (diffYtoFrom * 190) - (p.getPing() / 50)), "diff-y",
							"Actual diff Y: " + diffYtoFrom + ", last diff Y: " + lastDiffY + ". Down: " + idDown
									+ " / " + idDownDown + ", vel: " + p.getVelocity() + ", diffY base: "
									+ diffYtoFromBasic);
					if (isSetBack() && mayCancel)
						LocationUtils.teleportPlayerOnGround(p);
				}
				lastDiffY = diffYtoFrom;
			}

			np.doubles.set(AIR_JUMP, "diff-y", diffYtoFrom);
		}, (p.getPing() / 50) + 2);
	}

	@Check(name = "going-down", description = "Going down", conditions = { CheckConditions.SURVIVAL,
			CheckConditions.NO_FLY, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_USE_TRIDENT })
	public void onMoveGoingDown(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		Location loc = p.getLocation().clone(), locDown = loc.clone().sub(0, 1, 0),
				locDownDown = locDown.clone().sub(0, 1, 0);

		Scheduler.getInstance().runDelayed(() -> {
			if (hasOtherThanExtended(loc, "AIR") || hasOtherThan(locDown, "AIR") || hasOtherThan(locDownDown, "AIR"))
				return;
			if (locDownDown.getBlock().getType().getId().contains("STAIR")
					|| locDown.getBlock().getType().getId().contains("STAIR"))
				return;
			boolean mayCancel = false;

			double diffYtoFromBasic = e.getTo().getY() - e.getFrom().getY();
			double diffYtoFrom = diffYtoFromBasic - Math.abs(e.getTo().getDirection().getY());
			double lastDiffY = np.doubles.get(AIR_JUMP, "diff-y", 0.0), velLen = p.getVelocity().length();

			boolean wasGoingDown = np.booleans.get(AIR_JUMP, "going-down", false);
			if (diffYtoFrom > lastDiffY && wasGoingDown && diffYtoFrom != 0.5 && velLen < p.getTheoricVelocity().getY()
					&& locDown.getBlock().getType().getId().equalsIgnoreCase("AIR") && velLen < 1.5) { // 0.5 when use stairs or
																						// slab
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(diffYtoFrom * 200), "going-down",
						"Was going down, last y " + lastDiffY + ", current: " + diffYtoFrom + ". Down Down: "
								+ locDownDown.getBlock().getType().getId());
			}
			np.booleans.set(AIR_JUMP, "going-down", diffYtoFrom < 0);
			if (isSetBack() && mayCancel)
				LocationUtils.teleportPlayerOnGround(p);
		}, (p.getPing() / 50) + 2);
	}
}
