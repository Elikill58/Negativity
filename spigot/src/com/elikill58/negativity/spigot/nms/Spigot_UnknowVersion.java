package com.elikill58.negativity.spigot.nms;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.location.BlockPosition;
import com.elikill58.negativity.spigot.SpigotNegativity;

public class Spigot_UnknowVersion extends SpigotVersionAdapter {

	private static final float[] b = new float[65536];

	static {
		for (int var1 = 0; var1 < b.length; var1++)
			b[var1] = (float) Math.sin(var1 * Math.PI * 2.0D / 65536.0D);
	}
	
	public Spigot_UnknowVersion(String version) {
		super(version);
		SpigotNegativity.getInstance().getLogger().warning("Failed to find version adapter for " + version + ".");
	}

	@Override
	protected String getOnGroundFieldName() {
		return "f";
	}

	@Override
	public double getAverageTps() {
		return 0;
	}

	@Override
	public int getPlayerPing(Player player) {
		return 0;
	}

	@Override
	public float cos(float f) {
		return b[(int) (f * 10430.378F + 16384.0F) & 0xFFFF];
	}

	@Override
	public float sin(float f) {
		return b[(int) (f * 10430.378F) & 0xFFFF];
	}
	
	@Override
	public BlockPosition getBlockPosition(Object obj) {
		return new BlockPosition(get(obj, "x"), get(obj, "y"), get(obj, "z"));
	}
}
