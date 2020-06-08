package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FlyProtocol extends Cheat implements Listener {

	public FlyProtocol() {
		super(CheatKeys.FLY, true, ItemUtils.FIREWORK, CheatCategory.MOVEMENT, true, "flyhack");
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this) || e.isCancelled())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (np.hasElytra() || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;

		if (p.hasPotionEffect(PotionEffectType.SPEED)) {
			int speed = 0;
			for (PotionEffect pe : p.getActivePotionEffects())
				if (pe.getType().equals(PotionEffectType.SPEED))
					speed += pe.getAmplifier() + 1;
			if (speed > 5)
				return;
		}
		if (p.getAllowFlight() || p.getEntityId() == 100
				|| (p.isInsideVehicle() && !p.getVehicle().getType().equals(EntityType.BOAT)))
			return;
		boolean mayCancel = false;
		double y = e.getFrom().getY() - e.getTo().getY();
		if(String.valueOf(y).contains("E") && !String.valueOf(y).equalsIgnoreCase("2.9430145066276694E-4")){
			mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, 100, "Suspicious Y: " + y);
		}
		Location locUnder = p.getLocation().clone().subtract(0, 1, 0),
				locUnderUnder = p.getLocation().clone().subtract(0, 2, 0);
		double i = e.getTo().toVector().distance(e.getFrom().toVector());
		if (!(p.isSprinting() && (e.getTo().getY() - e.getFrom().getY()) > 0)
				&& locUnder.getBlock().getType().equals(Material.AIR)
				&& locUnderUnder.getBlock().getType().equals(Material.AIR)
				&& (p.getFallDistance() == 0.0F || Utils.isInBoat(p))
				&& (p.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR)) && i > 0.8
				&& !p.isOnGround()) {
			mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, UniversalUtils.parseInPorcent((int) i * 50),
					"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this),
					Utils.isInBoat(p) ? hoverMsg("boat") : null);
		}

		if (!np.isUsingSlimeBlock && !LocationUtils.hasOtherThanExtended(p.getLocation(), "AIR")
				&& !LocationUtils.hasOtherThanExtended(p.getLocation().clone().subtract(0, 1, 0), "AIR")
				&& !LocationUtils.hasOtherThanExtended(p.getLocation().clone().subtract(0, 2, 0), "AIR")
				&& (e.getFrom().getY() <= e.getTo().getY() || Utils.isInBoat(p))) {
			double d = e.getTo().getY() - e.getFrom().getY();
			int nb = getNbAirBlockDown(np), porcent = UniversalUtils.parseInPorcent(nb * 15 + d);
			if (LocationUtils.hasOtherThan(p.getLocation().add(0, -3, 0), Material.AIR))
				porcent = UniversalUtils.parseInPorcent(porcent - 15);
			mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, porcent, "Player not in ground (" + nb + " air blocks down), distance Y: " + d
							+ ". Warn for fly: " + np.getWarn(this),
							hoverMsg(Utils.isInBoat(p) ? "boat_air_below" : "air_below", "%nb%", nb));
		}
		Location to = e.getTo().clone();
		to.setY(e.getFrom().getY());
		double distanceWithoutY = to.distance(e.getFrom());
		if (distanceWithoutY == i && !p.isOnGround() && i != 0
				&& p.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR)
				&& !p.getLocation().getBlock().getType().name().contains("WATER") && distanceWithoutY > 0.1) {
			if (np.flyNotMovingY)
				mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, 98, "Player not in ground but not moving Y. DistanceWithoutY: " + distanceWithoutY);
			np.flyNotMovingY = true;
		} else
			np.flyNotMovingY = false;
		if (isSetBack() && mayCancel) {
			Utils.teleportPlayerOnGround(p);
		}
	}

	private int getNbAirBlockDown(SpigotNegativityPlayer np) {
		Location loc = np.getPlayer().getLocation().clone();
		int i = 0;
		while (!LocationUtils.hasOtherThanExtended(loc, "AIR") && i < 20) {
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
