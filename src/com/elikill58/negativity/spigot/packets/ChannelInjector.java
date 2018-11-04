package com.elikill58.negativity.spigot.packets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.PacketAbstract.IPacketListener;
import com.elikill58.negativity.spigot.utils.Utils.Version;

public class ChannelInjector {

	private ChannelAbstract channel;
	private List<Player> players = new ArrayList<>();

	public boolean inject(IPacketListener iPacketListener) {
		try {
			if (Version.getVersion().equals(Version.V1_7))
				channel = new NMUChannel(iPacketListener);
			else
				channel = new INCChannel(iPacketListener);
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
		this.channel.addChannel(p);
	}

	public void removeChannel(Player p) {
		if(players.contains(p)) {
			players.remove(p);
			this.channel.removeChannel(p);
		}
	}
	
	public ChannelAbstract getChannel() {
		return channel;
	}
	
	public boolean contains(Player p) {
		return players.contains(p);
	}
	
	public static class ChannelWrapper<T> {

		private T channel;

		public ChannelWrapper(T channel) {
			this.channel = channel;
		}

		public T channel() {
			return this.channel;
		}
	}
}
