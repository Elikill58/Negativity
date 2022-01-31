package com.elikill58.negativity.bungee.integrations;

import java.util.UUID;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.bungee.BungeeNegativity;
import com.elikill58.negativity.bungee.NegativityChannels;
import com.elikill58.negativity.bungee.impl.entity.RedisBungeePlayer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.RedisNegativityMessage;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class RedisSupport implements Listener {

	private static final String REDIS_CHANNEL = "NEGATIVITY_MESSAGES";
	
	public static void load(BungeeNegativity pl) {
		RedisBungee.getApi().registerPubSubChannels(REDIS_CHANNEL);
		pl.getProxy().getPluginManager().registerListener(pl, new RedisSupport());
	}

	public static String getProxyId() {
		return RedisBungee.getApi().getServerId();
	}
	
	public static void sendMessage(RedisNegativityMessage redisMsg) {
		try {
			RedisBungee.getApi().sendChannelMessage(REDIS_CHANNEL, new String(NegativityMessagesManager.writeMessage(redisMsg)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Player tryGetPlayer(String name) {
		return tryGetPlayer(RedisBungee.getApi().getUuidFromName(name));
	}

	public static Player tryGetPlayer(UUID uuid) {
		if(uuid == null)
			return null;
		return NegativityPlayer.getNegativityPlayer(uuid, () -> new RedisBungeePlayer(uuid)).getPlayer();
	}
	
	public static String getPlayerName(UUID uuid) {
		return RedisBungee.getApi().getNameFromUuid(uuid);
	}
	
	public static String getServerNameForPlayer(UUID uuid) {
		return BungeeNegativity.getServerName(RedisBungee.getApi().getServerFor(uuid));
	}
	
	@EventHandler
	public void pubSub(PubSubMessageEvent e) {
		if (!e.getChannel().equals(REDIS_CHANNEL))
			return;
		try {
			NegativityMessage negMsg = NegativityMessagesManager.readMessage(e.getMessage().getBytes());
			if(negMsg instanceof RedisNegativityMessage) {
				RedisNegativityMessage redisMsg = (RedisNegativityMessage) negMsg;
				if(!redisMsg.getProxyId().equalsIgnoreCase(getProxyId()))
					NegativityChannels.manageGlobalChannelMessage(redisMsg.getServer(), redisMsg.getProxyId(), redisMsg.getMessage());
			} else
				Adapter.getAdapter().getLogger().error("Received message with redis is not supported: " + negMsg.getClass().getSimpleName());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
