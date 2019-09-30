package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;

public class SpiderProtocol extends Cheat implements Listener {

	public SpiderProtocol() {
		super("SPIDER", false, Utils.getMaterialWith1_13_Compatibility("WEB", "COBWEB"), false, true, "wallhack",
				"wall");
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		Location loc = p.getLocation();
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (p.getFallDistance() != 0 || np.hasElytra())
			return;
		Material playerLocType = loc.getBlock().getType(),
				underPlayer = loc.clone().subtract(0, 1, 0).getBlock().getType(),
				underUnder = loc.clone().subtract(0, 2, 0).getBlock().getType(),
				m3 = loc.clone().add(0, 1, 0).getBlock().getType();
		if (!underPlayer.equals(Material.AIR) || !underUnder.equals(Material.AIR) || playerLocType.equals(Material.VINE)
				|| playerLocType.equals(Material.LADDER) || underPlayer.equals(Material.VINE)
				|| underPlayer.equals(Material.LADDER) || m3.equals(Material.VINE) || m3.equals(Material.LADDER)
				|| !playerLocType.equals(Material.AIR))
			return;
		double y = e.getTo().getY() - e.getFrom().getY(), last = np.lastY;
		np.lastY = y;
		boolean isAris = ((float) y) == p.getWalkSpeed();
		if (((y > 0.499 && y < 0.7) || isAris || last == y) && hasOtherThan(loc, Material.AIR)) {
			if(hasBypassBlockAround(loc))
				return;
			int relia = Utils.parseInPorcent((e.getTo().getY() - e.getFrom().getY()) * 200 + (isAris ? 39 : 0));
			if (SpigotNegativity.alertMod((np.getWarn(this) > 6 ? ReportType.WARNING : ReportType.VIOLATION), p, this,
					relia, "Nothing around him. To > From: " + y + " isAris: " + isAris + " has not stab slairs.")
					&& isSetBack()) {
				Location locc = p.getLocation();
				while (locc.getBlock().getType().equals(Material.AIR))
					locc.subtract(0, 1, 0);
				p.teleport(locc.add(0, 1, 0));
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove2(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		Location loc = p.getLocation();
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		double y = e.getTo().getY() - e.getFrom().getY();
		boolean isAris = ((float) y) == p.getWalkSpeed();
		if (np.lastSpiderLoc != null && np.lastSpiderLoc.getWorld().equals(loc.getWorld()) && y > 0) {
			if(hasBypassBlockAround(loc)) {
				np.lastSpiderLoc = loc;
				return;
			}
			loc.setX(np.lastSpiderLoc.getX());
			loc.setZ(np.lastSpiderLoc.getZ());
			double tempDis = loc.distance(np.lastSpiderLoc);
			if (np.lastSpiderDistance == tempDis && tempDis != 0) {
				int porcent = Utils.parseInPorcent(tempDis * 450);
				if (SpigotNegativity.alertMod(ReportType.WARNING, p, this, porcent, "Nothing around him. To > From: "
						+ y + " isAris: " + isAris + ". Walk on wall with always same y.") && isSetBack()) {
					Location locc = p.getLocation();
					while (locc.getBlock().getType().equals(Material.AIR))
						locc.subtract(0, 1, 0);
					p.teleport(locc.add(0, 1, 0));
				}
			}
			np.lastSpiderDistance = tempDis;
		}
		np.lastSpiderLoc = loc;
	}

	public boolean hasOtherThan(Location loc, Material m) {
		if (!loc.clone().add(0, 0, 1).getBlock().getType().equals(m))
			return true;
		if (!loc.clone().add(1, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.clone().add(-1, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.clone().add(-1, 0, 1).getBlock().getType().equals(m))
			return true;
		return false;
	}
	
	private boolean hasBypassBlockAround(Location loc) {
		for (int u = 0; u < 360; u += 3) {
			Location flameloc = loc.clone();
			flameloc.setZ(flameloc.getZ() + Math.cos(u) * 3);
			flameloc.setX(flameloc.getX() + Math.sin(u) * 3);
			String name = flameloc.clone().getBlock().getType().name(),
					secondname = flameloc.clone().add(0, 1, 0).getBlock().getType().name();
			if (name.contains("SLAB") || name.contains("STAIRS") || secondname.contains("SLAB")
					|| secondname.contains("STAIRS"))
				return true;
		}
		if(loc.getBlock().getType().name().contains("WATER") || loc.clone().subtract(0, 1, 0).getBlock().getType().name().contains("WATER"))
			return true;
		
		return false;
	}
}
