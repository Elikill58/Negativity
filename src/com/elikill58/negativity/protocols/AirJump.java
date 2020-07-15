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
		double lastDiffY = np.doubles.get(AIR_JUMP, "diff-y", 0.0);
		Location loc = p.getLocation().clone(), locDown = loc.sub(0, 1, 0);
		if (diffYtoFrom > 0.35 && lastDiffY < diffYtoFrom && lastDiffY > 0 && !hasOtherThanExtended(loc.clone(), "AIR")
				&& !hasOtherThanExtended(locDown, "AIR")
				&& !hasOtherThanExtended(loc.clone().sub(0, 2, 0), "AIR")) {
			mayCancel = Negativity.alertMod(
					diffYtoFrom > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent((int) (diffYtoFrom * 210) - p.getPing()),
					"Actual diff Y: " + lastDiffY + ", last diff Y: " + diffYtoFrom + ", ping: " + p.getPing()
							+ ". Warn for AirJump: " + np.getWarn(this));
		}
		lastDiffY = diffYtoFrom;
		
		boolean wasGoingDown = np.booleans.get(AIR_JUMP, "going-down", false);
		if(diffYtoFrom > lastDiffY && wasGoingDown) {
			if(!hasOtherThanExtended(locDown, "AIR") && !hasOtherThan(loc, "AIR")) {
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffYtoFrom * 200), "Was going down, last y " + lastDiffY + ", current: " + diffYtoFrom);
			}
		}
		np.doubles.set(AIR_JUMP, "diff-y", diffYtoFrom);
		np.booleans.set(AIR_JUMP, "going-down", diffYtoFrom < 0);
		if (isSetBack() && mayCancel)
			LocationUtils.teleportPlayerOnGround(p);
	}
}
