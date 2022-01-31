package com.elikill58.negativity.spigot.listeners;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.PlayerVersionMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;

public class ChannelListeners implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] data) {
		if (channel.equalsIgnoreCase(SpigotNegativity.CHANNEL_NAME_FML) && data[0] == 2) {
			NegativityPlayer.getCached(p.getUniqueId()).MODS.putAll(getModData(data));
			return;
		}

		if (!channel.equalsIgnoreCase(NegativityMessagesManager.CHANNEL_ID)) {
			return;
		}
		Logger log = SpigotNegativity.getInstance().getLogger();
		try {
			NegativityMessage message = NegativityMessagesManager.readMessage(data);
			if (message == null) {
				log.warning(String.format("Received unknown plugin message. Channel %s send to %s.", channel, p));
				return;
			}

			if (message instanceof ProxyPingMessage) {
				ProxyPingMessage pingMessage = (ProxyPingMessage) message;
				ProxyCompanionManager.foundCompanion(pingMessage);
			} else if (message instanceof ClientModsListMessage) {
				ClientModsListMessage modsMessage = (ClientModsListMessage) message;
				NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
				if(!modsMessage.getMods().isEmpty()) {
					np.MODS.clear();
					np.MODS.putAll(modsMessage.getMods());
				}
			} else if(message instanceof PlayerVersionMessage) {
				SpigotEntityManager.getPlayer(p).setPlayerVersion(((PlayerVersionMessage) message).getVersion());
			} else {
				log.warning("Received unexpected plugin message " + message.getClass().getName());
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Could not read plugin message.", e);
		}
	}

	private HashMap<String, String> getModData(byte[] data) {
		HashMap<String, String> mods = new HashMap<>();
		boolean store = false;
		String tempName = null;
		for (int i = 2; i < data.length; store = !store) {
			int end = i + data[i] + 1;
			byte[] range = Arrays.copyOfRange(data, i + 1, end);
			String string = new String(range);
			if (store)
				mods.put(tempName, string);
			else
				tempName = string;
			i = end;
		}
		return mods;
	}
}
