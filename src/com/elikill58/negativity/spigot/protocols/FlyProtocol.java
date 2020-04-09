package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FlyProtocol extends Cheat implements Listener {

	public FlyProtocol() {
		super(CheatKeys.FLY, true, Utils.getMaterialWith1_15_Compatibility("FIREWORK", "LEGACY_FIREWORK"), CheatCategory.MOVEMENT, true, "flyhack");
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this) || e.isCancelled())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(np.hasElytra() || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;

		if (p.hasPotionEffect(PotionEffectType.SPEED)) {
			int speed = 0;
			for (PotionEffect pe : p.getActivePotionEffects())
				if (pe.getType().equals(PotionEffectType.SPEED))
					speed = speed + pe.getAmplifier() + 1;
			if (speed > 40)
				return;
		}
		if (p.getVehicle() != null || p.getAllowFlight() || p.getEntityId() == 100)
			return;
		boolean mayCancel = false;
		double i = e.getTo().toVector().distance(e.getFrom().toVector());
		if(!(p.isSprinting() && (e.getTo().getY() - e.getFrom().getY()) > 0)
				&& p.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)
				&& p.getLocation().subtract(0, 2, 0).getBlock().getType().equals(Material.AIR)) {
			if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SPONGE)) {
				if ((p.getFallDistance() == 0.0F)
						&& (p.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR))
						&& i > 1.25D && !p.isOnGround()) {
					if (np.getWarn(this) > 5)
						mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, this,
								UniversalUtils.parseInPorcent((int) i * 50),
								"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this));
					else
						mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this,
								UniversalUtils.parseInPorcent((int) i * 50),
								"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this));
					if (isSetBack() && mayCancel) {
						Utils.teleportPlayerOnGround(p);
					}
				}
			}
	
			if (!np.hasOtherThanExtended(p.getLocation(), Material.AIR)
					&& !np.hasOtherThanExtended(p.getLocation().add(0, -1, 0), Material.AIR)
					&& !np.hasOtherThanExtended(p.getLocation().add(0, -2, 0), Material.AIR)
					&& e.getFrom().getY() <= e.getTo().getY()) {
				double d = e.getTo().getY() - e.getFrom().getY();
				int  nb = getNbAirBlockDown(np), porcent = UniversalUtils.parseInPorcent(nb * 15 + d);
				if(np.hasOtherThan(p.getLocation().add(0, -3, 0), Material.AIR))
					porcent = UniversalUtils.parseInPorcent(porcent - 15);
				mayCancel = SpigotNegativity.alertMod(
						np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this, porcent,
						"Player not in ground (" + nb + " air blocks down), distance Y: " + d + ". Warn for fly: " + np.getWarn(this));
			}
		}
		Location to = e.getTo().clone();
		to.setY(e.getFrom().getY());
		double distanceWithoutY = to.distance(e.getFrom());
		if(distanceWithoutY == i && !p.isOnGround() && i != 0 && p.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
			mayCancel = SpigotNegativity.alertMod(
					np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this, 98,
					"Player not in ground but not moving Y. DistanceWithoutY: " + distanceWithoutY);
		}
		if (isSetBack() && mayCancel) {
			Utils.teleportPlayerOnGround(p);
		}
	}
	
	private int getNbAirBlockDown(SpigotNegativityPlayer np) {
		Location loc = np.getPlayer().getLocation();
		int i = 0;
		while (!np.hasOtherThanExtended(loc, Material.AIR) && i < 20) {
			loc.subtract(0, 1, 0);
			i++;
		}
		return i;
	}
	
	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
