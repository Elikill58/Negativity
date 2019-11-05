package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class StepProtocol extends Cheat implements Listener {

	public StepProtocol() {
		super("STEP", false, Material.BRICK_STAIRS, true, true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if ((System.currentTimeMillis() - np.launchFirework) < 1000 || np.hasElytra() || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double dif = from.getY() - to.getY();
		if (!p.hasPotionEffect(PotionEffectType.JUMP)) {
			if (np.slime_block) {
				if (dif >= 0)
					np.slime_block = false;
			} else {
				Location baseLoc = p.getLocation();
				boolean hasSlimeBlock = false;
				for (int u = 0; u < 360; u += 3) {
					Location flameloc = baseLoc.clone().subtract(0, 1, 0);
					flameloc.setZ(flameloc.getZ() + Math.cos(u) * 3);
					flameloc.setX(flameloc.getX() + Math.sin(u) * 3);
					if (flameloc.getBlock().getType().name().equalsIgnoreCase("SLIME_BLOCK"))
						hasSlimeBlock = true;
				}
				if (hasSlimeBlock)
					np.slime_block = true;
				else {
					int ping = Utils.getPing(p), relia = Utils.parseInPorcent(dif * -500);
					if ((from.getY() - to.getY()) > 0)
						return;
					if (dif < -1.499 && ping < 200) {
						boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, relia, "Warn for Step: "
								+ np.getWarn(this) + ". Move " + dif + "blocks up. ping: " + ping);
						if (isSetBack() && mayCancel)
							e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onSpawn(EntitySpawnEvent e) {
		if(!e.getEntityType().equals(EntityType.FIREWORK))
			return;
		Location loc = e.getLocation();
		for(Player p : Utils.getOnlinePlayers())
			if(p.getLocation().distance(loc) < 2)
				SpigotNegativityPlayer.getNegativityPlayer(p).launchFirework = System.currentTimeMillis();
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
