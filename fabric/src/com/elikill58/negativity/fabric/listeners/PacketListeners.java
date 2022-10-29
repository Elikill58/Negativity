package com.elikill58.negativity.fabric.listeners;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyDecoderHandler;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyEncoderHandler;
import com.elikill58.negativity.fabric.GlobalFabricNegativity;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PacketListeners {

	private ExecutorService addChannelExecutor = Executors.newSingleThreadExecutor();
	
	public ExecutorService getOrCreateAddChannelExecutor() {
		return addChannelExecutor;
	}

	public PacketListeners() {
		ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
	}

	public void onPlayerJoin(ServerPlayNetworkHandler e, PacketSender sender, MinecraftServer srv) {
		addChannel(e.player);
	}
	
	private void addChannel(ServerPlayerEntity pe) {
		getOrCreateAddChannelExecutor().execute(() -> {
			Player p = GlobalFabricNegativity.getPlayer(pe);
			Channel channel = null;
			try {
				channel = ReflectionUtils.getFirstWith(pe.networkHandler.connection, ClientConnection.class, Channel.class);
				// Managing incoming packet (from player)
				channel.pipeline().addBefore("decoder", "negativity_decoder", new NettyDecoderHandler(p, PacketDirection.CLIENT_TO_SERVER));
				
				// Managing outgoing packet (to the player)
				channel.pipeline().addAfter("encoder", "negativity_encoder", new NettyEncoderHandler(p));
			} catch (NoSuchElementException exc) {
				// appear when the player's channel isn't accessible because of reload.
				Adapter.getAdapter().getLogger().warn("Please, don't use reload, this can produce some problem. Currently, " + p.getName() + " isn't fully checked because of that. More details: " + exc.getMessage() + " (NoSuchElementException)");
			} catch (IllegalArgumentException exc) {
				if(exc.getMessage().contains("Duplicate handler")) {
					removeChannel(channel, "negativity_decoder");
					removeChannel(channel, "negativity_encoder");
					addChannel(pe);
				} else
					Adapter.getAdapter().getLogger().error("Error while loading Packet channel. " + exc.getMessage() + ". Please, prefer restart than reload.");
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}
	
	private void removeChannel(Channel c, String key) {
		if(c == null)
			return;
		if(c.pipeline().get(key) != null)
			c.pipeline().remove(key);
	}
}
