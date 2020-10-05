package com.elikill58.negativity.common.protocols;

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
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Phase extends Cheat implements Listeners {

	public Phase() {
		super(CheatKeys.PHASE, CheatCategory.MOVEMENT, Materials.WHITE_STAINED_GLASS, false, false);
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(!checkActive("no-jump"))
			return;
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
