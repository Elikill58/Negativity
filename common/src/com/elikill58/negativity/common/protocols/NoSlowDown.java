package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.NO_SLOW_DOWN;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoSlowDown extends Cheat implements Listeners {

	public NoSlowDown() {
		super(NO_SLOW_DOWN, CheatCategory.MOVEMENT, Materials.SOUL_SAND, false, false, "slowdown");
	}

	@Check(name = "move", description = "Move verif", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		Location from = e.getFrom(), to = e.getTo();
		Location fl = from.sub(to);
		double xSpeed = Math.abs(from.getX() - to.getX());
	    double zSpeed = Math.abs(from.getZ() - to.getZ());
	    double xzSpeed = Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed);
	    double maxSpeed = (xSpeed >= zSpeed ? xSpeed : zSpeed);
	    if(maxSpeed < xzSpeed)
	    	maxSpeed = xzSpeed;
	    double distance = to.toVector().distance(from.toVector());
	    np.doubles.set(NO_SLOW_DOWN, "eating-distance", maxSpeed);
	    if(Version.getVersion().isNewerOrEquals(Version.V1_16)) {
		    ItemStack boots = p.getInventory().getBoots();
		    if(boots != null && boots.hasEnchant(Enchantment.SOUL_SPEED))
		    		return;
	    }
	    
	    boolean mayCancel = false;
	    if(loc.getBlock().getType().equals(Materials.SOUL_SAND) && !p.hasPotionEffect(PotionEffectType.SPEED)) {
			if (distance > 0.2 && distance >= p.getWalkSpeed()) {
				int relia = UniversalUtils.parseInPorcent(distance * 400);
				if((from.getY() - to.getY()) < -0.001)
					return;
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, relia, "move",
						"Soul sand under player. Distance from/to : " + distance + ". WalkSpeed: " + p.getWalkSpeed() + ", VelY: " + p.getVelocity().getY(),
						hoverMsg("main", "%distance%", String.format("%.2f", distance)));
			}
	    }
	    /*if(checkActive("walk-speed")) {
			double dif = to.getY() - from.getY();
			if (dif == 0 && distance >= p.getWalkSpeed() && np.ints.get(NO_SLOW_DOWN, "eating", 0) >= 3) {
				mayCancel = Negativity.alertMod(distance >= (p.getWalkSpeed() * 1.5) ? ReportType.VIOLATION : ReportType.WARNING, p,
						this, UniversalUtils.parseInPorcent(distance * 350), "walk-speed", "Distance: " + distance + ", walkSpeed: " + p.getWalkSpeed());
			}
	    }*/
		if (isSetBack() && mayCancel)
			e.setTo(from.clone().add(fl.getX() / 2, (fl.getY() / 2) + 0.5, fl.getZ()));
	}

	@Check(name = "eat", description = "Check eat", conditions = { CheckConditions.NO_ELYTRA })
	public void foodCheck(PlayerItemConsumeEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		double dis = np.doubles.get(NO_SLOW_DOWN, "eating-distance", 0.0);
		if (dis > p.getWalkSpeed() || p.isSprinting()) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(dis * 200), "item",
					"Distance while eating: " + dis + ", WalkSpeed: " + p.getWalkSpeed(), hoverMsg("main", "%distance%", String.format("%.2f", dis)));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
}
