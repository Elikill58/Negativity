package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class SneakProtocol extends Cheat implements Listener {

	public SneakProtocol() {
		super(CheatKeys.SNEAK, true, Material.BLAZE_POWDER, CheatCategory.MOVEMENT, true, "sneack");
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.isSneaking() && p.isSprinting() && !p.isFlying()) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, 99, "Sneaking, sprinting and not flying.");
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
