package com.elikill58.negativity.minestom.packets;

import java.util.NoSuchElementException;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;
import com.elikill58.negativity.minestom.impl.packet.FabricPacketManager;
import com.elikill58.negativity.minestom.nms.FabricVersionAdapter;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import net.minestom.server.MinecraftServer;

public class CustomPacketManager extends FabricPacketManager {

	static final String KEY_HANDLER_PLAYER = "packet_handler", KEY_PLAYER = "packet_player_negativity", KEY_HANDSHAKE = "packet_handshake_negativity",
			KEY_HANDLER_SERVER = "packet_handler", KEY_SERVER = "packet_server_negativity";
	
	public CustomPacketManager() {
		
	}
	
	public AbstractPacket onPacketSent(NPacket packet, Player sender, Object nmsPacket) {
		FabricPacket customPacket = new FabricPacket(packet, nmsPacket, sender);
		notifyHandlersSent(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(NPacket packet, Player sender, Object nmsPacket) {
		if(packet.getPacketType().isUnset())
			Adapter.getAdapter().debug("Received unset: " + packet.getClass().getSimpleName() + " > " + FabricVersionAdapter.getVersionAdapter().getNameOfPacket(nmsPacket));
		FabricPacket customPacket = new FabricPacket(packet, nmsPacket, sender);
		notifyHandlersReceive(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	private class ChannelHandlerReceive extends ChannelInboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerReceive(Player player) {
			this.owner = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			NPacket commonPacket = FabricVersionAdapter.getVersionAdapter().getPacket(owner, PacketDirection.CLIENT_TO_SERVER, packet);
			if(commonPacket == null) {
				super.channelRead(ctx, packet);
				return;
			}
			AbstractPacket nextPacket = onPacketReceive(commonPacket, owner, packet);
			if(!nextPacket.isCancelled())
				super.channelRead(ctx, nextPacket.getNmsPacket() != null ? nextPacket.getNmsPacket() : packet);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			if(ctx == null || ctx.isRemoved())
				return;
			if(cause.getMessage().toLowerCase().contains("connection reset by ")
					|| cause.getLocalizedMessage().toLowerCase().contains("connection reset by "))
				return;
			Adapter.getAdapter().getLogger().error("Exception caught when reading packet");
			cause.printStackTrace();
		}
	}

	private class ChannelHandlerSent extends ChannelOutboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerSent(Player player) {
			this.owner = player;
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			NPacket commonPacket = FabricVersionAdapter.getVersionAdapter().getPacket(owner, PacketDirection.SERVER_TO_CLIENT, packet);
			if(commonPacket == null) {
				super.write(ctx, packet, promise);
				return;
			}
			AbstractPacket nextPacket = onPacketSent(commonPacket, this.owner, packet);
			if(!nextPacket.isCancelled())
				super.write(ctx, nextPacket.getNmsPacket() != null ? nextPacket.getNmsPacket() : packet, promise);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			if(cause.getMessage().toLowerCase().contains("connection reset by ")
					|| cause.getLocalizedMessage().toLowerCase().contains("connection reset by "))
				return;
			Adapter.getAdapter().getLogger().error("Exception caught when sending packet");
			cause.printStackTrace();
		}
	}
}
