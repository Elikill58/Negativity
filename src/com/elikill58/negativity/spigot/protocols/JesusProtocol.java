package com.elikill58.negativity.spigot.protocols;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
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

public class JesusProtocol extends Cheat implements Listener {

	private final Material WATER = Utils.getMaterialWith1_15_Compatibility("STATIONARY_WATER", "LEGACY_STATIONARY_WATER"),
			LILY = Utils.getMaterialWith1_15_Compatibility("WATER_LILY", "LEGACY_WATER_LILY");
	
	public JesusProtocol() {
		super(CheatKeys.JESUS, false, Material.WATER_BUCKET, CheatCategory.MOVEMENT, true, "waterwalk", "water", "water walk");
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
		Material m = loc.getBlock().getType(), under = loc.clone().add(0, -1, 0).getBlock().getType();
		boolean isInWater = m.equals(WATER);
		boolean isOnWater = under.equals(WATER);
		if (p.getVehicle() instanceof Boat)
			return;
		if (!isInWater && isOnWater && !hasBoatAroundHim(loc)) {
			if (!np.hasOtherThan(loc.clone().subtract(0, 1, 0), WATER)
					&& !p.getLocation().getBlock().getType().equals(LILY)) {
				if(hasWaterLily(loc.clone().subtract(0, 1, 0)))
					return;
				for (int u = 0; u < 360; u += 3) {
					Location flameloc = loc.clone().subtract(0, 1, 0);
					flameloc.setZ(flameloc.getZ() + Math.cos(u) * 3);
					flameloc.setX(flameloc.getX() + Math.sin(u) * 3);
					if (!flameloc.getBlock().getType().equals(WATER)) {
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
				boolean mayCancel = SpigotNegativity.alertMod(type, p, this, UniversalUtils.parseInPorcent(reliability), "Warn for Jesus: " + np.getWarn(this) + " (Stationary_water aroud him) Diff: " + dif + " and ping: "
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
		
		int i = (np.jesusState ? 1 : 2);
		if(!np.jesusLastY.containsKey(p.getName() + "-" + i))
			np.jesusLastY.put(p.getName() + "-" + i, 0.0);
		
		if(d == np.jesusLastY.get(p.getName() + "-" + i) && !p.getLocation().getBlock().getType().name().contains("WATER")) {
			Location dessous = p.getLocation().clone().subtract(0, 1, 0);
			if(dessous.getBlock().getType().equals(WATER) && !np.hasOtherThan(dessous, WATER)) {
				if(!(np.has(p.getLocation().clone(), LILY) || p.getLocation().getBlock().getType().equals(LILY))) {
					boolean mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent((d + 5) * 10), "Warn for Jesus: " + np.getWarn(this) + " (Stationary_water aroud him) Difference between 2 y: " + d + " (other: " + np.jesusLastY.get(p.getName() + "-" + (np.jesusState ? 2 : 1)) + ") and ping: " + Utils.getPing(p));
					if(isSetBack() && mayCancel)
						p.teleport(p.getLocation().subtract(0, 1, 0));
				}
			}
		}
		
		np.jesusLastY.put(p.getName() + "-" + i, d);
		np.jesusState = !np.jesusState;
	}
	
	@EventHandler
	public void onPlayerMoveNew(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (p.getVehicle() != null || p.isFlying())
			return;
		if(p.hasPotionEffect(PotionEffectType.SPEED))
			return;

		Location from = e.getFrom(), to = e.getTo();
		double distance = to.distance(from) - Math.abs(from.getY() - to.getY());
		Block block = p.getLocation().getBlock();
		Location loc = p.getLocation();
		Location upperLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
		Location underLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ());
		float distanceFall = p.getFallDistance();
		if (block.isLiquid() && underLoc.getBlock().isLiquid() && distanceFall < 1 && !upperLoc.getBlock().isLiquid() && !np.hasOtherThan(underLoc, "WATER")) {
			if (distance > p.getWalkSpeed() && !hasWaterLily(loc) && !hasWaterLily(upperLoc)) {
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.JESUS), 98, "In water, distance: " + distance,
						new CheatHover("main", "%distance%", distance));
				if(isSetBack() && mayCancel)
					p.teleport(p.getLocation().subtract(0, 1, 0));
			}
		}
	}
	
	private boolean hasWaterLily(Location loc) {
		int fX = loc.getBlockX(), fY = loc.getBlockY(), fZ = loc.getBlockZ();
		for (int y = (fY - 1); y != (fY + 2); y++)
			for (int x = (fX - 2); x != (fX + 3); x++)
				for (int z = (fZ - 2); z != (fZ + 3); z++)
					if(loc.getWorld().getBlockAt(x, y, z).getType().equals(LILY))
						return true;
		return false;
	}
	
	public boolean hasBoatAroundHim(Location loc) {
		World world = loc.getWorld();
		if (world == null) {
			return false;
		}

		List<Entity> entities = world.getEntities();
		for(Entity entity : entities) {
			Location l = entity.getLocation();
			if (entity instanceof Boat && l.distance(loc) < 2)
				return true;
		}
		return false;
	}
}
