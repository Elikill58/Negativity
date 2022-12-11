package com.elikill58.negativity.common.server;

import java.util.Optional;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.events.packets.PrePacketSendEvent;
import com.elikill58.negativity.api.impl.CompensatedWorld;
import com.elikill58.negativity.api.impl.entity.CompensatedEntity;
import com.elikill58.negativity.api.impl.entity.CompensatedPlayer;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutCustomPayload;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityDestroy;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutMultiBlockChange;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnEntityLiving;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnPlayer;
import com.elikill58.negativity.universal.Adapter;

public class NegativityPacketOutListener implements Listeners {

	public int nb = 0;
	public double dx = 0, dy = 0, dz = 0;
	
	@EventListener
	public void onPacketSend(PacketSendEvent e) {
		if(!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		NPacket packet = (NPacket) e.getPacket();
		PacketType type = packet.getPacketType();
		if(type.equals(PacketType.Server.SPAWN_ENTITY)) {
			NPacketPlayOutSpawnEntity spawn = (NPacketPlayOutSpawnEntity) packet;
			CompensatedEntity et = new CompensatedEntity(spawn.entityId, spawn.type, p.getWorld());
			et.setLocation(new Location(p.getWorld(), spawn.x, spawn.y, spawn.z, spawn.yaw, spawn.pitch));
			p.getWorld().addEntity(et);
			//Adapter.getAdapter().debug("Spawned entity " + spawn.entityId + " / " + spawn.type + " for " + p.getName());
		} else if(type.equals(PacketType.Server.SPAWN_PLAYER)) {
			NPacketPlayOutSpawnPlayer spawn = (NPacketPlayOutSpawnPlayer) packet;
			CompensatedPlayer et = new CompensatedPlayer(spawn.entityId, spawn.uuid, p.getWorld());
			et.setLocation(new Location(p.getWorld(), spawn.x, spawn.y, spawn.z, spawn.yaw, spawn.pitch));
			p.getWorld().addEntity(et);
			Adapter.getAdapter().debug("Spawn player " + spawn.entityId + " / " + et.getLocation());
		} else if(type.equals(PacketType.Server.SPAWN_ENTITY_LIVING)) {
			NPacketPlayOutSpawnEntityLiving spawn = (NPacketPlayOutSpawnEntityLiving) packet;
			CompensatedEntity et = new CompensatedEntity(spawn.entityId, spawn.type, p.getWorld());
			et.setLocation(new Location(p.getWorld(), spawn.x, spawn.y, spawn.z));
			p.getWorld().addEntity(et);
			Adapter.getAdapter().debug("Spawned living " + spawn.entityId + " / " + spawn.type + " for " + p.getName());
		} else if(type.equals(PacketType.Server.ENTITY_TELEPORT)) {
			NPacketPlayOutEntityTeleport teleport = (NPacketPlayOutEntityTeleport) packet;
			p.getWorld().getEntityById(teleport.entityId).ifPresent(et -> {
				if(et instanceof CompensatedEntity) {
					((CompensatedEntity) et).setLocation(teleport.getLocation(p.getWorld()));
				} else if(et instanceof CompensatedPlayer) {
					Adapter.getAdapter().debug("Teleporting " + et.getName() + " to " + teleport.x + ", " + teleport.y + ", " + teleport.z);
					((CompensatedPlayer) et).setLocation(teleport.getLocation(p.getWorld()));
				} else
					Adapter.getAdapter().debug("Failed to find valid class for entity " + teleport.entityId + " and class " + et.getClass().getSimpleName());
			});
		} else if(type.equals(PacketType.Server.ENTITY_DESTROY)) {
			NPacketPlayOutEntityDestroy destroy = (NPacketPlayOutEntityDestroy) packet;
			for(int ids : destroy.entityIds)
				p.getWorld().removeEntity(ids);
		} else if(type.isFlyingPacket()) {
			NPacketPlayOutEntity flying = (NPacketPlayOutEntity) packet;
			Optional<Entity> optEt = p.getWorld().getEntityById(flying.entityId);
			if(optEt.isPresent()) {
				Entity et = optEt.get();
				if(!(et instanceof CompensatedEntity) && !(et instanceof CompensatedPlayer)) {
					Adapter.getAdapter().debug("Entity with ID " + flying.entityId + " (named " + et.getName() + ") isn't compensated.");
					return;
				}
				/*if(et instanceof Player && (flying.deltaX != 0 || flying.deltaZ != 0))
					Adapter.getAdapter().debug("Move player " + et.getName() + " of " + flying.deltaX + " / " + flying.deltaY + " / " + flying.deltaZ);*/
				if(et instanceof Player) {
					Player platform = Adapter.getAdapter().getPlayer(((Player) et).getUniqueId());
					Location l = platform.getLocation(), cl = et.getLocation();
					nb++;
					dx += Math.abs(l.getX() - cl.getX());
					dy += Math.abs(l.getY() - cl.getY());
					dz += Math.abs(l.getZ() - cl.getZ());
					if(l.distance(cl) > 0.1) {
						Adapter.getAdapter().debug(p.getName() + " distance: " + (l.getX() - cl.getX()) + " / " + (l.getY() - cl.getY()) + " / " + (l.getZ() - cl.getZ()) + " > dx: "
									+ String.format("%.2f", (dx / nb)) + ", dy: " + String.format("%.2f", (dy / nb)) +", dz: " + String.format("%.2f", (dz / nb)));
					}
				}
				Location loc = et.getLocation();
				loc.add(flying.deltaX, flying.deltaY, flying.deltaZ);
				loc.setYaw(flying.yaw);
				loc.setPitch(flying.pitch);
			} else
				Adapter.getAdapter().debug("Failed to find entity with ID " + flying.entityId + " for player " + p.getName() + " and world " + p.getWorld());
		}
	}
	
	@EventListener
	public void onPacketPreSend(PrePacketSendEvent e) {
		if(!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		NPacket packet = (NPacket) e.getPacket();
		PacketType type = packet.getPacketType();
		if(type.equals(PacketType.Server.BLOCK_CHANGE)) {
			NPacketPlayOutBlockChange change = (NPacketPlayOutBlockChange) packet;
			CompensatedWorld w = p.getWorld();
			w.addTimingBlock(p.getPing(), change.type, change.pos.getX(), change.pos.getY(), change.pos.getZ());
		} else if(type.equals(PacketType.Server.MULTI_BLOCK_CHANGE)) {
			NPacketPlayOutMultiBlockChange change = (NPacketPlayOutMultiBlockChange) packet;
			CompensatedWorld w = p.getWorld();
			change.blockStates.forEach((pos, m) -> w.addTimingBlock(p.getPing(), m, pos.getX(), pos.getY(), pos.getZ()));
		} else if(type.equals(PacketType.Server.CUSTOM_PAYLOAD)) {
			NPacketPlayOutCustomPayload a = (NPacketPlayOutCustomPayload) packet;
			Adapter.getAdapter().debug("Channel: " + a.channel);
		}
	}
}
