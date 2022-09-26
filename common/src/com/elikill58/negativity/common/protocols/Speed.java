package com.elikill58.negativity.common.protocols;

import java.util.Optional;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.SpeedData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Speed extends Cheat implements Listeners {

	public Speed() {
		super(CheatKeys.SPEED, CheatCategory.MOVEMENT, Materials.BEACON, SpeedData::new, CheatDescription.NO_FIGHT);
	}

	@Check(name = "distance-jumping", description = "Distance when jumping", conditions = { CheckConditions.NO_USE_TRIDENT, CheckConditions.SURVIVAL, CheckConditions.NO_ICE_AROUND })
	public void onDistanceJumping(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Location from = e.getFrom(), to = e.getTo();
		double amplifierSpeed = p.getPotionEffect(PotionEffectType.SPEED)
				.orElseGet(() -> new PotionEffect(PotionEffectType.SPEED, 0, 0)).getAmplifier();
		double y = to.toVector().clone().setY(0).distance(from.toVector().clone().setY(0)),
				velLen = p.getVelocity().length();
		boolean onGround = p.isOnGround();
		if (!onGround && (y - (amplifierSpeed / 10) - (velLen > 0.45 ? velLen : 0)) >= 0.85D
				&& p.getTheoricVelocity().length() < 0.85D && p.getVelocity().length() < 0.4) { // theoric length to
																								// when the new high
																								// velocity is actually
																								// taken
			Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(y * 190), "distance-jumping",
					"WS: " + p.getWalkSpeed() + ", fd: " + p.getFallDistance() + ", from/to: "
							+ String.format("%.10f", y) + ", ySpeed: "
							+ String.format("%.10f", y - (amplifierSpeed / 10) - (velLen > 0.5 ? velLen : 0))
							+ ", vel: " + p.getVelocity() + ", thvel: " + p.getTheoricVelocity(),
					hoverMsg("distance_jumping", "%distance%", String.format("%.4f", y)));
		}
	}

	@Check(name = "same-diff", description = "Check for same Y movement", conditions = { CheckConditions.NO_ELYTRA,
			CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_USE_ELEVATOR })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np, SpeedData data) {
		Player p = e.getPlayer();
		Location from = e.getFrom(), to = e.getTo();
		double dif = to.getY() - from.getY();
		if (dif != 0.0 && data.sameDiffY != 0.0) {
			if (Math.abs(dif) == Math.abs(data.sameDiffY)) {
				if (Negativity.alertMod(ReportType.WARNING, p, this, 95, "same-diff",
						"Differences : " + dif + " / " + data.sameDiffY) && isSetBack())
					e.setCancelled(true);
			}
			data.sameDiffY = dif;
		}
	}

	@Check(name = "walk-speed", description = "Check the walk speed", conditions = { CheckConditions.NO_FIGHT, CheckConditions.SURVIVAL, CheckConditions.NO_ICE_AROUND })
	public void onWalkSpeed(PlayerMoveEvent e, SpeedData data) {
		Player p = e.getPlayer();
		Location from = e.getFrom(), to = e.getTo();
		double dif = to.getY() - from.getY();

		double maxDistance = (p.isFlying() ? p.getFlySpeed() : p.getWalkSpeed()) * (p.isSprinting() ? 1.32 : 1);

		// status values from: https://www.mcpk.wiki/wiki/Status_Effects
		Optional<PotionEffect> optSpeed = p.getPotionEffect(PotionEffectType.SPEED);
		if (optSpeed.isPresent()) {
			int amplifierSpeed = optSpeed.get().getAmplifier();
			maxDistance *= 1 + (double) amplifierSpeed * 0.2;
			data.oldSpeedLevel = amplifierSpeed;
		} else if (data.oldSpeedLevel > 0) {
			maxDistance *= 1 + (double) data.oldSpeedLevel * 0.2;
			data.oldSpeedLevel--;
		}

		Optional<PotionEffect> optSlow = p.getPotionEffect(PotionEffectType.SLOWNESS);
		if (optSlow.isPresent()) {
			int amplifierSlow = optSlow.get().getAmplifier();
			maxDistance *= (double) amplifierSlow * 0.15;
			data.oldSlowLevel = amplifierSlow;
		} else if (data.oldSlowLevel > 0) {
			maxDistance *= (double) data.oldSlowLevel * 0.15;
			data.oldSlowLevel--;
		}

		maxDistance += p.getTheoricVelocity().length();

		double distance = from.distanceXZ(to);
		if (distance > maxDistance) {
			double difDistance = distance - maxDistance;
			Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(difDistance * 500),
					"walk-speed",
					"Distance: " + distance + ", maxDistance: " + maxDistance + ", yDif: " + dif + ", fall: " + p.getFallDistance() + ", vel: " + p.getTheoricVelocity() + ", ice: " + NegativityPlayer.getNegativityPlayer(p).iceCounter,
					new CheatHover.Literal("Move with " + String.format("%.3f", distance) + " but should move max "
							+ String.format("%.3f", maxDistance)),
					(long) (difDistance * 50));
		}
	}

	@Check(name = "high-speed", description = "Distance with high speed", conditions = {
			CheckConditions.NO_USE_JUMP_BOOST, CheckConditions.NO_USE_SLIME, CheckConditions.NO_SWIM })
	public void onHighSpeed(PlayerMoveEvent e, SpeedData data) {
		Player p = e.getPlayer();
		Location from = e.getFrom(), to = e.getTo();
		Location loc = p.getLocation().clone();
		Block under = loc.clone().sub(0, 1, 0).getBlock();
		double distance = from.distance(to);
		double y = to.toVector().clone().setY(0).distance(from.toVector().clone().setY(0));
		boolean onGround = p.isOnGround();
		if (!onGround && y < 0.85D && !under.getType().getId().contains("STEP")
				&& !under.getType().getId().contains("VOID")
				&& !(under.getType().getId().contains("WATER") || under.isWaterLogged() || under.isLiquid())
				&& p.getVelocity().length() < 1.5) {
			Location toHigh = to.clone();
			toHigh.setY(from.getY());
			double yy = toHigh.distance(from);
			if ((distance - Math.abs(p.getVelocity().getY() * 0.95)) > 0.45 && (distance > (yy * 2))
					&& p.getFallDistance() < 1) {
				if (++data.highSpeedAmount > 4)
					Negativity.alertMod(ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(86 + data.highSpeedAmount), "high-speed",
							"HighSpeed - Under: " + under.getType().getId() + ", Speed: " + distance + ", nb: "
									+ data.highSpeedAmount + ", FD: " + p.getFallDistance() + ", y: " + yy + ", vel "
									+ p.getVelocity());
			} else
				data.highSpeedAmount = 0;
		}
	}
	
}
