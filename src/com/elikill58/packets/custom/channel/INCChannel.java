package com.elikill58.orebfuscator.packets.custom.channel;

import org.bukkit.entity.Player;

import com.elikill58.orebfuscator.packets.AbstractPacket;
import com.elikill58.orebfuscator.packets.PacketType;
import com.elikill58.orebfuscator.packets.custom.CustomPacketManager;
import com.elikill58.orebfuscator.utils.Utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class INCChannel extends ChannelAbstract {

	private Class<?> craftPlayerClass = null;
	
	public INCChannel(CustomPacketManager customPacketManager) {
		super(customPacketManager);
		try {
			craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + Utils.VERSION + ".entity.CraftPlayer");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
					} catch (IllegalArgumentException e) {
						getPacketManager().getPlugin().getLogger().severe("Error while loading Packet channel. " + e.getMessage() + ". Please, prefer restart than reload.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
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
						if (channel.pipeline().get(KEY_PLAYER) != null)
							channel.pipeline().remove(KEY_PLAYER);
						
						if (channel.pipeline().get(KEY_HANDLER_SERVER) != null)
							channel.pipeline().remove(KEY_HANDLER_SERVER);
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
		}
	}

	Channel getChannel(Player p) throws ReflectiveOperationException {
		try {
			Object craftPlayer = craftPlayerClass.cast(p);
			Object entityPlayer = craftPlayer.getClass().getMethod("getHandle").invoke(craftPlayer);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
			return (Channel) networkManager.getClass().getDeclaredField("channel").get(networkManager);
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
				super.channelRead(ctx, nextPacket.getPacket());
		}
		
		public Player getOwner() {
			return owner;
		}
	}

	class ChannelHandlerSent extends ChannelOutboundHandlerAdapter {

		private Player owner;

		public ChannelHandlerSent(Player player) {
			this.owner = player;
		}
		
		public Player getOwner() {
			return owner;
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			AbstractPacket nextPacket = getPacketManager().onPacketSent(PacketType.getType(packet.getClass().getSimpleName()), owner, packet);
			if(!nextPacket.isCancelled())
				super.write(ctx, packet, promise);
		}
	}
}
