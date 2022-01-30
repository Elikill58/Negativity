package com.elikill58.negativity.api.packets;

import java.util.HashMap;
import java.util.function.BiFunction;

import com.elikill58.negativity.api.packets.packet.NPacket;

public class PacketTransferManager<T extends NPacket, R> {

	protected final HashMap<String, BiFunction<R, Object, T>> bukkitToNegativity = new HashMap<>();
	protected final HashMap<PacketType, BiFunction<R, T, Object>> negativityToBukkit = new HashMap<>();
	protected final BiFunction<R, Object, T> fallbackBukkitToNegativity;
	protected final BiFunction<R, T, Object> fallBackNegativityToBukkit;
	
	public PacketTransferManager(BiFunction<R, Object, T> fallbackBukkitToNegativity, BiFunction<R, T, Object> fallBackNegativityToBukkit) {
		this.fallbackBukkitToNegativity = fallbackBukkitToNegativity;
		this.fallBackNegativityToBukkit = fallBackNegativityToBukkit;
	}
	
	public void addTo(String packetName, BiFunction<R, Object, T> bukkitToNegativiy) {
		this.bukkitToNegativity.put(packetName, bukkitToNegativiy);
	}
	
	public void addFrom(PacketType packetName, BiFunction<R, T, Object> negativityToBukkit) {
		this.negativityToBukkit.put(packetName, negativityToBukkit);
	}
	
	public T bukkitToNegativity(R p, Object nmsPacket) {
		return bukkitToNegativity.getOrDefault(nmsPacket, fallbackBukkitToNegativity).apply(p, nmsPacket);
	}
	
	public Object negativityToBukkit(R p, T npacket) {
		return negativityToBukkit.getOrDefault(npacket.getPacketType(), fallBackNegativityToBukkit).apply(p, npacket);
	}

	public int size() {
		return bukkitToNegativity.size() + negativityToBukkit.size();
	}
}
