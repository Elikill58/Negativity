package com.elikill58.negativity.sponge.nms;

import java.lang.reflect.Field;

import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.sponge.SpongeNegativity;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Sponge_1_12_2 extends SpongeVersionAdapter {

	public Sponge_1_12_2() {
		super("v1_12_2");
		packetsPlayIn.put("CPacketPlayerDigging", (packet) -> {
			CPacketPlayerDigging blockDig = (CPacketPlayerDigging) packet;
			BlockPos pos = blockDig.getPosition();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(), translateDigAction(blockDig.getAction()), translateFacing(blockDig.getFacing()));
		});

		packetsPlayIn.put("CPacketChatMessage", (packet) -> new NPacketPlayInChat(((CPacketChatMessage) packet).getMessage()));

		packetsPlayIn.put("CPacketPlayer$PositionRotation", (f) -> {
			CPacketPlayer packet = (CPacketPlayer) f;
			return new NPacketPlayInPositionLook(packet.getX(0), packet.getY(0), packet.getZ(0), packet.getYaw(0), packet.getPitch(0), packet.isOnGround());
		});
		packetsPlayIn.put("CPacketPlayer$Position", (f) -> {
			CPacketPlayer packet = (CPacketPlayer) f;
			return new NPacketPlayInPosition(packet.getX(0), packet.getY(0), packet.getZ(0), packet.getYaw(0), packet.getPitch(0), packet.isOnGround());
		});
		packetsPlayIn.put("CPacketPlayer$Rotation", (f) -> {
			CPacketPlayer packet = (CPacketPlayer) f;
			return new NPacketPlayInLook(packet.getX(0), packet.getY(0), packet.getZ(0), packet.getYaw(0), packet.getPitch(0), packet.isOnGround());
		});
		packetsPlayIn.put("CPacketPlayer", (f) -> {
			CPacketPlayer packet = (CPacketPlayer) f;
			float yaw = packet.getYaw(0);
			float pitch = packet.getPitch(0);
			return new NPacketPlayInFlying(packet.getX(0), packet.getY(0), packet.getZ(0), yaw, pitch, packet.isOnGround(), yaw == packet.getYaw(Float.MAX_VALUE), pitch == packet.getPitch(Float.MAX_VALUE));
		});
		packetsPlayIn.put("CPacketKeepAlive", (f) -> new NPacketPlayInKeepAlive(((CPacketKeepAlive) f).getKey()));
		packetsPlayIn.put("CPacketUseEntity", (f) -> {
			CPacketUseEntity p = (CPacketUseEntity) f;
			Vec3d v = p.getHitVec();
			return new NPacketPlayInUseEntity(0, v == null ? new Vector(0, 0, 0) : new Vector(v.x, v.y, v.z), EnumEntityUseAction.valueOf(p.getAction().name()));
		});
		packetsPlayIn.put("CPacketEntityAction", (f) -> {
			try {
				CPacketEntityAction packet = (CPacketEntityAction) f;
				Field entityIdField = f.getClass().getDeclaredField("entityID");
				entityIdField.setAccessible(true);
				return new NPacketPlayInEntityAction(entityIdField.getInt(f),
						EnumPlayerAction.getAction(packet.getAction().name()), packet.getAuxData());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		

		packetsPlayOut.put("SPacketBlockBreakAnim", (f) -> {
			SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) f;
			BlockPos pos = packet.position;
			return new NPacketPlayOutBlockBreakAnimation(pos.getX(), pos.getY(), pos.getZ(), packet.breakerId, packet.progress);
		});
		packetsPlayOut.put("SPacketKeepAlive", (f) -> new NPacketPlayOutKeepAlive(((SPacketKeepAlive) f).id));
		packetsPlayOut.put("SPacketEntityTeleport", (f) -> {
			SPacketEntityTeleport packet = (SPacketEntityTeleport) f;
			return new NPacketPlayOutEntityTeleport(packet.entityId, packet.posX, packet.posY, packet.posZ, packet.yaw, packet.pitch, packet.onGround);
		});
		packetsPlayOut.put("SPacketEntityVelocity", (f) -> {
			SPacketEntityVelocity packet = (SPacketEntityVelocity) f;
			return new NPacketPlayOutEntityVelocity(packet.entityID, packet.motionX, packet.motionY, packet.motionZ);
		});
		packetsPlayOut.put("SPacketPlayerPosLook", (f) -> {
			SPacketPlayerPosLook packet = (SPacketPlayerPosLook) f;
			return new NPacketPlayOutPosition(packet.x, packet.y, packet.z, packet.yaw, packet.pitch);
		});
		packetsPlayOut.put("SPacketExplosion", (f) -> {
			SPacketExplosion packet = (SPacketExplosion) f;
			return new NPacketPlayOutExplosion(packet.posX, packet.posY, packet.posZ, packet.motionX, packet.motionY, packet.motionZ);
		});
		packetsPlayOut.put("SPacketEntity", (f) -> {
			SPacketEntity packet = (SPacketEntity) f;
			return new NPacketPlayOutEntity(packet.entityId, packet.posX, packet.posY, packet.posZ);
		});
		packetsPlayOut.put("SPacketEntityEffect", (f) -> {
			SPacketEntityEffect packet = (SPacketEntityEffect) f;
			return new NPacketPlayOutEntityEffect(packet.getEntityId(), packet.getEffectId(), packet.getAmplifier(), packet.getDuration(), (byte) 0);
		});
		
		SpongeNegativity.getInstance().getLogger().info("[Packets-" + version + "] Loaded " + packetsPlayIn.size() + " PlayIn and " + packetsPlayOut.size() + " PlayOut.");
	}
	
	private static DigAction translateDigAction(CPacketPlayerDigging.Action action) {
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
		case SWAP_HELD_ITEMS:
			return DigAction.SWAP_ITEM;
		}
		throw new IllegalStateException("Unexpected CPacketPlayerDigging.Action constant: " + action.name());
	}
	
	private static DigFace translateFacing(EnumFacing facing) {
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
}
