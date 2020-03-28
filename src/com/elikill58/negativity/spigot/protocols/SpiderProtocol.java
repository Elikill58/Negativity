package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings("deprecation")
public class SpiderProtocol extends Cheat implements Listener {

	public SpiderProtocol() {
		super(CheatKeys.SPIDER, false, Material.SPIDER_EYE, CheatCategory.MOVEMENT, true, "wallhack",
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
		if (p.getFallDistance() != 0 || np.hasElytra() || p.isFlying() || p.hasPotionEffect(PotionEffectType.JUMP) || !np.hasOtherThan(loc, Material.AIR))
			return;
		if(e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ())
			return;
		Material playerLocType = loc.getBlock().getType(),
				underPlayer = loc.clone().subtract(0, 1, 0).getBlock().getType(),
				underUnder = loc.clone().subtract(0, 2, 0).getBlock().getType(),
				m3 = loc.clone().add(0, 1, 0).getBlock().getType();
		if (!underPlayer.equals(Material.AIR) || !underUnder.equals(Material.AIR) || playerLocType.equals(Material.VINE)
				|| playerLocType.equals(Material.LADDER) || underPlayer.equals(Material.VINE)
				|| underPlayer.equals(Material.LADDER) || m3.equals(Material.VINE) || m3.equals(Material.LADDER)
				|| !playerLocType.equals(Material.AIR) || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;
		double y = e.getTo().getY() - e.getFrom().getY(), last = np.lastYDiff;
		np.lastYDiff = y;
		boolean isAris = ((float) y) == p.getWalkSpeed();
		if (((y > 0.499 && y < 0.7) || isAris || last == y)) {
			if(hasBypassBlockAround(loc))
				return;
			int relia = UniversalUtils.parseInPorcent((e.getTo().getY() - e.getFrom().getY()) * 200 + (isAris ? 39 : 0));
			if (SpigotNegativity.alertMod((np.getWarn(this) > 6 ? ReportType.WARNING : ReportType.VIOLATION), p, this,
					relia, "Nothing around him. To > From: " + y + " isAris: " + isAris + ", has not stab slairs")
					&& isSetBack()) {
				Location locc = p.getLocation();
				while (locc.getBlock().getType().equals(Material.AIR) && locc.getY() > 0)
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
		Location loc = p.getLocation().clone();
		if (!np.ACTIVE_CHEAT.contains(this) || p.isFlying())
			return;

		Material playerLocType = loc.getBlock().getType(),
				underPlayer = loc.clone().subtract(0, 1, 0).getBlock().getType(),
				underUnder = loc.clone().subtract(0, 2, 0).getBlock().getType(),
				m3 = loc.clone().add(0, 1, 0).getBlock().getType();
		if (!underPlayer.equals(Material.AIR) || !underUnder.equals(Material.AIR) || playerLocType.equals(Material.VINE)
				|| playerLocType.equals(Material.LADDER) || underPlayer.equals(Material.VINE)
				|| underPlayer.equals(Material.LADDER) || m3.equals(Material.VINE) || m3.equals(Material.LADDER)
				|| !playerLocType.equals(Material.AIR) || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;
		double y = e.getTo().getY() - e.getFrom().getY();
		if (np.lastSpiderLoc != null && np.lastSpiderLoc.getWorld().equals(loc.getWorld()) && y > 0) {
			double tempDis = loc.getY() - np.lastSpiderLoc.getY();
			if (np.lastSpiderDistance == tempDis && tempDis != 0) {
				np.SPIDER_SAME_DIST++;
				if(np.SPIDER_SAME_DIST > 1) {
					int porcent = UniversalUtils.parseInPorcent(tempDis * 400 + np.SPIDER_SAME_DIST);
					if (SpigotNegativity.alertMod(ReportType.WARNING, p, this, porcent, "Nothing around him. To > From: "
							+ y + ". Walk on wall with always same y.") && isSetBack()) {
						Location locc = p.getLocation();
						while (locc.getBlock().getType().equals(Material.AIR))
							locc.subtract(0, 1, 0);
						p.teleport(locc.add(0, 1, 0));
					}
				}
			} else
				np.SPIDER_SAME_DIST = 0;
			np.lastSpiderDistance = tempDis;
		}
		np.lastSpiderLoc = loc;
	}
	
	private boolean hasBypassBlockAround(Location loc) {
		if(hasOtherThan(loc, "SLAB") || hasOtherThan(loc, "STAIRS"))
			return true;
		loc = loc.clone().subtract(0, 1, 0);
		if(hasOtherThan(loc, "SLAB") || hasOtherThan(loc, "STAIRS"))
			return true;
		if(loc.getBlock().getType().name().contains("WATER") || loc.clone().subtract(0, 1, 0).getBlock().getType().name().contains("WATER"))
			return true;
		return false;
	}

	public boolean hasOtherThan(Location loc, String m) {
		if (!loc.clone().add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.clone().add(1, 0, -1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.clone().add(-1, 0, -1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.clone().add(-1, 0, 1).getBlock().getType().name().contains(m))
			return true;
		return false;
	}
}
