package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;

public class FlyProtocol implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(Cheat.FLY))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		for(ItemStack item : p.getInventory().getArmorContents())
			if(item != null && item.getType().name().contains("ELYTRA"))
				return;
		
		if (p.hasPotionEffect(PotionEffectType.SPEED)) {
			int speed = 0;
			for (PotionEffect pe : p.getActivePotionEffects())
				if (pe.getType().equals(PotionEffectType.SPEED))
					speed = speed + pe.getAmplifier() + 1;
			if (speed > 40)
				return;
		}
		double i = e.getTo().toVector().distance(e.getFrom().toVector());
		if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SPONGE)) {
			if (p.getVehicle() != null || p.getAllowFlight() || p.getEntityId() == 100)
				return;
			if ((p.getFallDistance() == 0.0F)
					&& (p.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR))
					&& i > 1.25D && !p.isOnGround()) {
				np.addWarn(Cheat.FLY);
				boolean mayCancel = false;
				if (np.getWarn(Cheat.FLY) > 5)
					mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, Cheat.FLY,
							Utils.parseInPorcent((int) i * 50),
							"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(Cheat.FLY));
				else
					mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.FLY,
							Utils.parseInPorcent((int) i * 50),
							"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(Cheat.FLY));
				if (Cheat.FLY.isSetBack() && mayCancel) {
					Location loc = p.getLocation();
					while (loc.getBlock().getType().equals(Material.AIR)) {
						loc.subtract(0, 1, 0);
					}
					p.teleport(loc.add(0, 1, 0));
				}
			}
		}
	}

}
