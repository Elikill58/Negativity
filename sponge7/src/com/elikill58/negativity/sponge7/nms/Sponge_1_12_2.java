package com.elikill58.negativity.sponge7.nms;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;

public class Sponge_1_12_2 extends SpongeVersionAdapter {

	public Sponge_1_12_2() {
		super("v1_12_2");
	}

	@Override
	public AbstractChannel getPlayerChannel(Player p) {
		return null;
	}
}
