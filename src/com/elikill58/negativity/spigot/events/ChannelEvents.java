package com.elikill58.negativity.spigot.events;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;

public class ChannelEvents implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] data) {
		if (channel.equalsIgnoreCase(SpigotNegativity.CHANNEL_NAME_FML) && data[0] == 2) {
			SpigotNegativityPlayer.getNegativityPlayer(p).MODS.putAll(getModData(data));
			return;
		}

		if (!channel.toLowerCase(Locale.ROOT).contains("negativity")) {
			return;
		}

		NegativityMessage message;
		try {
			message = NegativityMessagesManager.readMessage(data);
			if (message == null) {
				String warnMessage = String.format("Received unknown plugin message. Channel %s send to %s.", channel, p);
				SpigotNegativity.getInstance().getLogger().warning(warnMessage);
				return;
			}
		} catch (IOException e) {
			SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not read plugin message.", e);
			return;
		}

		if (message instanceof ProxyPingMessage) {
			ProxyPingMessage pingMessage = (ProxyPingMessage) message;
			ProxyCompanionManager.foundCompanion(pingMessage.getProtocol());
		} else if (message instanceof ClientModsListMessage) {
			ClientModsListMessage modsMessage = (ClientModsListMessage) message;
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			np.MODS.clear();
			np.MODS.putAll(modsMessage.getMods());
		} else {
			SpigotNegativity.getInstance().getLogger().warning("Received unexpected plugin message " + message.getClass().getName());
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
