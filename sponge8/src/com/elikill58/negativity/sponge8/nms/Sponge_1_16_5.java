package com.elikill58.negativity.sponge8.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
import com.elikill58.negativity.sponge8.SpongeNegativity;
import com.elikill58.negativity.universal.Adapter;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;

@SuppressWarnings("unchecked")
public class Sponge_1_16_5 extends SpongeVersionAdapter {

	public Sponge_1_16_5() {
		super("v1_16_5");
		packetsPlayIn.put("ClientboundBlockBreakAckPacket", (packet) -> {
			BlockPos pos = get(packet, "pos");
			return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(),
					translateDigAction(get(packet, "action")), DigFace.BOTTOM);
		});

		packetsPlayIn.put("ServerboundChatPacket", (packet) -> new NPacketPlayInChat(((ServerboundChatPacket) packet).getMessage()));

		packetsPlayIn.put("ServerboundMovePlayerPacket$PosRot", (f) -> {
			Class<?> sup = ServerboundMovePlayerPacket.class;
			return new NPacketPlayInPositionLook(get(f, sup, "x"), get(f, sup, "y"), get(f, sup, "z"),
					get(f, sup, "yRot"), get(f, sup, "xRot"), get(f, sup, "onGround"));
		});
		packetsPlayIn.put("ServerboundMovePlayerPacket$Pos", (f) -> {
			Class<?> sup = ServerboundMovePlayerPacket.class;
			return new NPacketPlayInPosition(get(f, sup, "x"), get(f, sup, "y"), get(f, sup, "z"),
					get(f, sup, "yRot"), get(f, sup, "xRot"), get(f, sup, "onGround"));
		});
		packetsPlayIn.put("ServerboundMovePlayerPacket$Rot", (f) -> {
			Class<?> sup = ServerboundMovePlayerPacket.class;
			return new NPacketPlayInLook(get(f, sup, "x"), get(f, sup, "y"), get(f, sup, "z"),
					get(f, sup, "yRot"), get(f, sup, "xRot"), get(f, sup, "onGround"));
		});
		packetsPlayIn.put("ServerboundMovePlayerPacket", (f) -> {
			return new NPacketPlayInFlying(get(f, "x"), get(f, "y"), get(f, "z"),
					get(f, "yRot"), get(f, "xRot"), get(f,  "onGround"), get(f, "hasPos"), get(f, "hasRot"));
		});
		packetsPlayIn.put("ServerboundKeepAlivePacket", (f) -> new NPacketPlayInKeepAlive(((ServerboundKeepAlivePacket) f).getId()));
		/*packetsPlayIn.put("CPacketUseEntity", (f) -> {
			ClientboundUseEntityPacket p = (ClientboundUseEntityPacket) f;
			Vec3d v = p.getHitVec();
			return new NPacketPlayInUseEntity(0, v == null ? new Vector(0, 0, 0) : new Vector(v.x, v.y, v.z),
					EnumEntityUseAction.valueOf(p.getAction().name()));
		});*/

		
		
		packetsPlayOut.put("ServerboundPlayerActionPacket", (f) -> {
			ServerboundPlayerActionPacket packet = (ServerboundPlayerActionPacket) f;
			BlockPos pos = packet.getPos();
			return new NPacketPlayOutBlockBreakAnimation(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
		});
		packetsPlayOut.put("ClientboundKeepAlivePacket", (f) -> new NPacketPlayOutKeepAlive(get(f, "id")));
		/*packetsPlayOut.put("SPacketEntityTeleport", (f) -> {
			ServerboundEntityTeleport packet = (ServerboundEntityTeleportPacket) f;
			return new NPacketPlayOutEntityTeleport(packet.entityId, packet.posX, packet.posY, packet.posZ, packet.yaw,
					packet.pitch, packet.onGround);
		});
		packetsPlayOut.put("SPacketEntityVelocity", (f) -> {
			ServerboundEntityVelocityPacket packet = (ServerboundEntityVelocityPacket) f;
			return new NPacketPlayOutEntityVelocity(packet.entityID, packet.motionX, packet.motionY, packet.motionZ);
		});
		packetsPlayOut.put("SPacketPlayerPosLook", (f) -> {
			ServerboundPlayerPosLookPacket packet = (ServerboundPlayerPosLookPacket) f;
			return new NPacketPlayOutPosition(packet.x, packet.y, packet.z, packet.yaw, packet.pitch);
		});
		packetsPlayOut.put("SPacketExplosion", (f) -> {
			ServerboundExplosionPacket packet = (ServerboundExplosionPacket) f;
			return new NPacketPlayOutExplosion(packet.posX, packet.posY, packet.posZ, packet.motionX, packet.motionY,
					packet.motionZ);
		});
		packetsPlayOut.put("SPacketEntity", (f) -> {
			ServerboundEntityPacket packet = (ServerboundEntityPacket) f;
			return new NPacketPlayOutEntity(packet.entityId, packet.posX, packet.posY, packet.posZ);
		});
		packetsPlayOut.put("SPacketEntityEffect", (f) -> {
			ServerboundEntityEffectPacket packet = (ServerboundEntityEffectPacket) f;
			return new NPacketPlayOutEntityEffect(packet.getEntityId(), packet.getEffectId(), packet.getAmplifier(),
					packet.getDuration(), (byte) 0);
		});*/

		SpongeNegativity.getInstance().getLogger().info("[Packets-" + version + "] Loaded " + packetsPlayIn.size()
				+ " PlayIn and " + packetsPlayOut.size() + " PlayOut.");
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

	protected <T> T get(Object obj, String name) {
		return get(obj.getClass(), obj, name);
	}

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

	protected <T> T getFromMethod(Object obj, String methodName) {
		return getFromMethod(obj.getClass(), obj, methodName);
	}

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
}
