package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.NO_SLOW_DOWN;

import java.util.Optional;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
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
import com.elikill58.negativity.common.protocols.data.NoSlowDownData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoSlowDown extends Cheat implements Listeners {

	public NoSlowDown() {
		super(NO_SLOW_DOWN, CheatCategory.MOVEMENT, Materials.SOUL_SAND, NoSlowDownData::new);
	}
	
	@EventListener
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer(); // manage eating distance even when "move" check is disabled
		Location from = e.getFrom(), to = e.getTo();
		double xSpeed = Math.abs(from.getX() - to.getX());
	    double zSpeed = Math.abs(from.getZ() - to.getZ());
	    double xzSpeed = Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed);
	    double maxSpeed = (xSpeed >= zSpeed ? xSpeed : zSpeed);
	    if(maxSpeed < xzSpeed)
	    	maxSpeed = xzSpeed;
	    NegativityPlayer.getNegativityPlayer(p).<NoSlowDownData>getCheckData(this).eatingDistance = maxSpeed;
	}

	@Check(name = "move", description = "Move verif", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np, NoSlowDownData data) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
	    if(Version.getVersion().isNewerOrEquals(Version.V1_16)) {
		    Optional<ItemStack> boots = p.getInventory().getBoots();
		    if(boots.isPresent() && boots.get().hasEnchant(Enchantment.SOUL_SPEED))
		    	return;
	    }
	    if(p.hasPotionEffect(PotionEffectType.SPEED)) {
	    	data.onSoulSand = false;
	    	return;
	    }
	    if(loc.getBlock().getType().equals(Materials.SOUL_SAND)) {
	    	if(data.onSoulSand) {
		    	Location from = e.getFrom(), to = e.getTo();
		    	double distance = to.toVector().distance(from.toVector());
				if (distance > 0.2 && distance >= p.getWalkSpeed()) {
					int relia = UniversalUtils.parseInPorcent(distance * 400);
					if((from.getY() - to.getY()) < -0.001)
						return;
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, relia, "move",
							"Soul sand under player. Distance from/to : " + distance + ". WalkSpeed: " + p.getWalkSpeed() + ", VelY: " + p.getVelocity(),
							hoverMsg("main", "%distance%", String.format("%.2f", distance)));
					if (mayCancel && isSetBack())
						e.setCancelled(true);
				}
	    	}
	    	data.onSoulSand = true;
	    } else
	    	data.onSoulSand = false;
	}

	@Check(name = "eat", description = "Check eat", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE })
	public void foodCheck(PlayerItemConsumeEvent e, NegativityPlayer np, NoSlowDownData data) {
		Player p = e.getPlayer();
		if (data.eatingDistance > p.getWalkSpeed()) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(data.eatingDistance * 200 + (p.isSprinting() ? 20 : 0)), "eat",
					"Distance while eating: " + data.eatingDistance + ", WalkSpeed: " + p.getWalkSpeed() + (p.isSprinting() ? " and Sprinting" : ""), hoverMsg("main", "%distance%", String.format("%.2f", data.eatingDistance)));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
}
