package com.elikill58.negativity.common;

import java.util.ArrayList;
import java.util.Random;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.EventPriority;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.packets.Packet;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutSpawnPlayer;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.utils.Maths;

public class PacketListener implements Listeners {

	@EventListener
	public void onPacketReceive(PacketReceiveEvent e) {
		if(!e.hasPlayer() || e.getPacket().getPacketType() == null)
			return;
		Player p = e.getPlayer();
		Packet packet = e.getPacket();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		np.allPackets++;
		PacketType type = packet.getPacketType();
		if(type.isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) packet.getPacket();
			if(flying.hasLook || flying.hasPos) // if it's real flying
				np.packets.put(type, np.packets.getOrDefault(type, 0) + 1);
			if(flying.hasLook && np.shouldCheckSensitivity) {
				
				double deltaPitch = flying.pitch - np.doubles.get(CheatKeys.ALL, "sens-pitch", 0.0);
				double lastDeltaPitch = np.doubles.get(CheatKeys.ALL, "sens-delta-pitch", 0.0);

				float actualGcd = (float) Maths.getGcd(Math.abs(deltaPitch), Math.abs(lastDeltaPitch));
				double sensModifier = Math.cbrt(0.8333 * actualGcd);
				double tmpSens = ((1.666 * sensModifier) - 0.3333) * 200;
				np.doubles.set(CheatKeys.ALL, "sens-delta-pitch", deltaPitch);
				np.doubles.set(CheatKeys.ALL, "sens-pitch", (double) flying.pitch);	
				if(tmpSens > 0 && tmpSens < 200) {
					if((int) np.sensitivity == (int) tmpSens) {
						int nb = np.ints.get(CheatKeys.ALL, "sens-nb", 0);
						if(nb >= 4) {
							np.shouldCheckSensitivity = false;
							np.doubles.remove(CheatKeys.ALL, "sens-delta-pitch");
							np.doubles.remove(CheatKeys.ALL, "sens-pitch");
							np.doubles.remove(CheatKeys.ALL, "sens-nb");
						}
						np.ints.set(CheatKeys.ALL, "sens-nb", nb + 1);
					} else
						np.ints.set(CheatKeys.ALL, "sens-nb", 0);
					np.sensitivity = tmpSens;
				}
			}
			if(flying.hasPos) {
				World w = p.getWorld();
				Location oldLoc = p.getLocation();
				np.lastDelta = np.delta;
				if(flying.hasLook)
					np.delta = new Location(w, flying.x - oldLoc.getX(), flying.y - oldLoc.getY(), flying.z - oldLoc.getZ(), flying.yaw - oldLoc.getYaw(), flying.pitch - oldLoc.getPitch());
				else
					np.delta = new Location(w, flying.x - oldLoc.getX(), flying.y - oldLoc.getY(), flying.z - oldLoc.getZ(), np.lastDelta.getYaw(), np.lastDelta.getPitch()); // no yaw/pitch move
				np.lastLocations.add(flying.getLocation(p.getWorld()));
				if(np.lastLocations.size() >= 10)
					np.lastLocations.remove(0); // limit to 10 last loc

				Block blockUnder = w.getBlockAt(flying.x, flying.y - 1, flying.z);
				Block blockUp = w.getBlockAt(flying.x, flying.y + 1, flying.z);
				if(blockUnder.getType().getId().contains("ICE") || blockUp.getType().getId().contains("ICE")) {
					np.iceCounter = 5;
				} else if(flying.isGround && np.iceCounter > 0) {
					np.iceCounter--;
				}
				if(blockUp.getType().isSolid())
					np.blockAbove = 4;
				else if(np.blockAbove > 0)
					np.blockAbove--;
			}
			if(flying instanceof NPacketPlayInPositionLook)
				np.isTeleporting = false;
		} else
			np.packets.put(type, np.packets.getOrDefault(type, 0) + 1);
		if(type == PacketType.Client.BLOCK_DIG && packet.getPacket() instanceof NPacketPlayInBlockDig) {
			NPacketPlayInBlockDig blockDig = (NPacketPlayInBlockDig) packet.getPacket();
			if(blockDig.action != DigAction.FINISHED_DIGGING)
				return;
			
			Block b = blockDig.getBlock(p.getWorld());
			BlockBreakEvent event = new BlockBreakEvent(p, b);
			EventManager.callEvent(event);
			if(event.isCancelled())
				packet.setCancelled(event.isCancelled());
		}
		if (type == PacketType.Client.USE_ENTITY) {
			np.isAttacking = true;
			NPacketPlayInUseEntity useEntityPacket = (NPacketPlayInUseEntity) packet.getPacket();
			if(useEntityPacket.action.equals(EnumEntityUseAction.ATTACK)) {
				for(Entity entity : p.getWorld().getEntities()) {
					if(entity.isSameId(String.valueOf(useEntityPacket.entityId))) {
						PlayerDamageEntityEvent event = new PlayerDamageEntityEvent(p, entity, false);
						EventManager.callEvent(event);
						if(event.isCancelled())
							packet.setCancelled(event.isCancelled());
					}
				}
			}
		} else if (type == PacketType.Client.KEEP_ALIVE || type == PacketType.Client.POSITION) {
			np.isAttacking = false;
			p.applyTheoricVelocity();
		} else if (type == PacketType.Client.STEER_VEHICLE) {
			np.timeInvincibility = System.currentTimeMillis() + p.getPing() * 2;
		} else if (type == PacketType.Client.PONG) {
			NPacketPlayInPong pong = (NPacketPlayInPong) packet.getPacket();
			if(np.idWaitingAppliedVelocity != -1 && np.idWaitingAppliedVelocity == pong.id) {
				p.applyTheoricVelocity();
				np.idWaitingAppliedVelocity = -1;
			}
		}
		new ArrayList<>(np.getCheckProcessors()).forEach((cp) -> cp.handlePacketReceived(e));
	}

	@EventListener(priority = EventPriority.PRE)
	public void onJumpBoostUse(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		double amplifier = (p.hasPotionEffect(PotionEffectType.JUMP)
				? p.getPotionEffect(PotionEffectType.JUMP).get().getAmplifier()
				: 0);
		if (p.isOnGround() && amplifier == 0)
			np.isUsingJumpBoost = false;
		
	}

	@EventListener(priority = EventPriority.PRE)
	public void onJumpBoostUse(PacketSendEvent e) {
		if (!e.getPacket().getPacketType().equals(PacketType.Server.ENTITY_EFFECT))
			return;
		NPacketPlayOutEntityEffect packet = (NPacketPlayOutEntityEffect) e.getPacket().getPacket();
		if (packet.type.equals(PotionEffectType.JUMP))
			NegativityPlayer.getNegativityPlayer(e.getPlayer()).isUsingJumpBoost = true;
	}

	@EventListener
	public void onPacketSend(PacketSendEvent e) {
		if(!e.hasPlayer())
			return;
		Player p = e.getPlayer();
		PacketType type = e.getPacket().getPacketType();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if(type.equals(PacketType.Server.POSITION) || type.equals(PacketType.Server.ENTITY_TELEPORT))
			np.isTeleporting = true;
		else if(type.equals(PacketType.Server.ENTITY_VELOCITY)) {
			NPacketPlayOutEntityVelocity packet = (NPacketPlayOutEntityVelocity) e.getPacket().getPacket();
			if(!p.isSameId(String.valueOf(packet.entityId))) // not giving velocity to itself - not useful to send packet
				return;
			int randomNb = new Random().nextInt();
			if(randomNb == -1)
				randomNb = -26;
			p.queuePacket(new NPacketPlayOutPing(np.idWaitingAppliedVelocity = randomNb));
		} else if(type.equals(PacketType.Server.SPAWN_ENTITY)) {
			NPacketPlayOutSpawnEntity spawn = (NPacketPlayOutSpawnEntity) e.getPacket().getPacket();
			Adapter.getAdapter().debug(e.getPlayer().getName() + " will know entity " + spawn.type + ", id: " + spawn.entityId + " (" + spawn.x + "/" + spawn.y + "/" + spawn.z + ") is spawned.");
		} else if(type.equals(PacketType.Server.SPAWN_PLAYER)) {
			NPacketPlayOutSpawnPlayer spawn = (NPacketPlayOutSpawnPlayer) e.getPacket().getPacket();
			Adapter.getAdapter().debug(e.getPlayer().getName() + " will know player " + spawn.uuid + " (" + spawn.x + "/" + spawn.y + "/" + spawn.z + ") is spawned.");
		}
		new ArrayList<>(np.getCheckProcessors()).forEach((cp) -> cp.handlePacketSent(e));
	}
}
