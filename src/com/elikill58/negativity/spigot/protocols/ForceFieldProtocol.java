package com.elikill58.negativity.spigot.protocols;

import java.text.NumberFormat;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.elikill58.negativity.spigot.FakePlayer;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;

@SuppressWarnings("deprecation")
public class ForceFieldProtocol implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player))
			return;
		Player p = (Player) e.getDamager();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(Cheat.FORCEFIELD))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		double dis = e.getEntity().getLocation().distance(p.getLocation());
		if (dis > (Adapter.getAdapter().getDoubleInConfig("cheats.forcefield.reach")
				+ (p.getGameMode().equals(GameMode.CREATIVE) ? 1 : 0))
				&& !p.getItemInHand().getType().equals(Material.BOW)) {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumIntegerDigits(2);
			np.addWarn(Cheat.FORCEFIELD);
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.FORCEFIELD,
					Utils.parseInPorcent(dis * 2 * 10),
					"Big distance with: " + e.getEntity().getType().name().toLowerCase() + ". Exact distance: " + dis
							+ ". Ping: " + Utils.getPing(p),
					"Distance with " + e.getEntity().getType().getName() + ": " + nf.format(dis));
			if (Cheat.FORCEFIELD.isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void event(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(Cheat.FORCEFIELD))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (!e.getAction().equals(Action.LEFT_CLICK_AIR))
			return;
		Location ploc = p.getLocation(), eyeloc = p.getEyeLocation();
		FakePlayer c = null;
		double distanceWithPlayer = 500, distanceWithEye = 500;
		for (FakePlayer temp : np.FAKE_PLAYER) {
			Location cloc = temp.getLocation();
			double nextDistanceWithPlayer = ploc.distance(cloc), nextDistanceWithEye = eyeloc.distance(cloc);
			if (nextDistanceWithPlayer < distanceWithPlayer && nextDistanceWithPlayer < 10) {
				distanceWithPlayer = nextDistanceWithPlayer;
				c = temp;
			} else if (nextDistanceWithEye < distanceWithEye && nextDistanceWithEye < 10) {
				distanceWithEye = nextDistanceWithEye;
				c = temp;
			}
		}
		if (c == null)
			return;

		np.fakePlayerTouched++;
		c.hide(p);
		manageForcefieldForFakeplayer(p, np);
	}

	public static void manageForcefieldForFakeplayer(Player p, SpigotNegativityPlayer np) {
		if (np.fakePlayerTouched < 5)
			return;
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		double rapport = np.fakePlayerTouched / (timeBehindStart / 1000);
		SpigotNegativity.alertMod(rapport > 20 ? ReportType.VIOLATION : ReportType.WARNING, p, Cheat.FORCEFIELD,
				Utils.parseInPorcent(rapport * 10), "Hitting fake entities. " + np.fakePlayerTouched
						+ " entites touch in " + timeBehindStart + " millisecondes",
				np.fakePlayerTouched + " fake players touched in " + timeBehindStart + " ms");
	}
}
