package com.elikill58.negativity.spigot.packets;

public abstract class PacketHandler {

	public abstract void onReceive(AbstractPacket packet);
	public abstract void onSend(AbstractPacket packet);
}
