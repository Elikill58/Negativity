package com.elikill58.negativity.protocols;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerMoveEvent;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.potion.PotionEffectType;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.support.EssentialsSupport;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoFall extends Cheat implements Listeners {

	public NoFall() {
		super(CheatKeys.NO_FALL, true, Materials.GRASS, CheatCategory.MOVEMENT, true);
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || e.isCancelled())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.getAllowFlight() || p.hasElytra() || p.getVehicle() != null || p.hasPotionEffect(PotionEffectType.SPEED))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double distance = to.toVector().distance(from.toVector());
		if (!(distance == 0.0D || from.getY() < to.getY())) {
			if (p.getFallDistance() == 0.0F && p.getLocation().clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR)) {
				int relia = UniversalUtils.parseInPorcent(distance * 100);
				if (p.isOnGround()) {
					if (distance > 0.79D && !(p.getWalkSpeed() > 0.45F && Negativity.essentialsSupport
							&& EssentialsSupport.checkEssentialsSpeedPrecondition(p))) {
						boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia,
								"Player in ground. FallDamage: " + p.getFallDistance() + ", DistanceBetweenFromAndTo: "
										+ distance + " (ping: " + p.getPing() + "). Warn: "
										+ np.getWarn(this));
						if(mayCancel)
							np.NO_FALL_DAMAGE += 1;
					} else if (np.NO_FALL_DAMAGE != 0) {
						if (isSetBack())
							manageDamage(p, np.NO_FALL_DAMAGE, relia);
						np.NO_FALL_DAMAGE = 0;
					}
				} else {
					if (distance > 2D) {
						boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia,
								"Player not in ground no fall Damage. FallDistance: " + p.getFallDistance()
										+ ", DistanceBetweenFromAndTo: " + distance + " (ping: " + p.getPing()
										+ "). Warn: " + np.getWarn(this));
						if(mayCancel)
							np.NO_FALL_DAMAGE += 1;
					} else if (np.NO_FALL_DAMAGE != 0) {
						if (isSetBack())
							manageDamage(p, np.NO_FALL_DAMAGE, relia);
						np.NO_FALL_DAMAGE = 0;
					}
				}
			} else if(!p.isOnGround()) {
				Material justUnder = p.getLocation().clone().sub(0, 0.1, 0).getBlock().getType();
				if(justUnder.isSolid() && p.getFallDistance() > 3.0 && !np.isInFight) {
					int ping = p.getPing(), relia = UniversalUtils.parseInPorcent(100 - (ping / 5) + p.getFallDistance());
					boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia,
							"Player not ground with fall damage (FallDistance: " + p.getFallDistance() + "). Block 0.1 below: " + justUnder.getId()
									+ ", DistanceBetweenFromAndTo: " + distance + " (ping: " + ping
									+ "). Warn: " + np.getWarn(this));
					if(mayCancel && isSetBack())
						manageDamage(p, (int) p.getFallDistance(), relia);
				}
			}
		}
	}
	
	private void manageDamage(Player p, int damage, int relia) {
		ConfigAdapter config = Adapter.getAdapter().getConfig();
		p.damage(damage >= p.getHealth() ? (config.getBoolean("cheats.nofall.kill") && config.getDouble("cheats.nofall.kill-reliability") >= relia ? damage : p.getHealth() - 0.5) : p.getHealth());
	}
}
