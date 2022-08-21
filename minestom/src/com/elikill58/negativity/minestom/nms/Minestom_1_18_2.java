package com.elikill58.negativity.minestom.nms;

import org.jetbrains.annotations.NotNull;

import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInGround;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInHeldItemSlot;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSteerVehicle;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInTeleportAccept;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Adapter;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.network.packet.client.play.ClientChatMessagePacket;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;
import net.minestom.server.network.packet.client.play.ClientHeldItemChangePacket;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.client.play.ClientKeepAlivePacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket.Status;
import net.minestom.server.network.packet.client.play.ClientPlayerPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPongPacket;
import net.minestom.server.network.packet.client.play.ClientSteerVehiclePacket;
import net.minestom.server.network.packet.client.play.ClientTeleportConfirmPacket;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.BlockBreakAnimationPacket;
import net.minestom.server.network.packet.server.play.EntityEffectPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.network.packet.server.play.EntityTeleportPacket;
import net.minestom.server.network.packet.server.play.EntityVelocityPacket;
import net.minestom.server.network.packet.server.play.ExplosionPacket;
import net.minestom.server.network.packet.server.play.KeepAlivePacket;
import net.minestom.server.network.packet.server.play.PingPacket;
import net.minestom.server.network.packet.server.play.PlayerPositionAndLookPacket;
import net.minestom.server.potion.Potion;

public class Minestom_1_18_2 extends MinestomVersionAdapter {

