package com.elikill58.negativity.sponge9.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

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
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.universal.Adapter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unchecked")
public class Sponge_1_16_5 extends SpongeVersionAdapter {

	public Sponge_1_16_5() {
		super("v1_16_5");
		packetsPlayIn.put("ServerboundPlayerActionPacket", (p, f) -> {
			ServerboundPlayerActionPacket packet = (ServerboundPlayerActionPacket) f;
			BlockPos pos = packet.getPos();
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(),
					translateDigAction(packet.getAction()), translateDigDirection(packet.getDirection()));
		});

		packetsPlayIn.put("ServerboundChatPacket", (p, packet) -> new NPacketPlayInChat(((ServerboundChatPacket) packet).getMessage()));

		packetsPlayIn.put("ServerboundMovePlayerPacket$PosRot", (p, f) -> {
			Class<?> sup = ServerboundMovePlayerPacket.class;
			return new NPacketPlayInPositionLook(get(f, sup, "x"), get(f, sup, "y"), get(f, sup, "z"),
					get(f, sup, "yRot"), get(f, sup, "xRot"), get(f, sup, "onGround"));
		});
		packetsPlayIn.put("ServerboundMovePlayerPacket$Pos", (p, f) -> {
			Class<?> sup = ServerboundMovePlayerPacket.class;
			return new NPacketPlayInPosition(get(f, sup, "x"), get(f, sup, "y"), get(f, sup, "z"),
					get(f, sup, "yRot"), get(f, sup, "xRot"), get(f, sup, "onGround"));
		});
		packetsPlayIn.put("ServerboundMovePlayerPacket$Rot", (p, f) -> {
			Class<?> sup = ServerboundMovePlayerPacket.class;
			return new NPacketPlayInLook(get(f, sup, "x"), get(f, sup, "y"), get(f, sup, "z"),
					get(f, sup, "yRot"), get(f, sup, "xRot"), get(f, sup, "onGround"));
		});
		packetsPlayIn.put("ServerboundMovePlayerPacket", (p, f) -> {
			return new NPacketPlayInFlying(get(f, "x"), get(f, "y"), get(f, "z"),
					get(f, "yRot"), get(f, "xRot"), get(f,  "onGround"), get(f, "hasPos"), get(f, "hasRot"));
		});
		packetsPlayIn.put("ServerboundKeepAlivePacket", (p, f) -> new NPacketPlayInKeepAlive(((ServerboundKeepAlivePacket) f).getId()));
		packetsPlayIn.put("ServerboundInteractPacket", (pl, f) -> {
			ServerboundInteractPacket p = (ServerboundInteractPacket) f;
			Vec3 v = p.getLocation();
			return new NPacketPlayInUseEntity(0, v == null ? new Vector(0, 0, 0) : new Vector(v.x, v.y, v.z),
					EnumEntityUseAction.valueOf(p.getAction().name()));
		});
		packetsPlayIn.put("ServerboundPlayerCommandPacket", (p, f) -> {
			try {
				ServerboundPlayerCommandPacket packet = (ServerboundPlayerCommandPacket) f;
				Field entityIdField = f.getClass().getDeclaredField("id");
				entityIdField.setAccessible(true);
				return new NPacketPlayInEntityAction(entityIdField.getInt(f),
						EnumPlayerAction.getAction(packet.getAction().name()), packet.getData());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("ServerboundPingRequestPacket", (p, f) -> new NPacketPlayInPong(((ServerboundPingRequestPacket) f).getTime()));
		packetsPlayIn.put("ServerboundPickItemPacket", (p, f) -> new NPacketPlayInHeldItemSlot(((ServerboundPickItemPacket) f).getSlot())); // TODO check if it's good packet

		
		
		packetsPlayOut.put("ClientboundBlockBreakAckPacket", (p, f) -> {
			BlockPos pos = get(f, "pos");
			BlockState state = get(f, "state");
			return new NPacketPlayOutBlockBreakAnimation(pos.getX(), pos.getY(), pos.getZ(), 0, state.hashCode());
		});
		packetsPlayOut.put("ClientboundKeepAlivePacket", (p, f) -> new NPacketPlayOutKeepAlive(get(f, "id")));
		packetsPlayOut.put("ClientboundTeleportEntityPacket", (p, f) -> 
			new NPacketPlayOutEntityTeleport(get(f, "id"), get(f, "x"), get(f, "y"), get(f, "z"), (float) (byte) get(f, "yRot"), (float) (byte) get(f, "xRot"), get(f, "onGround")));
		packetsPlayOut.put("ClientboundMoveEntityPacket", (p, f) -> {
			return new NPacketPlayOutPosition(get(f, "xa"), get(f, "ya"), get(f, "za"), (float) (byte) get(f, "yRot"), (float) (byte) get(f, "xRot"));
		});
		packetsPlayOut.put("ClientboundExplodePacket", (p, f) -> {
			return new NPacketPlayOutExplosion(get(f, "x"), get(f, "y"), get(f, "z"), get(f, "knockbackX"), get(f, "knockbackY"), get(f, "knockbackZ"));
		});
		packetsPlayOut.put("ClientboundPlayerPositionPacket", (p, f) ->  new NPacketPlayOutEntity(get(f, "id"), get(f, "x"), get(f, "y"), get(f, "z")));
		packetsPlayOut.put("ClientboundSetEntityMotionPacket", (p, f) -> {
			return new NPacketPlayOutEntityVelocity(get(f, "id"), get(f, "xa"), get(f, "ya"), get(f, "za"));
		});
		packetsPlayOut.put("ClientboundUpdateMobEffectPacket", (p, f) -> {
			return new NPacketPlayOutEntityEffect(get(f, "entityId"), get(f, "effectId"), get(f, "effectAmplifier"), get(f, "effectDurationTicks"), get(f, "flags"));
		});
		packetsPlayOut.put("ClientboundPongResponsePacket", (p, f) -> new NPacketPlayOutPing(get(f, "time")));

		negativityToPlatform.put(PacketType.Server.PING, (p, f) -> new ClientboundPongResponsePacket(((NPacketPlayOutPing) f).id));

		log();
	}

	private DigFace translateDigDirection(Direction direction) {
		switch (direction) {
		case DOWN:
			return DigFace.BOTTOM;
		case EAST:
			return DigFace.EAST;
		case NORTH:
			return DigFace.NORTH;
		case SOUTH:
			return DigFace.SOUTH;
		case UP:
			return DigFace.TOP;
		case WEST:
			return DigFace.WEST;
		}
		return null;
	}

	private static DigAction translateDigAction(ServerboundPlayerActionPacket.Action action) {
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

	@Override
	protected <T> T get(Object obj, Class<?> clazz, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find field " + name + " in class " + obj.getClass().getSimpleName() + " for class " + clazz.getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected <T> T get(Object obj, String name) {
		return get(obj.getClass(), obj, name);
	}

	@Override
	protected <T> T get(Class<?> clazz, Object obj, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find field " + name + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected Object getSafe(Object obj, String name) {
		try {
			Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			return f.get(obj);
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find safe field " + name + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected String getStr(Object obj, String name) {
		try {
			Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			return f.get(obj).toString();
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find str field " + name + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected <T> T getFromMethod(Object obj, String methodName) {
		return getFromMethod(obj.getClass(), obj, methodName);
	}

	@Override
	protected <T> T getFromMethod(Class<?> clazz, Object obj, String methodName) {
		try {
			Method f = clazz.getDeclaredMethod(methodName);
			f.setAccessible(true);
			return (T) f.invoke(obj);
		} catch (NoSuchMethodException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find method " + methodName + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void sendPacket(ServerPlayer p, Object basicPacket) {
		((net.minecraft.server.level.ServerPlayer) p).connection.send((Packet<?>) basicPacket);
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void queuePacket(ServerPlayer p, Object basicPacket) {
		try {
			Object packetQueued = callFirstConstructor(Connection.class.getDeclaredClasses()[0], basicPacket, null);
			
			((Queue) get(((net.minecraft.server.level.ServerPlayer) p).connection, "queue")).add(packetQueued);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
