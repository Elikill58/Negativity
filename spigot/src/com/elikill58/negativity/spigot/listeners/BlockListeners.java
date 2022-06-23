package com.elikill58.negativity.spigot.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.packets.custom.CustomPacket;
import com.elikill58.negativity.universal.Version;

public class BlockListeners implements Listener {

	@EventHandler
	public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
		if(!Version.getVersion().equals(Version.V1_7)) // for 1.7, event called from PacketManager
			return;
		BlockBreakEvent event = new BlockBreakEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotBlock(e.getBlock()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}

	@EventHandler
	public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent e) {
		Player p = SpigotEntityManager.getPlayer(e.getPlayer());
		Block b = e.getBlock();
		// TODO make it better by using real better and not the parsed one by spigot
		CustomPacket packet = new CustomPacket(new NPacketPlayInBlockPlace(b.getX(), b.getY(), b.getZ()), new NPacketPlayInBlockPlace(), p);
		SpigotNegativity.getInstance().getPacketManager().getPacketManager().notifyHandlersReceive(PacketSourceType.CUSTOM, packet);
		if(packet.isCancelled())
			e.setCancelled(true);
		BlockPlaceEvent event = new BlockPlaceEvent(p, new SpigotBlock(b));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
}
