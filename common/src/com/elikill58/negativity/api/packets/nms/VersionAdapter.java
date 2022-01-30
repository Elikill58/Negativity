package com.elikill58.negativity.api.packets.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.elikill58.negativity.api.packets.PacketTransferManager;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketHandshake;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.packets.packet.NPacketStatus;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeUnset;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.api.packets.packet.status.NPacketStatusUnset;
import com.elikill58.negativity.universal.Adapter;

@SuppressWarnings("unchecked")
public abstract class VersionAdapter<R> {

	protected PacketTransferManager<NPacketPlayOut, R> packetsPlayOut = new PacketTransferManager<>((p, obj) -> new NPacketPlayOutUnset(obj.getClass().getName()), (p, a) -> null);
	protected PacketTransferManager<NPacketPlayIn, R> packetsPlayIn = new PacketTransferManager<>((p, obj) -> new NPacketPlayInUnset(obj.getClass().getName()), (p, a) -> null);
	protected PacketTransferManager<NPacketHandshake, R> packetsHandshake = new PacketTransferManager<>((p, obj) -> new NPacketHandshakeUnset(), (p, a) -> null);
	protected PacketTransferManager<NPacketStatus, R> packetsStatus = new PacketTransferManager<>((p, obj) -> new NPacketStatusUnset(), (p, a) -> null);
	
	public void sendPacket(R p, NPacket packet) {
		if(packet instanceof NPacketPlayIn)
			sendPacket(p, packetsPlayIn.negativityToBukkit(p, (NPacketPlayIn) packet));
		else if(packet instanceof NPacketPlayOut)
			sendPacket(p, packetsPlayOut.negativityToBukkit(p, (NPacketPlayOut) packet));
		else if(packet instanceof NPacketHandshake)
			sendPacket(p, packetsHandshake.negativityToBukkit(p, (NPacketHandshake) packet));
		else if(packet instanceof NPacketStatus)
			sendPacket(p, packetsStatus.negativityToBukkit(p, (NPacketStatus) packet));
	}
	
	public abstract void sendPacket(R p, Object basicPacket);

	public NPacket getPacket(R player, Object nms) {
		String packetName = nms.getClass().getSimpleName();
		if (packetName.startsWith(PacketType.CLIENT_PREFIX))
			return packetsPlayIn.bukkitToNegativity(player, nms);
		else if (packetName.startsWith(PacketType.SERVER_PREFIX))
			return packetsPlayOut.bukkitToNegativity(player, nms);
		else if (packetName.startsWith(PacketType.LOGIN_PREFIX))
			return new NPacketLoginUnset();
		else if (packetName.startsWith(PacketType.STATUS_PREFIX))
			return packetsStatus.bukkitToNegativity(player, nms);
		else if (packetName.startsWith(PacketType.HANDSHAKE_PREFIX))
			return packetsHandshake.bukkitToNegativity(player, nms);
		Adapter.getAdapter().debug("[VersionAdapter] Unknow packet " + packetName + ".");
		return null;
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
