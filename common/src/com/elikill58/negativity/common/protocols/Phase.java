package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Phase extends Cheat {

	public Phase() {
		super(CheatKeys.PHASE, CheatCategory.MOVEMENT, Materials.WHITE_STAINED_GLASS, false, false);
	}

	@Check(name = "no-jump", description = "On air and don't jump", conditions = CheckConditions.SURVIVAL)
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		Location from = e.getFrom(), to = e.getTo();
		double y = to.getY() - from.getY();
		if (y > 0.1 && (!loc.clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR)
				|| !LocationUtils.hasOtherThan(loc.clone().sub(0, 1, 0), Materials.AIR)))
			np.isJumpingWithBlock = true;
		if (y < -0.1)
			np.isJumpingWithBlock = false;
		if (!loc.clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR)
				|| !loc.clone().sub(0, 2, 0).getBlock().getType().equals(Materials.AIR)
				|| !loc.clone().sub(0, 3, 0).getBlock().getType().equals(Materials.AIR)
				|| !loc.clone().sub(0, 4, 0).getBlock().getType().equals(Materials.AIR))
			return;
		if (y < 0)
			return;
		if (LocationUtils.hasOtherThan(loc.clone(), Materials.AIR) || LocationUtils.hasOtherThan(loc.clone().sub(0, 1, 0), Materials.AIR))
			return;
		if (!np.isJumpingWithBlock) {
			Negativity.alertMod(ReportType.VIOLATION, p, this, UniversalUtils.parseInPorcent((y * 200) + 20),
					"no-jump", "Player on air. No jumping. DistanceBetweenFromAndTo: " + y);
		}
	}
}
