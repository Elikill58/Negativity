package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastLadder extends Cheat implements Listeners {

	public FastLadder() {
		super(CheatKeys.FAST_LADDER, CheatCategory.MOVEMENT, Materials.LADDER, false, false, "ladder", "ladders");
	}

	@Check(name = "distance", description = "Check Y move only", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA, CheckConditions.NO_FLY })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Location loc = p.getLocation().clone();
		if (!loc.getBlock().getType().equals(Materials.LADDER)){
			np.isOnLadders = false;
			return;
		}
		if (!np.isOnLadders) {
			np.isOnLadders = true;
			return;
		}
		if(p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		for (PotionEffect pe : p.getActivePotionEffect())
			if (pe.getType().equals(PotionEffectType.SPEED) && pe.getAmplifier() > 2)
				return;
		if(LocationUtils.hasMaterialsAround(loc, "WATER"))
			return;
		Location from = e.getFrom(), to = e.getTo();
		Location fl = from.clone().sub(to);
		double distance = to.toVector().distance(from.toVector());
		int nbLadder = 0;
		Location tempLoc = loc.clone();
		while(tempLoc.getBlock().getType() == Materials.LADDER) {
			nbLadder++;
			tempLoc.sub(0, 1, 0);
		}
		if (distance > 0.23 && distance < 3.8 && nbLadder > 2 && loc.add(0, 1, 0).getBlock().getType().getId().contains("LADDER")) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 350), "distance",
					"On ladders. Distance from/to : " + distance + ". Number Ladder: " + nbLadder, hoverMsg("main", "%nb%", nbLadder));
			if (isSetBack() && mayCancel)
				e.setTo(e.getFrom().clone().add(fl.getX() / 2, (fl.getY() / 2) + 0.5, fl.getZ()));
		}
	}
}
