package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;

public class FastEatProtocol extends Cheat implements Listener {
	
	public FastEatProtocol() {
		super(CheatKeys.FAST_EAT, true, Material.COOKED_BEEF, CheatCategory.PLAYER, true, "fasteat", "autoeat");
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onItemConsume(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		np.flyingReason = FlyingReason.EAT;
		np.eatMaterial = e.getItem().getType();
	}
}
