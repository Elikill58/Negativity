package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.spigot.utils.LocationUtils.hasOtherThanExtended;

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
		if (!np.hasDetectionActive(this))
			return;
		if (p.isFlying() || p.getVehicle() != null || p.getItemInHand().getType().name().contains("TRIDENT") || np.hasElytra() || np.isInFight)
			return;
		Location loc = p.getLocation().clone(), locDown = loc.clone().subtract(0, 1, 0), locDownDown = locDown.clone().subtract(0, 1, 0);
		boolean mayCancel = false, hasOtherThanAir = hasOtherThanExtended(loc, "AIR"), hasOtherThanAirDown = hasOtherThanExtended(locDown, "AIR");
		
		double diffYtoFrom = e.getTo().getY() - e.getFrom().getY();
		if (diffYtoFrom > 0.35 && np.lastYDiff < diffYtoFrom && np.lastYDiff > 0 && !hasOtherThanAir
				&& !hasOtherThanAirDown && !hasOtherThanExtended(loc.clone().subtract(0, 2, 0), "AIR")) {
			mayCancel = SpigotNegativity.alertMod(
					diffYtoFrom > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent((int) (diffYtoFrom * 210) - Utils.getPing(p)),
					"Actual diff Y: " + np.lastYDiff + ", last diff Y: " + diffYtoFrom + ", ping: " + Utils.getPing(p)
							+ ". Warn for AirJump: " + np.getWarn(this));
		}
		np.lastYDiff = diffYtoFrom;
		
		boolean wasGoingDown = np.contentBoolean.getOrDefault("going-down", false);
		double d = np.contentDouble.getOrDefault("airjump-diff-y", 0.0);
		if(diffYtoFrom > d && wasGoingDown && diffYtoFrom != 0.5) { // 0.5 when use stairs or slab
			if(!hasOtherThanAirDown && !hasOtherThanAir && !locDownDown.getBlock().getType().name().contains("STAIR")) {
				mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffYtoFrom * 200), "Was going down, last y " + d + ", current: " + diffYtoFrom);
			}
		}
		np.contentDouble.put("airjump-diff-y", diffYtoFrom);
		np.contentBoolean.put("going-down", diffYtoFrom < 0);
		if (isSetBack() && mayCancel)
			Utils.teleportPlayerOnGround(p);
	}
}
