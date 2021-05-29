package com.elikill58.negativity.sponge.nms;

import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigFace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.sponge.SpongeNegativity;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class Sponge_1_12_R1 extends SpongeVersionAdapter {

	public Sponge_1_12_R1() {
		super("v1_12_R1");
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
		

		packetsPlayOut.put("SPacketBlockBreakAnim", (f) -> {
			SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) f;
			BlockPos pos = packet.getPosition();
			return new NPacketPlayOutBlockBreakAnimation(pos.getX(), pos.getY(), pos.getZ(), packet.getBreakerId(), packet.getProgress());
		});
		packetsPlayOut.put("SPacketKeepAlive", (f) -> new NPacketPlayOutKeepAlive(((SPacketKeepAlive) f).id));
		
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
