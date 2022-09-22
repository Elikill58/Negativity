package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.protocols.CheckData;

public class CriticalData extends CheckData {

	public List<NPacketPlayInPositionLook> positions = new ArrayList<>(Arrays.asList(new NPacketPlayInPositionLook(),
			new NPacketPlayInPositionLook(), new NPacketPlayInPositionLook(), new NPacketPlayInPositionLook()));

	public CriticalData(NegativityPlayer np) {
		super(np);
	}

	public void add(NPacketPlayInPositionLook packet) {
		positions.add(packet);
		positions.remove(0);
	}
}
