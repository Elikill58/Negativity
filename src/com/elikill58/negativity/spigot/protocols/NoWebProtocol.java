package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.spigot.utils.ItemUtils.WEB;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.blocks.SpigotLocation;
import com.elikill58.negativity.spigot.blocks.SpigotWorld;
import com.elikill58.negativity.spigot.listeners.NegativityPlayerMoveEvent;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoWebProtocol extends Cheat implements Listener {

	public static final double MAX = 0.7421028493192875;
	
	public NoWebProtocol() {
		super(CheatKeys.NO_WEB, false, WEB, CheatCategory.MOVEMENT, true, "no web");
	}

	@EventHandler
	public void onPlayerMove(NegativityPlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.isFlying() || p.hasPotionEffect(PotionEffectType.SPEED) || p.getFallDistance() > 1)
			return;
		SpigotLocation from = e.getFrom(), to = e.getFrom();
		double distance = to.distance(from);
		//if (!(distance > MAX)) {
			Block under = new SpigotLocation(SpigotWorld.getWorld(p.getWorld()), (from.getX() + to.getX()) / 2, ((from.getY() + to.getY()) / 2) - 1, (from.getZ() + to.getZ()) / 2).getBlock();
			if (under.getType() == WEB && distance > (p.getWalkSpeed() * 0.17)) { //&& distance > 0.13716039608514914) {
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 500), "Distance: " + distance + ", fallDistance: " + p.getFallDistance() + ", walkSpeed: " + p.getWalkSpeed());
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			}
		//}
	}
}
