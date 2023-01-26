package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.NegativityPlayerMoveEvent;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SneakProtocol extends Cheat implements Listener {

	public SneakProtocol() {
		super(CheatKeys.SNEAK, true, Material.BLAZE_POWDER, CheatCategory.MOVEMENT, true, "sneack", "sneac");
	}
	
	@EventHandler
	public void onMove(NegativityPlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if (!np.hasDetectionActive(this) || np.getPlayerVersion().isNewerOrEquals(Version.V1_14))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.isSneaking() && p.isSprinting() && !p.isFlying() && np.contentBoolean.getOrDefault("sneak-was-sneaking", false)) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(105 - (np.ping / 10)), "Sneak, sprint and not fly. ws: " + p.getWalkSpeed() + ", fd: " + p.getFallDistance());
			if(mayCancel && isSetBack()) {
				e.setCancelled(true);
				p.setSprinting(false);
			}
		}
		np.contentBoolean.put("sneak-was-sneaking", p.isSneaking());
	}
}
