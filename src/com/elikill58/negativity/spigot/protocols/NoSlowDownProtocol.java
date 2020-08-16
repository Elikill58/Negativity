package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoSlowDownProtocol extends Cheat implements Listener {

	public NoSlowDownProtocol() {
		super(CheatKeys.NO_SLOW_DOWN, false, Material.SOUL_SAND, CheatCategory.MOVEMENT, true, "slowdown");
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || p.isFlying())
			return;
		Location loc = p.getLocation();
		Location from = e.getFrom(), to = e.getTo();
		double xSpeed = Math.abs(from.getX() - to.getX());
	    double zSpeed = Math.abs(from.getZ() - to.getZ());
	    double xzSpeed = Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed);
	    np.contentDouble.put("slowdown-eating-distance", xSpeed >= zSpeed ? xSpeed : zSpeed);
	    if (np.contentDouble.get("slowdown-eating-distance") < xzSpeed)
	    	np.contentDouble.put("slowdown-eating-distance", xzSpeed);
		if (!loc.getBlock().getType().equals(Material.SOUL_SAND) || p.hasPotionEffect(PotionEffectType.SPEED))
			return;
		Location fl = from.clone().subtract(to.clone());
		double distance = to.toVector().distance(from.toVector());
		if (distance > 0.2) {
			int ping = Utils.getPing(p), relia = UniversalUtils.parseInPorcent(distance * 400);
			if((from.getY() - to.getY()) < -0.001)
				return;
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, relia,
					"Soul sand under player. Distance from/to : " + distance + ". Ping: " + ping);
			if (isSetBack() && mayCancel)
				e.setTo(from.clone().add(new Location(fl.getWorld(), fl.getX() / 2, fl.getY() / 2, fl.getZ())).add(0, 0.5, 0));
		}
	}

	@EventHandler
	public void FoodCheck(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if(p.isInsideVehicle() || p.isFlying())
			return;
		double dis = np.contentDouble.getOrDefault("slowdown-eating-distance", 0.0);
		if (dis > p.getWalkSpeed() || p.isSprinting()) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.NO_SLOW_DOWN), UniversalUtils.parseInPorcent(dis * 200),
					"Distance while eating: " + dis + ", WalkSpeed: " + p.getWalkSpeed(), hoverMsg("main", "%distance%", String.format("%.2f", dis)));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
}
