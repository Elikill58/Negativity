package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;

public class JesusProtocol implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(Cheat.JESUS))
			return;
		Location loc = p.getLocation();
		Material m = loc.getBlock().getType(), under = loc.clone().add(0, -1, 0).getBlock().getType();
		if (m.equals(Material.STATIONARY_WATER))
			np.isInWater = true;
		else
			np.isInWater = false;
		if (under.equals(Material.STATIONARY_WATER))
			np.isOnWater = true;
		else
			np.isOnWater = false;
		Entity vehicle = p.getVehicle();
		if(vehicle != null)
			if(vehicle instanceof Boat)
				return;
		if (!np.isInWater && np.isOnWater && !hasBoatAroundHim(loc)) {
			if (!np.hasOtherThan(loc.clone().subtract(0, 1, 0), Material.STATIONARY_WATER)
					&& !p.getLocation().getBlock().getType().equals(Material.WATER_LILY)) {
				boolean has = false, hasWaterLily = hasWaterLily(loc.clone().subtract(0, 1, 0));
				for (int u = 0; u < 360; u += 3) {
					Location flameloc = loc.clone().subtract(0, 1, 0);
					flameloc.setZ(flameloc.getZ() + Math.cos(u) * 3);
					flameloc.setX(flameloc.getX() + Math.sin(u) * 3);
					if (!flameloc.getBlock().getType().equals(Material.STATIONARY_WATER)) {
						has = true;
						if (flameloc.getBlock().getType().equals(Material.WATER_LILY))
							hasWaterLily = true;
					}
				}
				if(hasWaterLily || has)
					return;
				double dif = e.getFrom().getY() - e.getTo().getY();
				double reliability = 0;
				boolean isCheating = true;
				ReportType type = ReportType.VIOLATION;
				if(dif < 0.0005 && dif > 0.00000005)
					reliability = dif * 10000000 - 1;
				else if(dif < 0.1 && dif > 0.08)
					reliability = dif * 1000;
				else if(dif == 0.5){
					reliability = 50;
					type = ReportType.WARNING;
				} else if(dif < 0.30001 && dif > 0.3000)
					reliability = dif * 100 * 2.5;
				else if(dif < 0.002 && dif > -0.002 && dif != 0.0)
					reliability = Math.abs(dif * 5000);
				else if(dif == 0.0)
					reliability = 95;
				else isCheating = false;
				if(isCheating){
					np.addWarn(Cheat.JESUS);
					boolean mayCancel = SpigotNegativity.alertMod(type, p, Cheat.JESUS, Utils.parseInPorcent(reliability), "Warn for Jesus: " + np.getWarn(Cheat.JESUS) + " (Stationary_water aroud him) Diff: " + dif + " and ping: "
										+ Utils.getPing(p));
					if(Cheat.JESUS.isSetBack() && mayCancel)
						p.teleport(p.getLocation().subtract(0, 1, 0));
				}
			}
		}
	}
	
	private boolean hasWaterLily(Location loc) {
		boolean hasWaterLily = false;
		int fX = loc.getBlockX(), fY = loc.getBlockY(), fZ = loc.getBlockZ();
		for (int y = (fY - 1); y != (fY + 2); y++)
			for (int x = (fX - 2); x != (fX + 3); x++)
				for (int z = (fZ - 2); z != (fZ + 3); z++)
					if(loc.getWorld().getBlockAt(x, y, z).getType().equals(Material.WATER_LILY))
						hasWaterLily = true;
		return hasWaterLily;
	}
	
	public boolean hasBoatAroundHim(Location loc) {
		for(Player p : Utils.getOnlinePlayers()) {
			Location l = p .getLocation();
			if(l.getWorld().equals(loc.getWorld()))
				if(l.distance(loc) < 2)
					return true;
		}
		return false;
	}
}
