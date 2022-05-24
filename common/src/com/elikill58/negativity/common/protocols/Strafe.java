package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.STRAFE;

public class Strafe extends Cheat implements Listeners {

    public Strafe() {
        super(STRAFE, Cheat.CheatCategory.MOVEMENT, Materials.IRON_INGOT, Cheat.CheatDescription.NO_FIGHT);
    }

    @Check(name = "direction", description = "Check for impossible direction change.", conditions = {CheckConditions.SURVIVAL, CheckConditions.NO_FLY, CheckConditions.NO_GROUND
            , CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_ELYTRA, CheckConditions.NO_FALL_LESS_BLOCK_BELOW, CheckConditions.NO_FALL_LESS_BLOCK})
    public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
        if (e.isMovePosition()) {
            final double deltaX = Math.abs(e.getTo().getX() - e.getFrom().getX());
            final double deltaZ = Math.abs(e.getTo().getZ() - e.getFrom().getZ());

            final double lastX = np.doubles.get(getKey(), "lastX", 0d);
            final double lastZ = np.doubles.get(getKey(), "lastZ", 0d);

            final double lastDeltaX = Math.abs(e.getFrom().getX() - lastX);
            final double lastDeltaZ = Math.abs(e.getFrom().getZ() - lastZ);

            final double deltaXZ = Math.hypot(deltaX, deltaZ);
            final double lastDeltaXZ = Math.hypot(lastDeltaX, lastDeltaZ);

            final double accelerationX = deltaX - lastDeltaX;
            final double accelerationZ = deltaZ - lastDeltaZ;

            final double accelerationXZ = Math.hypot(accelerationX, accelerationZ);

            final double speedAcceleration = Math.abs(deltaXZ - lastDeltaXZ) * 100;

            if (accelerationXZ > 0.1
                    && speedAcceleration < 1
                    && e.getFrom().getY() > e.getTo().getY()) {
                Negativity.alertMod(ReportType.WARNING, e.getPlayer(), this, 100, "direction"
                        , String.format("deltaXZ=%s lastDeltaXZ=%s", deltaXZ, lastDeltaXZ));
            }

            np.doubles.set(getKey(), "lastX", e.getFrom().getX());
            np.doubles.set(getKey(), "lastZ", e.getFrom().getZ());
        }
    }
}