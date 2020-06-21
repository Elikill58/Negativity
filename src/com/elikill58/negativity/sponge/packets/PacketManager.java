package com.elikill58.negativity.sponge.packets;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.packets.events.PacketEvent.PacketSourceType;
import com.elikill58.negativity.sponge.packets.events.PacketReceiveEvent;
import com.elikill58.negativity.sponge.packets.events.PacketSendEvent;

public abstract class PacketManager {

	public abstract void addPlayer(Player p);
	public abstract void removePlayer(Player p);
	public abstract void clear();

	private final List<PacketHandler> handlers = new ArrayList<>();
	public boolean addHandler(PacketHandler handler) {
		boolean b = handlers.contains(handler);
		handlers.add(handler);
		return !b;
	}

	public boolean removeHandler(PacketHandler handler) {
		return handlers.remove(handler);
	}
	
	public void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		// Go on main Thread
		Sponge.getScheduler().createSyncExecutor(SpongeNegativity.getInstance()).execute(() -> {
			PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
			Sponge.getEventManager().post(event);
			handlers.forEach((handler) -> handler.onReceive(packet));
		});
	}

	public void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		// Go on main Thread
		Sponge.getScheduler().createSyncExecutor(SpongeNegativity.getInstance()).execute(() -> {
			PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
			Sponge.getEventManager().post(event);
			handlers.forEach((handler) -> handler.onSend(packet));
		});
	}
}
