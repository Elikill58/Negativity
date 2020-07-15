package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.api.item.Materials.WEB;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoWeb extends Cheat implements Listeners {

	private static final double MAX = 0.7421028493192875;
	
	public NoWeb() {
		super(CheatKeys.NO_WEB, false, WEB, CheatCategory.MOVEMENT, true, "no web");
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.isFlying() || p.hasPotionEffect(PotionEffectType.SPEED) || p.getFallDistance() > 1)
			return;
		Location l = p.getLocation();
		double distance = e.getTo().distance(e.getFrom());
		if (!(distance > MAX)) {
			Block under = l.getBlock();
			if (under.getType() == WEB && distance > 0.13716039608514914) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 500), "Distance: " + distance + ", fallDistance: " + p.getFallDistance());
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			}
		}
	}
}
