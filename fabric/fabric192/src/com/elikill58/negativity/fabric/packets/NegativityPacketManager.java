package com.elikill58.negativity.fabric.packets;

import com.elikill58.negativity.fabric.FabricNegativity;

public class NegativityPacketManager {

	private FabricPacketManager fabricPacketManager;

	public NegativityPacketManager(FabricNegativity pl) {
		fabricPacketManager = new CustomPacketManager();
	}

	public FabricPacketManager getSpongePacketManager() {
		return fabricPacketManager;
	}
}
