package com.elikill58.negativity.common.server;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.impl.CompensatedWorld;
import com.elikill58.negativity.api.impl.entity.CompensatedEntity;
import com.elikill58.negativity.api.impl.entity.CompensatedPlayer;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityDestroy;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutMultiBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutRespawn;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnPlayer;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.universal.Adapter;

public class NegativityPacketOutListener implements Listeners {

	@EventListener
	public void onPacketSend(PacketSendEvent e) {
		if(!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		NPacket packet = (NPacket) e.getPacket();
		PacketType type = packet.getPacketType();
		if(type.equals(PacketType.Server.SPAWN_ENTITY)) {
			NPacketPlayOutSpawnEntity spawn = (NPacketPlayOutSpawnEntity) packet;
			if(spawn.type.equals(EntityType.PIG))
				Adapter.getAdapter().debug("Spawning entity " + spawn.entityId + ", type: " + spawn.type);
			CompensatedEntity et = new CompensatedEntity(spawn.entityId, spawn.type, p.getWorld());
			et.setLocation(new Location(p.getWorld(), spawn.x, spawn.y, spawn.z));
			p.getWorld().addEntity(et);
		} else if(type.equals(PacketType.Server.SPAWN_PLAYER)) {
			NPacketPlayOutSpawnPlayer spawn = (NPacketPlayOutSpawnPlayer) packet;
			CompensatedPlayer et = new CompensatedPlayer(spawn.entityId, spawn.uuid, p.getWorld());
			et.setLocation(new Location(p.getWorld(), spawn.x, spawn.y, spawn.z));
			p.getWorld().addEntity(et);
		} else if(type.equals(PacketType.Server.RESPAWN)) {
			NPacketPlayOutRespawn respawn = (NPacketPlayOutRespawn) packet;
			if(p instanceof CompensatedPlayer) {
				if(p.getWorld() != null && p.getWorld().getName() == respawn.worldName)
					return; // don't change world
				CompensatedPlayer cp = (CompensatedPlayer) p;
				Adapter.getAdapter().debug("Changing world " + p.getWorld().getEntities().size() + " to " + respawn.worldName);
				CompensatedWorld world = new CompensatedWorld(p);
				world.setName(respawn.worldName);
				cp.setWorld(world);
			}
		} else if(type.equals(PacketType.Server.ENTITY_DESTROY)) {
			NPacketPlayOutEntityDestroy destroy = (NPacketPlayOutEntityDestroy) packet;
			for(int ids : destroy.entityIds)
				p.getWorld().removeEntity(ids);
		} else if(type.isFlyingPacket()) {
			NPacketPlayOutEntity flying = (NPacketPlayOutEntity) packet;
			p.getWorld().getEntityById(flying.entityId).ifPresent(entity -> {
				if(entity instanceof CompensatedEntity) {
					CompensatedEntity et = (CompensatedEntity) entity;
					et.setLocation(et.getLocation().add(flying.deltaX, flying.deltaY, flying.deltaZ));
				}
			});
		} else if(type.equals(PacketType.Server.BLOCK_CHANGE)) {
			NPacketPlayOutBlockChange change = (NPacketPlayOutBlockChange) packet;
			CompensatedWorld w = p.getWorld();
			Adapter.getAdapter().debug("Set " + change.type + " to " + change.pos);
			w.setBlock(change.type, change.pos.toLocation(w));
		} else if(type.equals(PacketType.Server.MULTI_BLOCK_CHANGE)) {
			NPacketPlayOutMultiBlockChange change = (NPacketPlayOutMultiBlockChange) packet;
			CompensatedWorld w = p.getWorld();
			change.blockStates.forEach((pos, m) -> w.setBlock(m, pos.toLocation(w)));
			change.blockStates.forEach((pos, m) -> Adapter.getAdapter().debug("[Multi] Set " + m + " to " + pos));
		} else if(!type.isFlyingPacket() && !type.equals(Server.LIGHT_UPDATE))
			Adapter.getAdapter().debug("Sending packet " + type.getPacketName() + " " + (packet instanceof NPacketPlayOutUnset ? ((NPacketPlayOutUnset) packet).getPacketTypeCible().getPacketName() : ""));
	}
}
