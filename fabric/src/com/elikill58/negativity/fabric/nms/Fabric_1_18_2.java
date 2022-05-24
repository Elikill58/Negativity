package com.elikill58.negativity.fabric.nms;

import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInHeldItemSlot;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayPingS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Fabric_1_18_2 extends FabricVersionAdapter {

	public Fabric_1_18_2() {
		super("v1_12_2");
		packetsPlayIn.put(PlayerActionC2SPacket.class.getSimpleName(), (p, packet) -> {
			PlayerActionC2SPacket blockDig = (PlayerActionC2SPacket) packet;
			BlockPos pos = blockDig.getPos();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(),
					translateDigAction(blockDig.getAction()), translateFacing(blockDig.getDirection()));
		});

		packetsPlayIn.put(ChatMessageC2SPacket.class.getSimpleName(),
				(p, packet) -> new NPacketPlayInChat(((ChatMessageC2SPacket) packet).getChatMessage()));

		packetsPlayIn.put(PlayerMoveC2SPacket.Full.class.getSimpleName(), (p, f) -> {
			PlayerMoveC2SPacket.Full packet = (PlayerMoveC2SPacket.Full) f;
			return new NPacketPlayInPositionLook(packet.getX(0), packet.getY(0), packet.getZ(0), packet.getYaw(0),
					packet.getPitch(0), packet.isOnGround());
		});
		packetsPlayIn.put(PlayerMoveC2SPacket.PositionAndOnGround.class.getSimpleName(), (p, f) -> {
			PlayerMoveC2SPacket.PositionAndOnGround packet = (PlayerMoveC2SPacket.PositionAndOnGround) f;
			return new NPacketPlayInPosition(packet.getX(0), packet.getY(0), packet.getZ(0), packet.getYaw(0),
					packet.getPitch(0), packet.isOnGround());
		});
		packetsPlayIn.put(PlayerMoveC2SPacket.LookAndOnGround.class.getSimpleName(), (p, f) -> {
			PlayerMoveC2SPacket.LookAndOnGround packet = (PlayerMoveC2SPacket.LookAndOnGround) f;
			return new NPacketPlayInLook(packet.getX(0), packet.getY(0), packet.getZ(0), packet.getYaw(0),
					packet.getPitch(0), packet.isOnGround());
		});
		packetsPlayIn.put(PlayerMoveC2SPacket.OnGroundOnly.class.getSimpleName(), (p, f) -> {
			PlayerMoveC2SPacket.OnGroundOnly packet = (PlayerMoveC2SPacket.OnGroundOnly) f;
			float yaw = packet.getYaw(0);
			float pitch = packet.getPitch(0);
			return new NPacketPlayInFlying(packet.getX(0), packet.getY(0), packet.getZ(0), yaw, pitch,
					packet.isOnGround(), yaw == packet.getYaw(Float.MAX_VALUE),
					pitch == packet.getPitch(Float.MAX_VALUE));
		});
		packetsPlayIn.put(KeepAliveC2SPacket.class.getSimpleName(), (p, f) -> new NPacketPlayInKeepAlive(((KeepAliveC2SPacket) f).getId()));
		packetsPlayIn.put(PlayerInteractEntityC2SPacket.class.getSimpleName(), (pa, f) -> {
			PlayerInteractEntityC2SPacket p = (PlayerInteractEntityC2SPacket) f;
			return new NPacketPlayInUseEntity(p.getEntity(pa.getWorld()).getId(), new Vector(0, 0, 0),
					EnumEntityUseAction.valueOf(getStr(f, "type")));
		});
		packetsPlayIn.put(ClientCommandC2SPacket.class.getSimpleName(), (p, f) -> {
			ClientCommandC2SPacket packet = (ClientCommandC2SPacket) f;
			return new NPacketPlayInEntityAction(packet.getEntityId(),
					EnumPlayerAction.getAction(packet.getMode().name()), packet.getMountJumpHeight());
		});
		packetsPlayIn.put(QueryPingC2SPacket.class.getSimpleName(),
				(p, f) -> new NPacketPlayInPong(((QueryPingC2SPacket) f).getStartTime()));
		packetsPlayIn.put(PickFromInventoryC2SPacket.class.getSimpleName(),
				(p, f) -> new NPacketPlayInHeldItemSlot(((PickFromInventoryC2SPacket) f).getSlot()));

		packetsPlayOut.put(BlockBreakingProgressS2CPacket.class.getSimpleName(), (p, f) -> {
			BlockBreakingProgressS2CPacket packet = (BlockBreakingProgressS2CPacket) f;
			BlockPos pos = packet.getPos();
			return new NPacketPlayOutBlockBreakAnimation(pos.getX(), pos.getY(), pos.getZ(), packet.getEntityId(),
					packet.getProgress());
		});
		packetsPlayOut.put(KeepAliveS2CPacket.class.getSimpleName(), (p, f) -> new NPacketPlayOutKeepAlive(((KeepAliveS2CPacket) f).getId()));
		/*packetsPlayOut.put("SPacketEntityTeleport", (p, f) -> {
			SPacketEntityTeleport packet = (SPacketEntityTeleport) f;
			return new NPacketPlayOutEntityTeleport(packet.entityId, packet.posX, packet.posY, packet.posZ, packet.yaw,
					packet.pitch, packet.onGround);
		});*/ // TODO manage entity teleport packet
		packetsPlayOut.put(EntityVelocityUpdateS2CPacket.class.getSimpleName(), (p, f) -> {
			EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) f;
			return new NPacketPlayOutEntityVelocity(packet.getId(), packet.getVelocityX(), packet.getVelocityY(),
					packet.getVelocityZ());
		});
		packetsPlayOut.put(PlayerPositionLookS2CPacket.class.getSimpleName(), (p, f) -> {
			PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) f;
			return new NPacketPlayOutPosition(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
		});
		packetsPlayOut.put(ExplosionS2CPacket.class.getSimpleName(), (p, f) -> {
			ExplosionS2CPacket packet = (ExplosionS2CPacket) f;
			return new NPacketPlayOutExplosion(packet.getX(), packet.getY(), packet.getZ(), packet.getPlayerVelocityX(),
					packet.getPlayerVelocityY(), packet.getPlayerVelocityZ());
		});
		packetsPlayOut.put(EntityPositionS2CPacket.class.getSimpleName(), (p, f) -> {
			EntityPositionS2CPacket packet = (EntityPositionS2CPacket) f;
			return new NPacketPlayOutEntity(packet.getId(), packet.getX(), packet.getY(), packet.getZ());
		});
		packetsPlayOut.put(EntityStatusEffectS2CPacket.class.getSimpleName(), (p, f) -> {
			EntityStatusEffectS2CPacket packet = (EntityStatusEffectS2CPacket) f;
			return new NPacketPlayOutEntityEffect(packet.getEntityId(), packet.getEffectId(), packet.getAmplifier(),
					packet.getDuration(), (byte) 0);
		});
		packetsPlayOut.put(PlayPingS2CPacket.class.getSimpleName(),
				(p, f) -> new NPacketPlayOutPing(((PlayPingS2CPacket) f).getParameter()));

		negativityToPlatform.put(PacketType.Server.PING, (p, f) -> new QueryPingC2SPacket(((NPacketPlayOutPing) f).id));

		log();
	}

	private static DigAction translateDigAction(PlayerActionC2SPacket.Action action) {
		switch (action) {
		case START_DESTROY_BLOCK:
			return DigAction.START_DIGGING;
		case ABORT_DESTROY_BLOCK:
			return DigAction.CANCEL_DIGGING;
		case STOP_DESTROY_BLOCK:
			return DigAction.FINISHED_DIGGING;
		case DROP_ALL_ITEMS:
			return DigAction.DROP_ITEM_STACK;
		case DROP_ITEM:
			return DigAction.DROP_ITEM;
		case RELEASE_USE_ITEM:
			return DigAction.FINISH_ACTION;
		case SWAP_ITEM_WITH_OFFHAND:
			return DigAction.SWAP_ITEM;
		}
		throw new IllegalStateException("Unexpected CPacketPlayerDigging.Action constant: " + action.name());
	}

	private static DigFace translateFacing(Direction facing) {
		switch (facing) {
		case DOWN:
			return DigFace.BOTTOM;
		case UP:
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
		throw new IllegalStateException("Unexpected EnumFacing constant: " + facing.name());
	}

	@Override
	public void sendPacket(ServerPlayerEntity p, Object basicPacket) {
		ServerPlayNetworking.getSender(p).sendPacket((Packet<?>) basicPacket);
	}

	@Override
	public void queuePacket(ServerPlayerEntity p, Object basicPacket) {
		ServerPlayNetworking.getSender(p).sendPacket((Packet<?>) basicPacket);
	}
}