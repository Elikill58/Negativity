package com.elikill58.negativity.spigot.events;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ChannelEvents implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] data) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
		if (channel.equalsIgnoreCase("Negativity")) {
			try {
				if (in.readUTF().equalsIgnoreCase("bungeecord")) {
					if(!SpigotNegativity.isOnBungeecord){
						SpigotNegativity.isOnBungeecord = true;
						Adapter.getAdapter().set("hasBungeecord", true);
						Logger log = SpigotNegativity.getInstance().getLogger();
						log.warning("Warning while loading Negativity:");
						log.warning("A bungeecord system have been detected, but from the config, bungeecord is disabled.");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(channel.equalsIgnoreCase("FML|HS")) {
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
