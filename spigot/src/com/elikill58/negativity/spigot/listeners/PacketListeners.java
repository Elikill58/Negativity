package com.elikill58.negativity.spigot.listeners;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyDecoderHandler;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyEncoderHandler;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;

import io.netty.channel.Channel;

public class PacketListeners implements Listener {

	private ExecutorService addChannelExecutor = Executors.newSingleThreadExecutor();
	
	public ExecutorService getOrCreateAddChannelExecutor() {
		return addChannelExecutor;
	}
	
	public PacketListeners() {
		/*SpigotVersionAdapter.getVersionAdapter().getFuturChannel().forEach((channelFuture) -> {
			channelFuture.channel().pipeline().addFirst(new ChannelInboundHandlerAdapter() {
				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
					ctx.fireChannelRead(msg);
					((Channel) msg).pipeline().addFirst(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel channel) {
							try {
								channel.eventLoop().submit(() -> {
									try {
										ChannelHandler interceptor = channel.pipeline().get("negativity_handshake_decoder");
										// Inject our packet interceptor
										if (interceptor == null) {
											interceptor = new NettyDecoderHandler(null, PacketDirection.HANDSHAKE);
											channel.pipeline().addBefore("decoder", "negativity_handshake_decoder", interceptor);
										}
										return interceptor;
									} catch (IllegalArgumentException e) {
										// Try again
										return channel.pipeline().get("negativity_handshake_decoder");
									}
								});
							} catch (Exception e) {
								SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
							}
						}
					});
				}
			});
		});*/
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		addChannel(p);
	}
	
	@EventHandler
	public void onLeft(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		Channel channel = SpigotVersionAdapter.getVersionAdapter().getChannel(p);
		removeChannel(channel, "negativity_decoder");
		removeChannel(channel, "negativity_encoder");
	}
	
	private void addChannel(Player p) {
		getOrCreateAddChannelExecutor().execute(() -> {
			Channel channel = SpigotVersionAdapter.getVersionAdapter().getChannel(p);
			try {
				// Managing incoming packet (from player)
				channel.pipeline().addBefore("decoder", "negativity_decoder", new NettyDecoderHandler(SpigotEntityManager.getPlayer(p), PacketDirection.CLIENT_TO_SERVER));
				
				// Managing outgoing packet (to the player)
				channel.pipeline().addBefore("encoder", "negativity_encoder", new NettyEncoderHandler(SpigotEntityManager.getPlayer(p)));
			} catch (NoSuchElementException exc) {
				// appear when the player's channel isn't accessible because of reload.
				SpigotNegativity.getInstance().getLogger().warning("Please, don't use reload, this can produce some problem. Currently, " + p.getName() + " isn't fully checked because of that. More details: " + exc.getMessage() + " (NoSuchElementException)");
			} catch (IllegalArgumentException exc) {
				if(exc.getMessage().contains("Duplicate handler")) {
					removeChannel(channel, "negativity_decoder");
					removeChannel(channel, "negativity_encoder");
					addChannel(p);
				} else
					SpigotNegativity.getInstance().getLogger().severe("Error while loading Packet channel. " + exc.getMessage() + ". Please, prefer restart than reload.");
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}
	
	private void removeChannel(Channel c, String key) {
		if(c.pipeline().get(key) != null)
			c.pipeline().remove(key);
	}
}
