package com.elikill58.negativity.spigot.nms;

import java.util.HashMap;
import java.util.function.Function;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;

public class PacketTransferManager<T extends NPacket> {

	protected HashMap<String, Function<Object, T>> bukkitToNegativity = new HashMap<>();
	protected HashMap<PacketType, Function<T, Object>> negativityToBukkit = new HashMap<>();
	
	public PacketTransferManager() {
		
	}
	
	public void addTo(String packetName, Function<Object, T> bukkitToNegativiy) {
		this.bukkitToNegativity.put(packetName, bukkitToNegativiy);
	}
	
	public void addFrom(PacketType packetName, Function<T, Object> negativityToBukkit) {
		this.negativityToBukkit.put(packetName, negativityToBukkit);
	}
	
	public T bukkitToNegativity(Object nmsPacket) {
		return bukkitToNegativity.get(nmsPacket).apply(nmsPacket);
	}
	
	public Object negativityToBukkit(T npacket) {
		return negativityToBukkit.get(npacket.getPacketType()).apply(npacket);
	}
}