	public Minestom_1_18_2() {
		super("v1_18_2");
		packetsPlayIn.put(getNameOfPacket(ClientPlayerDiggingPacket.class), (p, packet) -> {
			ClientPlayerDiggingPacket blockDig = (ClientPlayerDiggingPacket) packet;
			Point pos = blockDig.blockPosition();
			return new NPacketPlayInBlockDig(pos.blockX(), pos.blockY(), pos.blockZ(),
					translateDigAction(blockDig.status()), translateFacing(blockDig.blockFace()));
		});

		packetsPlayIn.put(getNameOfPacket(ClientChatMessagePacket.class),
				(p, packet) -> new NPacketPlayInChat(((ClientChatMessagePacket) packet).message()));

		packetsPlayIn.put(getNameOfPacket(ClientPlayerPositionAndRotationPacket.class), (p, f) -> {
			ClientPlayerPositionAndRotationPacket packet = (ClientPlayerPositionAndRotationPacket) f;
			return new NPacketPlayInPositionLook(packet.position().x(), packet.position().y(), packet.position().z(),
					packet.position().yaw(), packet.position().pitch(), packet.onGround());
		});
		packetsPlayIn.put(getNameOfPacket(ClientPlayerPositionPacket.class), (p, f) -> {
			ClientPlayerPositionPacket packet = (ClientPlayerPositionPacket) f;
			return new NPacketPlayInPosition(packet.position().x(), packet.position().y(), packet.position().z(), 0, 0,
					packet.onGround());
		});
		packetsPlayIn.put(getNameOfPacket(ClientPlayerRotationPacket.class), (p, f) -> {
			ClientPlayerRotationPacket packet = (ClientPlayerRotationPacket) f;
			return new NPacketPlayInLook(0, 0, 0, packet.yaw(), packet.pitch(), packet.onGround());
		});
		packetsPlayIn.put(getNameOfPacket(ClientPlayerPacket.class), (p, f) -> {
			ClientPlayerPacket packet = (ClientPlayerPacket) f;
			return new NPacketPlayInGround(packet.onGround());
		});
		packetsPlayIn.put(getNameOfPacket(ClientKeepAlivePacket.class),
				(p, f) -> new NPacketPlayInKeepAlive(((ClientKeepAlivePacket) f).id()));
		packetsPlayIn.put(getNameOfPacket(ClientInteractEntityPacket.class), (p, f) -> {
			ClientInteractEntityPacket packet = (ClientInteractEntityPacket) f;
			EnumEntityUseAction action = null;
			if (packet.type() instanceof ClientInteractEntityPacket.Attack)
				action = EnumEntityUseAction.ATTACK;
			else if (packet.type() instanceof ClientInteractEntityPacket.Interact)
				action = EnumEntityUseAction.INTERACT;
			else if (packet.type() instanceof ClientInteractEntityPacket.InteractAt)
				action = EnumEntityUseAction.INTERACT_AT;
			return new NPacketPlayInUseEntity(packet.targetId(), new Vector(0, 0, 0), action);
		});
		packetsPlayIn.put(getNameOfPacket(ClientEntityActionPacket.class), (p, f) -> {
			ClientEntityActionPacket packet = (ClientEntityActionPacket) f;
			return new NPacketPlayInEntityAction(packet.playerId(), translatePlayerAction(packet.action()),
					packet.horseJumpBoost());
		});
		packetsPlayIn.put(getNameOfPacket(ClientPongPacket.class),
				(p, f) -> new NPacketPlayInPong(((ClientPongPacket) f).id()));
		packetsPlayIn.put(getNameOfPacket(ClientHeldItemChangePacket.class),
				(p, f) -> new NPacketPlayInHeldItemSlot(((ClientHeldItemChangePacket) f).slot()));
		packetsPlayIn.put(getNameOfPacket(ClientSteerVehiclePacket.class), (p, f) -> {
			ClientSteerVehiclePacket packet = (ClientSteerVehiclePacket) f;
			return new NPacketPlayInSteerVehicle(packet.sideways(), packet.forward(), false, false);
		});
		packetsPlayIn.put(getNameOfPacket(ClientTeleportConfirmPacket.class),
				(p, f) -> new NPacketPlayInTeleportAccept(((ClientTeleportConfirmPacket) f).teleportId()));

		packetsPlayOut.put(getNameOfPacket(BlockBreakAnimationPacket.class), (p, f) -> {
			BlockBreakAnimationPacket packet = (BlockBreakAnimationPacket) f;
			Point pos = packet.blockPosition();
			return new NPacketPlayOutBlockBreakAnimation(pos.blockX(), pos.blockY(), pos.blockZ(), packet.entityId(),
					packet.destroyStage());
		});
		packetsPlayOut.put(getNameOfPacket(KeepAlivePacket.class),
				(p, f) -> new NPacketPlayOutKeepAlive(((KeepAlivePacket) f).id()));

		packetsPlayOut.put(getNameOfPacket(EntityTeleportPacket.class), (p, f) -> {
			EntityTeleportPacket packet = (EntityTeleportPacket) f;
			return new NPacketPlayOutEntityTeleport(packet.entityId(), packet.position().x(), packet.position().y(),
					packet.position().z(), packet.position().yaw(), packet.position().pitch(), packet.onGround());
		});

		packetsPlayOut.put(getNameOfPacket(EntityVelocityPacket.class), (p, f) -> {
			EntityVelocityPacket packet = (EntityVelocityPacket) f;
			return new NPacketPlayOutEntityVelocity(packet.entityId(), packet.velocityX(), packet.velocityY(),
					packet.velocityZ());
		});
		packetsPlayOut.put(getNameOfPacket(PlayerPositionAndLookPacket.class), (p, f) -> {
			PlayerPositionAndLookPacket packet = (PlayerPositionAndLookPacket) f;
			Pos pos = packet.position();
			return new NPacketPlayOutPosition(pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch());
		});
		packetsPlayOut.put(getNameOfPacket(ExplosionPacket.class), (p, f) -> {
			ExplosionPacket packet = (ExplosionPacket) f;
			return new NPacketPlayOutExplosion(packet.x(), packet.y(), packet.z(), packet.playerMotionX(),
					packet.playerMotionY(), packet.playerMotionZ());
		});
		packetsPlayOut.put(getNameOfPacket(EntityPositionPacket.class), (p, f) -> {
			EntityPositionPacket packet = (EntityPositionPacket) f; // TODO check if it's good one
			return new NPacketPlayOutEntity(packet.entityId(), packet.deltaX(), packet.deltaY(), packet.deltaZ());
		});
		packetsPlayOut.put(getNameOfPacket(EntityEffectPacket.class), (p, f) -> {
			EntityEffectPacket packet = (EntityEffectPacket) f;
			Potion po = packet.potion();
			return new NPacketPlayOutEntityEffect(packet.entityId(),
					PotionEffectType.fromName(po.effect().key().asString()), po.amplifier(), po.duration(), po.flags());
		});
		packetsPlayOut.put(getNameOfPacket(PingPacket.class), (p, f) -> new NPacketPlayOutPing(((PingPacket) f).id()));

		negativityToPlatform.put(PacketType.Server.PING, (p, f) -> new PingPacket((int) ((NPacketPlayOutPing) f).id));

		Adapter.getAdapter().debug("Packet PlayIn: " + String.join(", ", packetsPlayIn.keySet()));
		log();
	}

