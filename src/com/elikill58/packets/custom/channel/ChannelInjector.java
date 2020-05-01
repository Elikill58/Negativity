package com.elikill58.orebfuscator.packets.custom.channel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.elikill58.orebfuscator.packets.custom.CustomPacketManager;
import com.elikill58.orebfuscator.utils.Utils;

public class ChannelInjector {

	private ChannelAbstract channel;
	private CustomPacketManager customPacketManager;
	private List<Player> players = new ArrayList<>();

	public ChannelInjector(CustomPacketManager customPacketManager) {
		this.customPacketManager = customPacketManager;
	}
	
	public boolean inject() {
		try {
			if (Utils.VERSION.contains("v1_7"))
				channel = new NMUChannel(customPacketManager);
			else
				channel = new INCChannel(customPacketManager);
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}

	public void addChannel(Player p) {
		if(players.contains(p))
			return;
		players.add(p);
		channel.addChannel(p);
	}

	public void removeChannel(Player p) {
		if(players.contains(p)) {
			players.remove(p);
			channel.removeChannel(p);
		}
	}
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public ChannelAbstract getChannel() {
		return channel;
	}
	
	public boolean contains(Player p) {
		return players.contains(p);
	}
}
