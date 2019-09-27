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
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;

public class JesusProtocol extends Cheat implements Listener {

	public JesusProtocol() {
		super("JESUS", false, Material.WATER_BUCKET, false, true, "waterwalk", "water");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		Location loc = p.getLocation();
		/*Material m = loc.getBlock().getType(), under = loc.clone().add(0, -1, 0).getBlock().getType();
		if (m.equals(Utils.getMaterialWith1_13_Compatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER")))
			np.isInWater = true;
		else
			np.isInWater = false;
		if (under.equals(Utils.getMaterialWith1_13_Compatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER")))
			np.isOnWater = true;
		else
			np.isOnWater = false;*/
		Entity vehicle = p.getVehicle();
		if(vehicle != null)
			if(vehicle instanceof Boat)
				return;
		if (/*!np.isInWater && np.isOnWater &&*/ !hasBoatAroundHim(loc)) {
			if (!np.hasOtherThan(loc.clone().subtract(0, 1, 0), Utils.getMaterialWith1_13_Compatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER"))
					&& !p.getLocation().getBlock().getType().equals(Utils.getMaterialWith1_13_Compatibility("WATER_LILY", "LEGACY_WATER_LILY"))) {
				boolean has = hasWaterLily(loc.clone().subtract(0, 1, 0));
				if(has)
					return;
				for (int u = 0; u < 360; u += 3) {
					Location flameloc = loc.clone().subtract(0, 1, 0);
					flameloc.setZ(flameloc.getZ() + Math.cos(u) * 3);
					flameloc.setX(flameloc.getX() + Math.sin(u) * 3);
					if (!flameloc.getBlock().getType().equals(Utils.getMaterialWith1_13_Compatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER"))) {
						return;
					}
				}
				double dif = e.getFrom().getY() - e.getTo().getY();
				double reliability = 0;
				ReportType type = ReportType.WARNING;
				if(dif < 0.0005 && dif > 0.00000005)
					reliability = dif * 10000000 - 1;
				else if(dif < 0.1 && dif > 0.08)
					reliability = dif * 1000;
				else if(dif == 0.5)
					reliability = 75;
				else if(dif < 0.30001 && dif > 0.3000)
					reliability = dif * 100 * 2.5;
				else if(dif < 0.002 && dif > -0.002 && dif != 0.0)
					reliability = Math.abs(dif * 5000);
				else if(dif == 0.0)
					reliability = 95;
				else return;
				boolean mayCancel = SpigotNegativity.alertMod(type, p, this, Utils.parseInPorcent(reliability), "Warn for Jesus: " + np.getWarn(this) + " (Stationary_water aroud him) Diff: " + dif + " and ping: "
									+ Utils.getPing(p));
				if(isSetBack() && mayCancel)
					p.teleport(p.getLocation().subtract(0, 1, 0));
			}
		}
	}

	@EventHandler
	public void onPlayerMoveLast(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		double d = e.getTo().getY() - e.getFrom().getY();
		if(!np.jesusState.containsKey(p))
			np.jesusState.put(p, true);
		
		int i = (np.jesusState.get(p) ? 1 : 2);
		if(!np.jesusLastY.containsKey(p.getName() + "-" + i))
			np.jesusLastY.put(p.getName() + "-" + i, 0.0);
		
		if(d == np.jesusLastY.get(p.getName() + "-" + i) && !p.getLocation().getBlock().getType().name().contains("WATER")) {
			Location dessous = p.getLocation().clone().subtract(0, 1, 0);
			if(dessous.getBlock().getType().equals(Utils.getMaterialWith1_13_Compatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER")) && !np.hasOtherThan(dessous, Utils.getMaterialWith1_13_Compatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER"))) {
				if(!(np.has(p.getLocation().clone(), Utils.getMaterialWith1_13_Compatibility("WATER_LILY", "LEGACY_WATER_LILY")) || p.getLocation().getBlock().getType().equals(Utils.getMaterialWith1_13_Compatibility("WATER_LILY", "LEGACY_WATER_LILY")))) {
					boolean mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, this, Utils.parseInPorcent((d + 5) * 10), "Warn for Jesus: " + np.getWarn(this) + " (Stationary_water aroud him) Difference between 2 y: " + d + " (other: " + np.jesusLastY.get(p.getName() + "-" + (np.jesusState.get(p) ? 2 : 1)) + ") and ping: " + Utils.getPing(p));
					if(isSetBack() && mayCancel)
						p.teleport(p.getLocation().subtract(0, 1, 0));
				}
			}
		}
		
		np.jesusLastY.put(p.getName() + "-" + i, d);
		np.jesusState.put(p, !np.jesusState.get(p));
	}
	
	private boolean hasWaterLily(Location loc) {
		boolean hasWaterLily = false;
		int fX = loc.getBlockX(), fY = loc.getBlockY(), fZ = loc.getBlockZ();
		for (int y = (fY - 1); y != (fY + 2); y++)
			for (int x = (fX - 2); x != (fX + 3); x++)
				for (int z = (fZ - 2); z != (fZ + 3); z++)
					if(loc.getWorld().getBlockAt(x, y, z).getType().equals(Utils.getMaterialWith1_13_Compatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER")))
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