	private static DigAction translateDigAction(Status action) {
		switch (action) {
		case STARTED_DIGGING:
			return DigAction.START_DIGGING;
		case CANCELLED_DIGGING:
			return DigAction.CANCEL_DIGGING;
		case FINISHED_DIGGING:
			return DigAction.FINISHED_DIGGING;
		case DROP_ITEM_STACK:
			return DigAction.DROP_ITEM_STACK;
		case DROP_ITEM:
			return DigAction.DROP_ITEM;
		case UPDATE_ITEM_STATE:
			return DigAction.FINISH_ACTION;
		case SWAP_ITEM_HAND:
			return DigAction.SWAP_ITEM;
		}
		throw new IllegalStateException("Unexpected Status constant: " + action.name());
	}

	private static DigFace translateFacing(@NotNull BlockFace blockFace) {
		switch (blockFace) {
		case BOTTOM:
			return DigFace.BOTTOM;
		case TOP:
			return DigFace.TOP;
		case NORTH:
			return DigFace.NORTH;
		case SOUTH:
			return DigFace.SOUTH;
		case WEST:
			return DigFace.WEST;
		case EAST:
			return DigFace.EAST;
		}
		throw new IllegalStateException("Unexpected BlockFace constant: " + blockFace.name());
	}

	private static EnumPlayerAction translatePlayerAction(@NotNull ClientEntityActionPacket.Action action) {
		switch (action) {
		case LEAVE_BED:
			return EnumPlayerAction.LEAVE_BED;
		case OPEN_HORSE_INVENTORY:
			return EnumPlayerAction.OPEN_INVENTORY;
		case START_FLYING_ELYTRA:
			return EnumPlayerAction.START_FALL_FLYING;
		case START_JUMP_HORSE:
			return EnumPlayerAction.START_RIDING_JUMP;
		case START_SNEAKING:
			return EnumPlayerAction.START_SNEAKING;
		case START_SPRINTING:
			return EnumPlayerAction.START_SPRINTING;
		case STOP_JUMP_HORSE:
			return EnumPlayerAction.STOP_RIDING_JUMP;
		case STOP_SNEAKING:
			return EnumPlayerAction.STOP_SNEAKING;
		case STOP_SPRINTING:
			return EnumPlayerAction.STOP_SPRINTING;
		}
		throw new IllegalStateException("Unexpected ClientEntityActionPacket.Action constant: " + action.name());
	}

	private String getNameOfPacket(Class<?> c) {
		String packetClassName = c.getName();
		return packetClassName.substring(packetClassName.lastIndexOf('.') + 1);
	}

	@Override
	public String getNameOfPacket(Object nms) {
		return getNameOfPacket(nms.getClass());
	}

	@Override
	public void sendPacket(Player p, Object basicPacket) {
		p.sendPacket((@NotNull SendablePacket) basicPacket);
	}

	@Override
	public void queuePacket(Player p, Object basicPacket) {
		sendPacket(p, basicPacket); // can't queued
	}
}
