package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.NegativityPlayerMoveEvent;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastStairsProtocol extends Cheat implements Listener {

	public FastStairsProtocol() {
		super(CheatKeys.FAST_STAIRS, false, ItemUtils.BIRCH_WOOD_STAIRS, CheatCategory.MOVEMENT, true, "stairs");
	}
	
	@EventHandler
	public void onMove(NegativityPlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if(!np.hasDetectionActive(this) || p.isInsideVehicle())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.getFallDistance() != 0)
			return;
		String blockName = e.getTo().clone().subtract(0, 0.0001, 0).getBlock().getType().name();
		if(!blockName.contains("STAIRS") || p.hasPotionEffect(PotionEffectType.SPEED))
			return;
		Location from = e.getFrom().clone();
		from.setY(e.getTo().getY());
		double distance = from.distance(e.getTo()), lastDistance = np.contentDouble.getOrDefault("stairs-distance", 0.0);
		if(distance > 0.452 && lastDistance > distance) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 140),
					"No fall damage. Block: " + blockName + ", distance: " + distance + ", lastDistance: " + lastDistance,
					hoverMsg("main", "%distance%", String.format("%.3f", distance)));
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
		np.contentDouble.put("stairs-distance", distance);
	}
}
