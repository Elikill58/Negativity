package com.elikill58.negativity.spigot.nms;

import com.elikill58.negativity.spigot.utils.PacketUtils;

public class Spigot_1_20_R1 extends SpigotVersionAdapter {

	public Spigot_1_20_R1() {
		super(PacketUtils.getProtocolVersion());
	}

	@Override
	public String getTpsFieldName() {
		return "k";
	}
}
