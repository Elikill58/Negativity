package com.elikill58.negativity.common;

import java.util.ArrayList;
import java.util.Random;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.utils.Maths;

public class PacketListener implements Listeners {

	@EventListener
	public void onPacketReceive(PacketReceiveEvent e) {
		if(!e.hasPlayer() || e.getPacket().getPacketType() == null)
			return;
		Player p = e.getPlayer();
		AbstractPacket packet = e.getPacket();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		np.allPackets++;
		PacketType type = packet.getPacketType();
		if(type.isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) packet.getPacket();
			if(flying.hasLook || flying.hasPos) // if it's real flying
				np.packets.put(type, np.packets.getOrDefault(type, 0) + 1);
			if(flying.hasLook && np.shouldCheckSensitivity) {
				
				final double deltaPitch = flying.pitch - np.doubles.get(CheatKeys.ALL, "sens-pitch", 0.0);
				final double lastDeltaPitch = np.doubles.get(CheatKeys.ALL, "sens-delta-pitch", 0.0);

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
				np.lastLocations.add(flying.getLocation(p.getWorld()));
				if(np.lastLocations.size() >= 10)
					np.lastLocations.remove(0); // limit to 10 last loc
			}
			if(flying instanceof NPacketPlayInPositionLook)
				np.isTeleporting = false;
		} else
			np.packets.put(type, np.packets.getOrDefault(type, 0) + 1);
		if(type == PacketType.Client.BLOCK_DIG && !Version.getVersion().equals(Version.V1_7) && packet.getPacket() instanceof NPacketPlayInBlockDig) {
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
		}
		new ArrayList<>(np.getCheckProcessors()).forEach((cp) -> cp.handlePacketSent(e));
	}
}
