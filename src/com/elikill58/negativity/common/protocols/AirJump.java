package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThanExtended;
import static com.elikill58.negativity.universal.CheatKeys.AIR_JUMP;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AirJump extends Cheat implements Listeners {

	public AirJump() {
		super(AIR_JUMP, CheatCategory.MOVEMENT, Materials.FEATHER, false, false, "airjump", "air", "jump");
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
		Location loc = p.getLocation().clone(), locDown = loc.clone().sub(0, 1, 0);
		boolean mayCancel = false, hasOtherThanAir = hasOtherThanExtended(loc, "AIR"), hasOtherThanAirDown = hasOtherThanExtended(locDown, "AIR");;

		double diffYtoFrom = e.getTo().getY() - e.getFrom().getY();
		double lastDiffY = np.doubles.get(AIR_JUMP, "diff-y", 0.0);
		if(checkActive("diff-y")) {
			if (diffYtoFrom > 0.35 && lastDiffY < diffYtoFrom && lastDiffY > 0 && !hasOtherThanAir
					&& !hasOtherThanAirDown
					&& !hasOtherThanExtended(loc.clone().sub(0, 2, 0), "AIR")) {
				mayCancel = Negativity.alertMod(
						diffYtoFrom > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
								UniversalUtils.parseInPorcent((int) (diffYtoFrom * 210) - p.getPing()), "diff-y",
						"Actual diff Y: " + lastDiffY + ", last diff Y: " + diffYtoFrom);
			}
			lastDiffY = diffYtoFrom;
		}
		
		if(checkActive("going-down")) {
			boolean wasGoingDown = np.booleans.get(AIR_JUMP, "going-down", false);
			if(diffYtoFrom > lastDiffY && wasGoingDown && diffYtoFrom != 0.5) { // 0.5 when use stairs or slab
				if(!hasOtherThanAirDown && !hasOtherThanAir) {
					mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffYtoFrom * 200), "going-down",  "Was going down, last y " + lastDiffY + ", current: " + diffYtoFrom);
				}
			}
		}
		np.doubles.set(AIR_JUMP, "diff-y", diffYtoFrom);
		np.booleans.set(AIR_JUMP, "going-down", diffYtoFrom < 0);
		if (isSetBack() && mayCancel)
			LocationUtils.teleportPlayerOnGround(p);
	}
}
