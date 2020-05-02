package com.elikill58.negativity.spigot.packets.custom.channel;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.PacketType;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.utils.Utils;

import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelPromise;

public class NMUChannel extends ChannelAbstract {

	public NMUChannel(CustomPacketManager customPacketManager) {
		super(customPacketManager);
	}

	@Override
	public void addChannel(final Player player, String endChannelName) {
		try {
			final Channel channel = getChannel(player);
			getOrCreateAddChannelExecutor().execute(() -> {
				try {
					// Managing incoming packet (from player)
					if(channel.pipeline().get(KEY_HANDLER_PLAYER) == null)
						channel.pipeline().addBefore(KEY_HANDLER_PLAYER, KEY_PLAYER + endChannelName, new ChannelHandlerReceive(player));
					else
						getPacketManager().getPlugin().getLogger().warning("The Incoming Packet channel " + KEY_HANDLER_PLAYER + "for " + player.getName() + " is already started.");
					
					// Managing outgoing packet (to the player)
					if(channel.pipeline().get(KEY_HANDLER_SERVER) == null)
						channel.pipeline().addAfter(KEY_HANDLER_SERVER, KEY_SERVER + endChannelName, new ChannelHandlerSent(player));
					else
						getPacketManager().getPlugin().getLogger().warning("The Outgoing Packet channel " + KEY_HANDLER_PLAYER + "for " + player.getName() + " is already started.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeChannel(Player player, String endChannelName) {
		try {
			final Channel channel = getChannel(player);
			getOrCreateRemoveChannelExecutor().execute(() -> {
				try {
					if (channel.pipeline().get(KEY_HANDLER_PLAYER) != null)
						channel.pipeline().remove(KEY_HANDLER_PLAYER);
					
					if (channel.pipeline().get(KEY_HANDLER_SERVER) != null)
						channel.pipeline().remove(KEY_HANDLER_SERVER);
				} catch (Exception e) {
				}
			});
		} catch (Exception e) {
		}
	}

	private Channel getChannel(Player p) {
		try {
			Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + Utils.VERSION + ".entity.CraftPlayer").cast(p);
			Object entityPlayer = craftPlayer.getClass().getMethod("getHandle").invoke(craftPlayer);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
			
			for (Field field : networkManager.getClass().getDeclaredFields())
				if (field.getType().equals(Channel.class)) {
					field.setAccessible(true);
					return (Channel) field.get(networkManager);
				}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	class ChannelHandlerReceive extends ChannelInboundHandlerAdapter {

		private Player owner;

		public ChannelHandlerReceive(Player player) {
			this.owner = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			AbstractPacket nextPacket = getPacketManager().onPacketReceive(PacketType.getType(packet.getClass().getSimpleName()), this.owner, packet);
			if(!nextPacket.isCancelled())
				super.channelRead(ctx, packet);
		}
	}

	class ChannelHandlerSent extends ChannelOutboundHandlerAdapter {

		private Player owner;

		public ChannelHandlerSent(Player player) {
			this.owner = player;
		}
		
		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			AbstractPacket nextPacket = getPacketManager().onPacketSent(PacketType.getType(packet.getClass().getSimpleName()), owner, packet);
			if(!nextPacket.isCancelled())
				super.write(ctx, packet, promise);
		}
	}
}
