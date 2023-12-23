package com.elikill58.negativity.spigot.nms;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.utils.PacketUtils;

public class Spigot_UnknowVersion extends SpigotVersionAdapter {

	private static final float[] b = new float[65536];

	static {
		for (int var1 = 0; var1 < b.length; var1++)
			b[var1] = (float) Math.sin(var1 * Math.PI * 2.0D / 65536.0D);
	}
	
	public Spigot_UnknowVersion(String version) {
		super(PacketUtils.getProtocolVersion());
		SpigotNegativity.getInstance().getLogger().warning("Failed to find version adapter for " + version + ".");
	}

	@Override
	public String getTpsFieldName() {
		return "f";
	}
}
