package com.elikill58.negativity.spigot.events;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ChannelEvents implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] data) {
		//DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
		if(channel.equalsIgnoreCase(UniversalUtils.CHANNEL_NEGATIVITY_BUNGEECORD)) {
			if(!SpigotNegativity.isOnBungeecord){
				//SpigotNegativity.getInstance().getLogger().warning("BungeeNegativity has been found but its support isn't enabled on your server. Is it intended? If so you can ignore this message, otherwise please edit the configuration.");
				Logger log = SpigotNegativity.getInstance().getLogger();
				log.warning("A bungeecord system have been detected, nut not written in configuration. Editing config ...");
				SpigotNegativity.isOnBungeecord = true;
				Adapter.getAdapter().set("hasBungeecord", true);
				log.warning("Configuration well edited !");
			}
		} else if (channel.equalsIgnoreCase(UniversalUtils.CHANNEL_NEGATIVITY_MOD)) {
			List<String> lines = new ArrayList<>();
			try (ByteArrayInputStream ba = new ByteArrayInputStream(data);
					DataInputStream in = new DataInputStream(ba)) {
				lines.add(in.readUTF());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(lines.size() == 0)
				return;
			if(!lines.get(0).contains("mod"))
				return;
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			for(String l : lines) {
				if(l == lines.get(0))
					continue;
				String[] temp = l.split(":");
				np.MODS.put(temp[0], temp[1]);
			}
		} else if (channel.equalsIgnoreCase(SpigotNegativity.CHANNEL_NAME_FML)) {
			if (data[0] == 2) {
				SpigotNegativityPlayer.getNegativityPlayer(p).MODS.putAll(getModData(data));
			}
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
