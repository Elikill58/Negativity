package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoWebProtocol extends Cheat implements Listener {

	public static final Material WEB = Utils.getMaterialWith1_15_Compatibility("WEB", "COBWEB");
	private static final double MAX = 0.7421028493192875;
	
	public NoWebProtocol() {
		super(CheatKeys.NO_WEB, false, WEB, CheatCategory.MOVEMENT, true, "no web");
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.isFlying() || p.hasPotionEffect(PotionEffectType.SPEED) || p.getFallDistance() > 1)
			return;
		Location l = p.getLocation();
		double distance = e.getTo().distance(e.getFrom());
		if (!(distance > MAX)) {
			Block under = new Location(p.getWorld(), l.getX(), l.getY(), l.getZ()).getBlock();
			if (under.getType() == WEB && distance > 0.13716039608514914) {
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 500), "Distance: " + distance + ", fallDistance: " + p.getFallDistance(), "", "Distance: " + distance);
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			}
		}
	}
}
