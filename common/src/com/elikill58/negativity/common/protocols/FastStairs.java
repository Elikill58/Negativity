package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastStairs extends Cheat {

	public FastStairs() {
		super(CheatKeys.FAST_STAIRS, CheatCategory.MOVEMENT, Materials.BRICK_STAIRS);
	}

	@Check(name = "distance", description = "Check distance", conditions = { CheckConditions.SURVIVAL })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		String blockName = e.getTo().clone().sub(0, 0.0001, 0).getBlock().getType().getId();
		if (!blockName.contains("STAIRS") || p.hasPotionEffect(PotionEffectType.SPEED)) {
			np.doubles.remove(getKey(), "distance");
			return;
		}
		Location from = e.getFrom().clone();
		from.setY(e.getTo().getY());
		double distance = from.distance(e.getTo()), lastDistance = np.doubles.get(getKey(), "distance", 0.0);
		if (distance > 0.452 && lastDistance > distance && p.getFallDistance() == 0) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(distance * 140), "distance", "No fall damage. Block: " + blockName
							+ ", distance: " + distance + ", lastDistance: " + lastDistance,
					hoverMsg("main", "%distance%", String.format("%.2f", distance)));
			if (mayCancel && isSetBack())
				e.setCancelled(true);
		}
		np.doubles.set(getKey(), "distance", distance);
	}
}
