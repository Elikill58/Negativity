package com.elikill58.negativity.fabric.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.fabric.impl.packet.FabricPacketManager;
import com.elikill58.negativity.fabric.nms.FabricVersionAdapter;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricManager extends FabricPacketManager {

	static final String KEY_HANDLER_PLAYER = "packet_handler", KEY_PLAYER = "packet_player_negativity", KEY_HANDSHAKE = "packet_handshake_negativity",
			KEY_HANDLER_SERVER = "packet_handler", KEY_SERVER = "packet_server_negativity";
	
	@Override
	public void addPlayer(Player p) {
		try {
			ServerPlayerEntity pe = (ServerPlayerEntity) p.getDefault();
			Channel channel = ReflectionUtils.getFirstWith(pe.networkHandler.connection, ClientConnection.class, Channel.class);
			if(channel == null) {
				Adapter.getAdapter().getLogger().warn("Failed to load packet for " + p.getName());
			} else {
				// Managing incoming packet (from player)
				channel.pipeline().addBefore(KEY_HANDLER_PLAYER, KEY_PLAYER + p.getName(), new ChannelHandlerReceive(p));
				
				// Managing outgoing packet (to the player)
				channel.pipeline().addAfter(KEY_HANDLER_SERVER, KEY_SERVER + p.getName(), new ChannelHandlerSent(p));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void removePlayer(Player p) {
		try {
			ServerPlayerEntity pe = (ServerPlayerEntity) p.getDefault();
			Channel channel = ReflectionUtils.getFirstWith(pe.networkHandler.connection, ClientConnection.class, Channel.class);
			
			if(channel.pipeline().get(KEY_PLAYER + p.getName()) != null)
				channel.pipeline().remove(KEY_PLAYER + p.getName());
			
			if(channel.pipeline().get(KEY_SERVER + p.getName()) != null)
				channel.pipeline().remove(KEY_SERVER + p.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public AbstractPacket onPacketSent(NPacket packet, Player sender, Object nmsPacket) {
		FabricPacket customPacket = new FabricPacket(packet, nmsPacket, sender);
		notifyHandlersSent(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(NPacket packet, Player sender, Object nmsPacket) {
		Adapter.getAdapter().getLogger().info("Received packet: " + packet.getPacketType().getFullName() + " > " + nmsPacket.getClass().getSimpleName());
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
			NPacket commonPacket = FabricVersionAdapter.getVersionAdapter().getPacket(owner, packet);
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
			NPacket commonPacket = FabricVersionAdapter.getVersionAdapter().getPacket(owner, packet);
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
