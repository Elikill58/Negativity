package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;

public class TimerSpawnFakePlayer extends BukkitRunnable {

	@Override
	public void run() {
		for(Player p : Utils.getOnlinePlayers())
			SpigotNegativityPlayer.getNegativityPlayer(p).makeAppearEntities();
	}

}
