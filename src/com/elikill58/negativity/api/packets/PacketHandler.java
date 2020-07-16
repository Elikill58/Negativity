package com.elikill58.negativity.api.packets;

public abstract class PacketHandler {

	public abstract void onReceive(AbstractPacket packet);
	public abstract void onSend(AbstractPacket packet);
}
