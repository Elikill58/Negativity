package com.elikill58.negativity.spigot.protocols;

import java.text.NumberFormat;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;

@SuppressWarnings("deprecation")
public class ForceFieldProtocol extends Cheat implements Listener {

	private NumberFormat nf = NumberFormat.getInstance();
	
	public ForceFieldProtocol() {
		super(CheatKeys.FORCEFIELD, true, Material.DIAMOND_SWORD, true, true, "ff", "killaura");
		nf.setMaximumIntegerDigits(2);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || e.isCancelled())
			return;
		Player p = (Player) e.getDamager();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this) || e.getEntity() == null)
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		Location tempLoc = e.getEntity().getLocation().clone();
		tempLoc.setY(p.getLocation().getY());
		double dis = tempLoc.distance(p.getLocation());
		if (dis > Adapter.getAdapter().getDoubleInConfig("cheats.forcefield.reach")
				&& !p.getItemInHand().getType().equals(Material.BOW)) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this,
					Utils.parseInPorcent(dis * 2 * 10),
					"Big distance with: " + e.getEntity().getType().name().toLowerCase() + ". Exact distance: " + dis
							+ ". Ping: " + Utils.getPing(p),
					"Distance with " + e.getEntity().getName() + ": " + nf.format(dis), "Distance with " + e.getEntity().getName() + ": " + nf.format(dis));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}

	public static void manageForcefieldForFakeplayer(Player p, SpigotNegativityPlayer np) {
		if (np.fakePlayerTouched < 5)
			return;
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		double rapport = np.fakePlayerTouched / (timeBehindStart / 1000);
		SpigotNegativity.alertMod(rapport > 20 ? ReportType.VIOLATION : ReportType.WARNING, p, Cheat.fromString(CheatKeys.FORCEFIELD),
				Utils.parseInPorcent(rapport * 10), "Hitting fake entities. " + np.fakePlayerTouched
						+ " entites touch in " + timeBehindStart + " millisecondes",
				np.fakePlayerTouched + " fake players touched in " + timeBehindStart + " ms", np.fakePlayerTouched + " fake players touched in " + timeBehindStart + " ms");
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		SpigotNegativityPlayer np = (SpigotNegativityPlayer) p;
		return np.fakePlayerTouched + " fake players touched in " + (System.currentTimeMillis() - np.timeStartFakePlayer) + " ms";
	}
}
