package com.elikill58.negativity.spigot.listeners;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyDecoderHandler;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyEncoderHandler;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;

import io.netty.channel.Channel;

public class PacketListeners implements Listener {

	private ExecutorService addChannelExecutor = Executors.newSingleThreadExecutor();
	
	public ExecutorService getOrCreateAddChannelExecutor() {
		return addChannelExecutor;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		addChannel(p);
	}
	
	private void addChannel(Player p) {
		getOrCreateAddChannelExecutor().execute(() -> {
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
			Channel channel = SpigotVersionAdapter.getVersionAdapter().getChannel(p);
			try {
				// Managing incoming packet (from player)
				channel.pipeline().addBefore("decoder", "negativity_decoder", new NettyDecoderHandler(np.getPlayer()));
				
				// Managing outgoing packet (to the player)
				channel.pipeline().addAfter("encoder", "negativity_encoder", new NettyEncoderHandler(np.getPlayer()));
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
