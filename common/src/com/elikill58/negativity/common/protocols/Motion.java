package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.MotionData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Motion extends Cheat {

	public Motion() {
		super(CheatKeys.MOTION, CheatCategory.MOVEMENT, Materials.ANDESITE, MotionData::new);
	}

	@Check(name = "y-motion", description = "Consistent y-axis motions", conditions = { CheckConditions.NO_FLY, CheckConditions.NO_CLIMB_BLOCK, CheckConditions.NO_USE_JUMP_BOOST, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_USE_TRIDENT })
    public void onReceivePacket(PacketReceiveEvent e, NegativityPlayer np, MotionData data) {
		NPacket pa = e.getPacket();
		PacketType type = pa.getPacketType();
        if (!type.isFlyingPacket())
        	return;
        NPacketPlayInFlying flying = (NPacketPlayInFlying) pa;
        if(!flying.hasPos)
        	return;

        double lastDeltaY = np.lastDelta.getY();
        double deltaY = np.delta.getY();
        
        if (deltaY == 0) return;

        double offset = Math.abs(deltaY) / Math.abs(lastDeltaY);

        if (offset == 1) {
            if (++data.buffer > 5) {
            	Negativity.alertMod(ReportType.WARNING, e.getPlayer(), this, UniversalUtils.parseInPorcent(90 + data.buffer), "y-motion", "Offset: " + offset + ", deltaY: " + deltaY + ", lastDeltaY: " + lastDeltaY, null, data.buffer - 4);
            }
        } else {
        	data.buffer = 0;
        }
    }
}
