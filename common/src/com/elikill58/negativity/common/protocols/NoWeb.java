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
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoWeb extends Cheat implements Listeners {

	public NoWeb() {
		super(CheatKeys.NO_WEB, CheatCategory.MOVEMENT, WEB, false, false, "no web");
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
		if(!checkActive("speed"))
			return;
		
		Location from = e.getFrom();
		Location to = e.getFrom();
		double distance = to.distance(from);
		Block under = p.getWorld().getBlockAt((from.getX() + to.getX()) / 2, ((from.getY() + to.getY()) / 2) - 1, (from.getZ() + to.getZ()) / 2);
		if (under.getType() == WEB && distance > (p.getWalkSpeed() * 0.17)) { //&& distance > 0.13716039608514914) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 500), "speed", "Distance: " + distance + ", fallDistance: " + p.getFallDistance() + ", walkSpeed: " + p.getWalkSpeed());
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}
}
