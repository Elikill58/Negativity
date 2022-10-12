package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
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

	@Check(name = "distance-jumping", description = "Distance when jumping", conditions = {
			CheckConditions.NO_USE_TRIDENT, CheckConditions.SURVIVAL, CheckConditions.NO_ICE_AROUND,
			CheckConditions.NO_INSIDE_VEHICLE })
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

	@Check(name = "walk-speed", description = "Check the walk speed", conditions = { CheckConditions.NO_FIGHT,
			CheckConditions.SURVIVAL, CheckConditions.NO_ICE_AROUND })
	public void onWalkSpeed(PacketReceiveEvent e, NegativityPlayer np, SpeedData data) {
		if (!e.getPacket().getPacketType().isFlyingPacket())
			return;
		NPacketPlayInFlying flying = (NPacketPlayInFlying) e.getPacket().getPacket();
		if (!flying.hasPos)
			return;
		Player p = e.getPlayer();

		Location blockLoc = flying.getLocation(p.getWorld());
		double deltaXZ = blockLoc.distanceXZ(p.getLocation());

		if (deltaXZ == 0 /* || !p.isOnGround() */) {
			data.deltaXZ = deltaXZ;
			data.reduceWalkSpeedBuffer(1);
			return;
		}

		blockLoc.setY(p.getBoundingBox().getMinY() - 1);
		Block block = blockLoc.getBlock();

		/*
		 * Air resistance is automatically being calculated; We don't need to exempt AIR
		 * blocks since they have a friction of 0.6 and not 0.98 (default drag)
		 *
		 * if (block.getType() == Material.AIR) break friction;
		 */

		double friction = block.getFriction() * 0.91f;

		double predicted = data.deltaXZ * friction;

		double difference = predicted - data.deltaXZ;

		double minSpeed;
		if (p.isFlying())
			minSpeed = (p.getFlySpeed() + (np.blockAbove == 0 ? 0 : 0.1) + 0.0864) * (p.isSprinting() ? 2 : 1);
		else
			minSpeed = (p.getWalkSpeed() + (np.blockAbove == 0 ? 0 : 0.1) + 0.0864) * (p.isSprinting() ? 1.32 : 1);

		double speedEffect = data.getSpeedModifier();
		if (p.isOnGround()) {
			minSpeed *= speedEffect;
		} else {
			minSpeed += 0.08;
			if(speedEffect != 1)
				minSpeed *= speedEffect * 0.28;
		}
		double minDifference = np.blockAbove == 0 ? minSpeed : minSpeed + 0.5;

		if (deltaXZ > minSpeed && predicted > minSpeed && Math.abs(difference) > minDifference) {

			// p.sendMessage("!! " + (deltaXZ > minSpeed ? "§c" : "§e") +
			// String.format("%.4f", deltaXZ) + " > " + String.format("%.3f", minSpeed) + "
			// §f&& " + (predicted > multiplication ? "§c" : "§a") + String.format("%.4f",
			// predicted) + " > " + String.format("%.3f", multiplication) + " §f&& " +
			// (Math.abs(difference) > minDifference ? "§c" : "§b") + String.format("%.4f",
			// Math.abs(difference)) + " > " + String.format("%.4f", minDifference));

			if (++data.walkSpeedBuffer > 2.8) {
				Negativity.alertMod(ReportType.WARNING, p, this, 99, "walk-speed",
						(deltaXZ > minSpeed ? "Y " : "X ") + String.format("%.4f", deltaXZ) + " > "
								+ String.format("%.3f", minSpeed) + " && " + (predicted > minSpeed ? "Y " : "X ")
								+ String.format("%.4f", predicted) + " > " + String.format("%.3f", minSpeed)
								+ " && " + (Math.abs(difference) > minDifference ? "Y " : "X ")
								+ String.format("%.4f", Math.abs(difference)) + " > "
								+ String.format("%.4f", minDifference) + ", buffer: " + data.walkSpeedBuffer + ", ws: " + String.format("%.2f", p.getWalkSpeed()),
						null, (long) (data.walkSpeedBuffer - 1.8));

				data.walkSpeedBuffer *= 0.7;
			}
		} else {
			// p.sendMessage((deltaXZ > minSpeed ? "§c" : "§e") + String.format("%.4f",
			// deltaXZ) + " > " + String.format("%.3f", minSpeed) + " §f&& " + (predicted >
			// multiplication ? "§c" : "§a") + String.format("%.4f", predicted) + " > " +
			// String.format("%.3f", multiplication) + " §f&& " + (Math.abs(difference) >
			// minDifference ? "§c" : "§b") + String.format("%.4f", Math.abs(difference)) +
			// " > " + String.format("%.4f", minDifference));
			data.reduceWalkSpeedBuffer(p.isOnGround() ? 0.5 : 0.8);
		}
		data.deltaXZ = deltaXZ;
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
