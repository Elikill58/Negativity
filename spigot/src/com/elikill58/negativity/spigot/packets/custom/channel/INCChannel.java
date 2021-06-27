package com.elikill58.negativity.spigot.packets.custom.channel;

import java.util.NoSuchElementException;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.universal.Adapter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class INCChannel extends ChannelAbstract {
	
	public INCChannel(CustomPacketManager customPacketManager) {
		super(customPacketManager);
	}

	@Override
	public void addChannel(final Player player, String endChannelName) {
		getOrCreateAddChannelExecutor().execute(() -> {
			if(!player.isOnline())
				return;
			try {
				Channel channel = getChannel(player);
				// Managing incoming packet (from player)
				channel.pipeline().addBefore(KEY_HANDLER_PLAYER, KEY_PLAYER + endChannelName, new ChannelHandlerReceive(player));
				
				// Managing outgoing packet (to the player)
				channel.pipeline().addAfter(KEY_HANDLER_SERVER, KEY_SERVER + endChannelName, new ChannelHandlerSent(player));
			} catch (NoSuchElementException e) {
				// appear when the player's channel isn't accessible because of reload.
				getPacketManager().getPlugin().getLogger().warning("Please, don't use reload, this can produce some problem. Currently, " + player.getName() + " isn't fully checked because of that. More details: " + e.getMessage() + " (NoSuchElementException)");
			} catch (IllegalArgumentException e) {
				if(e.getMessage().contains("Duplicate handler")) {
					removeChannel(player, endChannelName);
					addChannel(player, endChannelName);
				} else
					getPacketManager().getPlugin().getLogger().severe("Error while loading Packet channel. " + e.getMessage() + ". Please, prefer restart than reload.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void removeChannel(Player player, String endChannelName) {
		getOrCreateRemoveChannelExecutor().execute(() -> {
			try {
				final Channel channel = getChannel(player);
				
				if(channel.pipeline().get(KEY_PLAYER + endChannelName) != null)
					channel.pipeline().remove(KEY_PLAYER + endChannelName);
				
				if(channel.pipeline().get(KEY_SERVER + endChannelName) != null)
					channel.pipeline().remove(KEY_SERVER + endChannelName);
			} catch (Exception e) {}
		});
	}

	private Channel getChannel(Player p) {
		return SpigotVersionAdapter.getVersionAdapter().getPlayerChannel(p);
	}

	private class ChannelHandlerReceive extends ChannelInboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerReceive(Player player) {
			this.owner = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(owner, packet, packet.getClass().getSimpleName());
			AbstractPacket nextPacket = getPacketManager().onPacketReceive(commonPacket, SpigotEntityManager.getPlayer(this.owner), packet);
			if(!nextPacket.isCancelled() && nextPacket.getNmsPacket() != null)
				super.channelRead(ctx, nextPacket.getNmsPacket());
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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
			NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(owner, packet, packet.getClass().getSimpleName());
			AbstractPacket nextPacket = getPacketManager().onPacketSent(commonPacket, SpigotEntityManager.getPlayer(this.owner), packet);
			if(!nextPacket.isCancelled() && nextPacket.getNmsPacket() != null)
				super.write(ctx, nextPacket.getNmsPacket(), promise);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			Adapter.getAdapter().getLogger().error("Exception caught when sending packet");
			cause.printStackTrace();
		}
	}
}
