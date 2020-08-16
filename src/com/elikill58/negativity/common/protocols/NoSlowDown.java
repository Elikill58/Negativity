package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.NO_SLOW_DOWN;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoSlowDown extends Cheat implements Listeners {

	public NoSlowDown() {
		super(NO_SLOW_DOWN, false, Materials.SOUL_SAND, CheatCategory.MOVEMENT, true, "slowdown");
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || p.hasElytra())
			return;
		Location loc = p.getLocation();
		Location from = e.getFrom(), to = e.getTo();
		double xSpeed = Math.abs(from.getX() - to.getX());
	    double zSpeed = Math.abs(from.getZ() - to.getZ());
	    double xzSpeed = Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed);
	    double maxSpeed = (xSpeed >= zSpeed ? xSpeed : zSpeed);
	    if(maxSpeed < xzSpeed)
	    	maxSpeed = xzSpeed;
	    np.doubles.set(NO_SLOW_DOWN, "eating-distance", maxSpeed);
		if (!loc.getBlock().getType().equals(Materials.SOUL_SAND) || p.hasPotionEffect(PotionEffectType.SPEED))
			return;
		Location fl = from.clone().sub(to.clone());
		double distance = to.toVector().distance(from.toVector());
		if (distance > 0.2) {
			int ping = p.getPing(), relia = UniversalUtils.parseInPorcent(distance * 400);
			if((from.getY() - to.getY()) < -0.001)
				return;
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, relia,
					"Soul sand under player. Distance from/to : " + distance + ". Ping: " + ping);
			if (isSetBack() && mayCancel)
				e.setTo(from.clone().add(fl.getX() / 2, (fl.getY() / 2) + 0.5, fl.getZ()));
		}
	}

	@EventListener
	public void FoodCheck(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || p.hasElytra())
			return;
		if(p.isInsideVehicle())
			return;
		double dis = np.doubles.get(NO_SLOW_DOWN, "eating-distance", 0.0);
		if (dis > p.getWalkSpeed() || p.isSprinting()) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(dis * 200),
					"Distance while eating: " + dis + ", WalkSpeed: " + p.getWalkSpeed(), hoverMsg("main", "%distance%", String.format("%.2f", dis)));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
}
