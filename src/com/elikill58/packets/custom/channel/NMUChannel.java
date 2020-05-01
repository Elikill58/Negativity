package com.elikill58.orebfuscator.packets.custom.channel;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import com.elikill58.orebfuscator.packets.AbstractPacket;
import com.elikill58.orebfuscator.packets.PacketType;
import com.elikill58.orebfuscator.packets.custom.CustomPacketManager;
import com.elikill58.orebfuscator.utils.Utils;

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
	public void addChannel(final Player player) {
		try {
			final Channel channel = getChannel(player);
			addChannelExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						channel.pipeline().addBefore(KEY_HANDLER, KEY_PLAYER, new ChannelHandlerReceive(player));
						channel.pipeline().addAfter(KEY_HANDLER_SERVER, KEY_SERVER, new ChannelHandlerSent(player));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeChannel(Player player) {
		try {
			final Channel channel = getChannel(player);
			removeChannelExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						if (channel.pipeline().get(KEY_PLAYER) != null) {
							channel.pipeline().remove(KEY_PLAYER);
						}
					} catch (Exception e) {
					}
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
