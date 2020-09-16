package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
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
import com.elikill58.negativity.universal.support.EssentialsSupport;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoFall extends Cheat implements Listeners {

	public NoFall() {
		super(CheatKeys.NO_FALL, true, Materials.YELLOW_WOOL, CheatCategory.MOVEMENT, true);
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
		Block b = p.getLocation().getBlock();
		Location locDown = b.getRelative(BlockFace.DOWN).getLocation();
		Location locUp = b.getRelative(BlockFace.UP).getLocation();
		if(checkActive("motion-y")) {
			double motionY = from.getY() - to.getY();
			if(locDown.getBlock().getType().equals(Materials.AIR)
					&& !LocationUtils.hasMaterialsAround(locDown, "STAIRS", "SCAFFOLD") && (motionY > p.getWalkSpeed() && p.getFallDistance() == 0)
						|| (motionY > (p.getWalkSpeed() / 2) && (p.isOnGround() && p.getFallDistance() > 0.2) && p.getWalkSpeed() > p.getFallDistance())) {
				if (locUp.getBlock().getType().getId().contains("WATER"))
					np.useAntiNoFallSystem = true;
				if (!np.useAntiNoFallSystem) {
					int porcent = UniversalUtils.parseInPorcent(900 * motionY);
					Negativity.alertMod(ReportType.WARNING, p, this, porcent, "motion-y", "New NoFall - Player on ground. motionY: " + motionY + ", walkSpeed: " + p.getWalkSpeed()
							+ ", onGround: " + p.isOnGround() + ", fallDistance: " + p.getFallDistance(), new Cheat.CheatHover.Literal("MotionY (on ground): " + motionY));
				}
			} else if(motionY < 0.1)
				np.useAntiNoFallSystem = false;
		}
		if (!(distance == 0.0D || from.getY() < to.getY())) {
			if (p.getFallDistance() == 0.0F && p.getLocation().clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR)) {
				int relia = UniversalUtils.parseInPorcent(distance * 100);
				if (p.isOnGround()) {
					if(checkActive("distance-ground")) {
						if (distance > 0.79D && !(p.getWalkSpeed() > 0.45F && Negativity.essentialsSupport
								&& EssentialsSupport.checkEssentialsSpeedPrecondition(p))) {
							boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia, "distance-ground",
									"Player in ground. FallDamage: " + p.getFallDistance() + ", DistanceBetweenFromAndTo: "
											+ distance);
							if(mayCancel)
								np.NO_FALL_DAMAGE += 1;
						} else if (np.NO_FALL_DAMAGE != 0) {
							if (isSetBack())
								manageDamage(p, np.NO_FALL_DAMAGE, relia);
							np.NO_FALL_DAMAGE = 0;
						}
					}
				} else if(checkActive("distance-no-ground")){
					if (distance > 2D) {
						boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia, "distance-no-ground",
								"Player not in ground no fall Damage. FallDistance: " + p.getFallDistance()
										+ ", DistanceBetweenFromAndTo: " + distance);
						if(mayCancel)
							np.NO_FALL_DAMAGE += 1;
					} else if (np.NO_FALL_DAMAGE != 0) {
						if (isSetBack())
							manageDamage(p, np.NO_FALL_DAMAGE, relia);
						np.NO_FALL_DAMAGE = 0;
					}
				}
			} else if(!p.isOnGround() && checkActive("have-to-ground")) {
				Material justUnder = p.getLocation().clone().sub(0, 0.1, 0).getBlock().getType();
				if(justUnder.isSolid() && p.getFallDistance() > 3.0 && !np.isInFight) {
					int ping = p.getPing(), relia = UniversalUtils.parseInPorcent(100 - (ping / 5) + p.getFallDistance());
					boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia, "have-to-ground",
							"Player not ground with fall damage (FallDistance: " + p.getFallDistance() + "). Block 0.1 below: " + justUnder.getId()
									+ ", DistanceBetweenFromAndTo: " + distance);
					if(mayCancel && isSetBack())
						manageDamage(p, (int) p.getFallDistance(), relia);
				}
			}
		}
	}
	
	private void manageDamage(Player p, int damage, int relia) {
		p.damage(damage >= p.getHealth() ? (getConfig().getBoolean("set_back.kill.active") ? damage : p.getHealth() - 0.5) : damage);
	}
}
