package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastLadder extends Cheat implements Listeners {

	public FastLadder() {
		super(CheatKeys.FAST_LADDER, false, Materials.LADDER, CheatCategory.MOVEMENT, true, "ladder", "ladders");
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		Location loc = p.getLocation().clone();
		if (!loc.getBlock().getType().equals(Materials.LADDER)){
			np.isOnLadders = false;
			return;
		}
		if (!np.isOnLadders) {
			np.isOnLadders = true;
			return;
		}
		if(p.isFlying() || p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		for (PotionEffect pe : p.getActivePotionEffect())
			if (pe.getType().equals(PotionEffectType.SPEED) && pe.getAmplifier() > 2)
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
			int ping = p.getPing();
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 350), "distance",
					"On ladders. Distance from/to : " + distance + ". Ping: " + ping + "ms. Number Ladder: " + nbLadder, hoverMsg("main", "%nb%", nbLadder));
			if (isSetBack() && mayCancel)
				e.setTo(e.getFrom().clone().add(fl.getX() / 2, (fl.getY() / 2) + 0.5, fl.getZ()));
		}
	}
}
