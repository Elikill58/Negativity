package com.elikill58.negativity.api.packets;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;

public abstract class PacketManager {

	public abstract void addPlayer(Player p);
	public abstract void removePlayer(Player p);
	public abstract void clear();

	protected final List<PacketHandler> handlers = new ArrayList<>();
	public boolean addHandler(PacketHandler handler) {
		boolean b = handlers.contains(handler);
		handlers.add(handler);
		return !b;
	}

	public boolean removeHandler(PacketHandler handler) {
		return handlers.remove(handler);
	}
}
