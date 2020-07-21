package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.LocationUtils;
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
		if(p.getAllowFlight() || p.hasElytra() || p.isInsideVehicle() || p.hasPotionEffect(PotionEffectType.SPEED))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double distance = to.toVector().distance(from.toVector());
		Location locDown = p.getLocation().clone().getBlock().getRelative(BlockFace.DOWN).getLocation();
		double motionY = from.getY() - to.getY();
		if(locDown.getBlock().getType().equals(Materials.AIR)
				&& !LocationUtils.hasMaterialsAround(locDown, "STAIRS")) {
			if ((motionY > p.getWalkSpeed() && p.getFallDistance() == 0)
					|| (motionY > (p.getWalkSpeed() / 2) && p.isOnGround() && p.getWalkSpeed() > p.getFallDistance())) {
				int porcent = UniversalUtils.parseInPorcent(900 * motionY);
				Negativity.alertMod(ReportType.WARNING, p, this, porcent, "New NoFall - Player on ground. motionY: " + motionY + ", walkSpeed: " + p.getWalkSpeed()
						+ ", onGround: " + p.isOnGround() + ", fallDistance: " + p.getFallDistance(), new Cheat.CheatHover.Literal("MotionY (on ground): " + motionY));
			}
		}
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
