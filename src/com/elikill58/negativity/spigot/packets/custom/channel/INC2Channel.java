package com.elikill58.negativity.spigot.packets.custom.channel;

import static com.elikill58.negativity.spigot.utils.PacketUtils.getPlayerConnection;

import java.util.List;
import java.util.NoSuchElementException;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class INC2Channel extends ChannelAbstract {
	
	public INC2Channel(CustomPacketManager customPacketManager) {
		super(customPacketManager);
		try {
			Object mcServer = ReflectionUtils.callMethod(PacketUtils.getCraftServer(), Version.getVersion().equals(Version.V1_17) ? "getServer" : "b");
			//DedicatedServer mcServer = ((DedicatedPlayerList) PacketUtils.getCraftServer()).b();//getServer();
			Object co = ReflectionUtils.getFirstWith(mcServer, PacketUtils.getNmsClass("MinecraftServer", "server."), PacketUtils.getNmsClass("ServerConnection", "server.network."));
			((List<ChannelFuture>) ReflectionUtils.getField(co, "f")).forEach((channelFuture) -> {
				channelFuture.channel().pipeline().addFirst(new ChannelInboundHandler(customPacketManager));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
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

				AbstractPacket pa = ChannelInboundHandler.TMP.remove(channel);
				if(pa != null)
					SpigotNegativityPlayer.getNegativityPlayer(player).setProtocolVersion(pa.getContent().getIntegers().readSafely(0, Version.getVersion().getProtocolNumber().get(0)));
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

	@Override
	public Channel getChannel(Player p) throws Exception {
		Object playerConnection = getPlayerConnection(p);
		Object networkManager = ReflectionUtils.getFirstWith(playerConnection, playerConnection.getClass(), PacketUtils.getNmsClass("NetworkManager", "network."));
		return ReflectionUtils.getFirstWith(networkManager, networkManager.getClass(), Channel.class);
	}

	private class ChannelHandlerReceive extends ChannelInboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerReceive(Player player) {
			this.owner = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			AbstractPacket nextPacket = getPacketManager().onPacketReceive(PacketType.getType(packet.getClass().getSimpleName()), this.owner, packet);
			if(nextPacket != null && nextPacket.isCancelled())
				return;
			super.channelRead(ctx, packet);
		}
	}

	private class ChannelHandlerSent extends ChannelOutboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerSent(Player player) {
			this.owner = player;
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			AbstractPacket nextPacket = getPacketManager().onPacketSent(PacketType.getType(packet.getClass().getSimpleName()), owner, packet);
			if(nextPacket != null && nextPacket.isCancelled())
				return;
			super.write(ctx, packet, promise);
		}
	}
}
