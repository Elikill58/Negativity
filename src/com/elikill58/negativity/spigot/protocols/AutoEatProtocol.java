package com.elikill58.negativity.spigot.protocols;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer.FlyingReason;

public class AutoEatProtocol implements Listener {
	
	@EventHandler (ignoreCancelled = true)
	public void onItemConsume(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		np.flyingReason = FlyingReason.EAT;
		np.eatMaterial = p.getItemInHand().getType();
	}
}
