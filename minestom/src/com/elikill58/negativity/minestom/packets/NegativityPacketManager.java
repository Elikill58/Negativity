package com.elikill58.negativity.minestom.packets;

import com.elikill58.negativity.minestom.MinestomNegativity;
import com.elikill58.negativity.minestom.impl.packet.FabricPacketManager;

public class NegativityPacketManager {

	private FabricPacketManager fabricPacketManager;

	public NegativityPacketManager(MinestomNegativity pl) {
		fabricPacketManager = new CustomPacketManager();
	}

	public FabricPacketManager getSpongePacketManager() {
		return fabricPacketManager;
	}
}
