package com.elikill58.negativity.sponge7.listeners;

import java.util.Locale;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.First;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;

public class CommandsListeners {

	@Listener
	public void onCommandPreProcess(SendCommandEvent e, @First Player p) {
		String cmd = e.getCommand();
		String[] arg = e.getArguments().split(" ");
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		PlayerCommandPreProcessEvent event = new PlayerCommandPreProcessEvent(SpongeEntityManager.getPlayer(p), cmd, arg, prefix, false);
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(true);
	}
}
