package com.elikill58.negativity.api.packets.nms;

import java.util.HashMap;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Handshake;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;

public abstract class NamedVersion {

	protected final HashMap<Integer, PacketType.Client> playIn = new HashMap<>();
	protected final HashMap<Integer, PacketType.Server> playOut = new HashMap<>();
	protected final HashMap<Integer, PacketType.Handshake> handshake = new HashMap<>();
	protected final HashMap<Integer, PacketType.Login> login = new HashMap<>();
	protected final HashMap<Integer, PacketType.Status> status = new HashMap<>();
	protected final HashMap<Integer, EntityType> entityTypes = new HashMap<>();
	protected final HashMap<Integer, String> materials = new HashMap<>();
	
	public NamedVersion() {
		handshake.put(0, Handshake.IS_SET_PROTOCOL);
	}
	
	/**
	 * Create packet with given ID for the given direction.
	 * <p>
	 * Return unset packet if not found
	 * 
	 * @param dir direction of packet (for example: server to client)
	 * @param packetId the ID of the packet
	 * @return new created packet
	 */
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
	
	/**
	 * Get entity type according to given ID.
	 * <br>
	 * Return {@link EntityType#UNKNOWN} is not found
	 * 
	 * @param id the entity type id
	 * @return type of entity or unknown
	 */
	public @NonNull EntityType getEntityType(int id) {
		if(!entityTypes.containsKey(id))
			Adapter.getAdapter().debug("Can't find entity type with id " + id);
		return entityTypes.getOrDefault(id, EntityType.UNKNOWN);
	}
	
	public Material getMaterial(int id) {
		if(!materials.containsKey(id))
			Adapter.getAdapter().debug("Can't find material with id " + id);
		return ItemRegistrar.getInstance().get(materials.getOrDefault(id, "stone"));
	}
}
