package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AirJumpProtocol extends Cheat implements Listener {

	public AirJumpProtocol() {
		super(CheatKeys.AIR_JUMP, false, Material.FEATHER, CheatCategory.MOVEMENT, true, "airjump", "air", "jump");
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (p.isFlying() || p.getVehicle() != null || p.getItemInHand().getType().name().contains("TRIDENT") || np.hasElytra() || np.isInFight)
			return;
		double temp = e.getTo().getY() - e.getFrom().getY();
		Location loc = p.getLocation().clone();
		if (temp > 0.35 && np.lastYDiff < temp && np.lastYDiff > 0 && !np.hasOtherThanExtended(loc.clone(), "AIR")
				&& !np.hasOtherThanExtended(loc.clone().subtract(0, 1, 0), "AIR")
				&& !np.hasOtherThanExtended(loc.clone().subtract(0, 2, 0), "AIR")) {
			boolean mayCancel = SpigotNegativity.alertMod(
					temp > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent((int) (temp * 210) - Utils.getPing(p)),
					"Actual diff Y: " + np.lastYDiff + ", last diff Y: " + temp + ", ping: " + Utils.getPing(p)
							+ ". Warn for AirJump: " + np.getWarn(this));
			if (isSetBack() && mayCancel)
				Utils.teleportPlayerOnGround(p);
		}
		np.lastYDiff = temp;
	}
}
