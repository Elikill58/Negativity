package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.AIR_JUMP;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.common.protocols.data.AirJumpData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AirJump extends Cheat {

	public AirJump() {
		super(AIR_JUMP, CheatCategory.MOVEMENT, Materials.FEATHER, AirJumpData::new);
	}

	@Check(name = "diff-y", description = "Y difference", conditions = { CheckConditions.SURVIVAL,
			CheckConditions.NO_FLY, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_STAIRS_AROUND, CheckConditions.NO_USE_JUMP_BOOST })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np, AirJumpData data) {
		Player p = e.getPlayer();
		if (p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		double diffYtoFromBasic = e.getTo().getY() - e.getFrom().getY();
		if(diffYtoFromBasic == 0.5)
			return; // seems to be stairs
		Location loc = p.getLocation().clone(), locDown = loc.clone().sub(0, 1, 0),
				locDownDown = locDown.clone().sub(0, 1, 0);
		if (locDown.getBlockChecker(1).hasOther("AIR"))
			return;
		Material mDown = locDown.getBlock().getType();
		String idDown = mDown.getId(), idDownDown = locDownDown.getBlock().getType().getId();
		if (idDownDown.contains("STAIR") || idDown.contains("STAIR") || mDown.isSolid())
			return;

		Scheduler.getInstance().runDelayed(() -> {
			double diffYtoFrom = diffYtoFromBasic - Math.abs(e.getTo().getDirection().getY());
			double velY = p.getVelocity().getY();
			if (diffYtoFrom - (velY > 0 ? velY : 0) > 0.35 && data.diffY < diffYtoFrom && data.diffY > velY
					&& data.diffY > p.getTheoricVelocity().getY()) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p,
						this, UniversalUtils.parseInPorcent((int) (diffYtoFrom * 190) - (p.getPing() / 50)), "diff-y",
						"Actual diff Y: " + diffYtoFrom + ", last diff Y: " + data.diffY + ". Down: " + idDown
								+ " / " + idDownDown + ", vel: " + p.getVelocity() + ", diffY base: "
								+ diffYtoFromBasic);
				if (isSetBack() && mayCancel)
					LocationUtils.teleportPlayerOnGround(p);
			}
			data.diffY = diffYtoFrom;
		}, (p.getPing() / 50) + 2);
	}

	@Check(name = "going-down", description = "Going down", conditions = { CheckConditions.SURVIVAL,
			CheckConditions.NO_FLY, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_BLOCK_MID_AROUND,CheckConditions.NO_USE_TRIDENT })
	public void onMoveGoingDown(PlayerMoveEvent e, NegativityPlayer np, AirJumpData data) {
		Player p = e.getPlayer();
		if (p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		Location loc = p.getLocation().clone(), locDown = loc.clone().sub(0, 1, 0),
				locDownDown = locDown.clone().sub(0, 1, 0);
		if (locDown.getBlockChecker(1).hasOther("AIR"))
			return;
		Vector oldVel = p.getVelocity();
		// TODO soon don't use scheduler
		Scheduler.getInstance().runDelayed(() -> {
			if (locDownDown.getBlock().getType().getId().contains("STAIR")
					|| locDown.getBlock().getType().getId().contains("STAIR"))
				return;
			Vector newVel = p.getVelocity();
			if(newVel.length() > oldVel.length() + p.getWalkSpeed()) // check if just get velocity
				return;
			boolean mayCancel = false;

			double diffYtoFromBasic = e.getTo().getY() - e.getFrom().getY();
			double diffYtoFrom = diffYtoFromBasic - Math.abs(e.getTo().getDirection().getY());
			double velLen = p.getVelocity().length();

			if (diffYtoFrom > data.diffY && data.goingDown && diffYtoFrom != 0.5 && velLen < p.getTheoricVelocity().getY()
					&& locDown.getBlock().getType().getId().equalsIgnoreCase("AIR") && velLen < 1.5) { // 0.5 when use stairs or
																						// slab
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(diffYtoFrom * 200), "going-down",
						"Was going down, last y " + data.diffY + ", current: " + diffYtoFrom + ". Down Down: "
								+ locDownDown.getBlock().getType().getId());
			}
			data.goingDown = diffYtoFrom < 0;
			if (isSetBack() && mayCancel)
				LocationUtils.teleportPlayerOnGround(p);
		}, (p.getPing() / 50) + 2);
	}
}
