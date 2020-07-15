package com.elikill58.negativity.protocols;

import static com.elikill58.negativity.common.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.common.utils.LocationUtils.hasOtherThanExtended;
import static com.elikill58.negativity.universal.CheatKeys.AIR_JUMP;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerMoveEvent;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.utils.LocationUtils;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AirJump extends Cheat implements Listeners {

	public AirJump() {
		super(AIR_JUMP, false, Materials.FEATHER, CheatCategory.MOVEMENT, true, "airjump", "air", "jump");
	}

	@EventListener
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (p.isFlying() || p.isInsideVehicle() || p.getItemInHand().getType().getId().contains("TRIDENT") || p.hasElytra() || np.isInFight)
			return;
		boolean mayCancel = false;
		
		double diffYtoFrom = e.getTo().getY() - e.getFrom().getY();
		Location loc = p.getLocation().clone(), locDown = loc.sub(0, 1, 0);
		if (diffYtoFrom > 0.35 && np.lastYDiff < diffYtoFrom && np.lastYDiff > 0 && !hasOtherThanExtended(loc.clone(), "AIR")
				&& !hasOtherThanExtended(locDown, "AIR")
				&& !hasOtherThanExtended(loc.clone().sub(0, 2, 0), "AIR")) {
			mayCancel = SpigotNegativity.alertMod(
					diffYtoFrom > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent((int) (diffYtoFrom * 210) - p.getPing()),
					"Actual diff Y: " + np.lastYDiff + ", last diff Y: " + diffYtoFrom + ", ping: " + p.getPing()
							+ ". Warn for AirJump: " + np.getWarn(this));
		}
		np.lastYDiff = diffYtoFrom;
		
		boolean wasGoingDown = np.booleans.get(AIR_JUMP, "going-down", false);
		double d = np.doubles.get(AIR_JUMP, "diff-y", 0.0);
		if(diffYtoFrom > d && wasGoingDown) {
			if(!hasOtherThanExtended(locDown, "AIR") && !hasOtherThan(loc, "AIR")) {
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffYtoFrom * 200), "Was going down, last y " + d + ", current: " + diffYtoFrom);
			}
		}
		np.doubles.set(AIR_JUMP, "diff-y", diffYtoFrom);
		np.booleans.set(AIR_JUMP, "going-down", diffYtoFrom < 0);
		if (isSetBack() && mayCancel)
			LocationUtils.teleportPlayerOnGround(p);
	}
}
