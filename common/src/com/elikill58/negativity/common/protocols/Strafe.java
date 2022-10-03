package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.STRAFE;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.EmptyData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;

public class Strafe extends Cheat implements Listeners {

	public Strafe() {
		super(STRAFE, Cheat.CheatCategory.MOVEMENT, Materials.IRON_INGOT, EmptyData::new, CheatDescription.NO_FIGHT);
	}

	@Check(name = "direction", description = "Check for impossible direction change.", conditions = {
			CheckConditions.SURVIVAL, CheckConditions.NO_FLY, CheckConditions.NO_GROUND, CheckConditions.NO_USE_TRIDENT,
			CheckConditions.NO_ELYTRA, CheckConditions.NO_FALL_LESS_BLOCK })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		if (e.isMovePosition()) {
			final double deltaX = Math.abs(np.delta.getX());
			final double deltaZ = Math.abs(np.delta.getZ());

			final double lastDeltaX = Math.abs(np.lastDelta.getX());
			final double lastDeltaZ = Math.abs(np.lastDelta.getZ());

			final double deltaXZ = Math.hypot(deltaX, deltaZ);
			final double lastDeltaXZ = Math.hypot(lastDeltaX, lastDeltaZ);

			final double accelerationX = deltaX - lastDeltaX;
			final double accelerationZ = deltaZ - lastDeltaZ;

			final double accelerationXZ = Math.hypot(accelerationX, accelerationZ);

			final double speedAcceleration = Math.abs(deltaXZ - lastDeltaXZ) * 100;

			if (accelerationXZ > 0.1 && speedAcceleration < 1 && e.getFrom().getY() > e.getTo().getY()) {
				Negativity.alertMod(ReportType.WARNING, e.getPlayer(), this, 100, "direction",
						"deltaXZ: " + deltaXZ + ", lastDeltaXZ: " + lastDeltaXZ + ", speed: " + speedAcceleration
								+ ", accelerationXZ: " + accelerationXZ);
			}
		}
	}
}