package com.elikill58.negativity.api.packets.nms;

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

public abstract class VersionAdapter<R> {

	protected PacketTransferManager<NPacketPlayOut, R> packetsPlayOut = new PacketTransferManager<>((p, obj) -> new NPacketPlayOutUnset(obj.getClass().getName()), (p, a) -> null);
	protected PacketTransferManager<NPacketPlayIn, R> packetsPlayIn = new PacketTransferManager<>((p, obj) -> new NPacketPlayInUnset(obj.getClass().getName()), (p, a) -> null);
	protected PacketTransferManager<NPacketHandshake, R> packetsHandshake = new PacketTransferManager<>((p, obj) -> new NPacketHandshakeUnset(), (p, a) -> null);
	protected PacketTransferManager<NPacketStatus, R> packetsStatus = new PacketTransferManager<>((p, obj) -> new NPacketStatusUnset(), (p, a) -> null);
	
	public VersionAdapter() {
		
	}
	
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
		Adapter.getAdapter().debug("[SpigotVersionAdapter] Unknow packet " + packetName + ".");
		return null;
	}
}
