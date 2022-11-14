package com.elikill58.negativity.api.packets.nms;

import java.util.HashMap;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;

public abstract class NamedVersion {

	protected final HashMap<Integer, PacketType.Client> playIn = new HashMap<>();
	protected final HashMap<Integer, PacketType.Server> playOut = new HashMap<>();
	protected final HashMap<Integer, PacketType.Handshake> handshake = new HashMap<>();
	protected final HashMap<Integer, PacketType.Login> login = new HashMap<>();
	protected final HashMap<Integer, PacketType.Status> status = new HashMap<>();
	
	public NPacket getPacket(PacketDirection dir, int packetId) {
		switch (dir) {
		case CLIENT_TO_SERVER:
			return createPacket(dir, packetId, playIn);
		case SERVER_TO_CLIENT:
			return createPacket(dir, packetId, playOut);
		case HANDSHAKE:
			return createPacket(dir, packetId, handshake);
		case LOGIN:
			return createPacket(dir, packetId, login);
		case STATUS:
			return createPacket(dir, packetId, status);
		}
		return null;
	}
	
	private @NonNull NPacket createPacket(PacketDirection dir, int packetId, HashMap<Integer, ? extends PacketType> packetTypes) {
		PacketType type = packetTypes.get(packetId);
		if(type != null)
			return type.createNewPacket();
		Adapter.getAdapter().debug("Failed to find packetId " + packetId + " for " + dir.name() + " (registered: " + packetTypes.size() + ")");
		return dir.createUnsetPacket("ID:" + packetId);
	}
}
