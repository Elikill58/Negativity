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
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;

public class FlyProtocol extends Cheat implements Listener {

	public FlyProtocol() {
		super("FLY", true, Utils.getMaterialWith1_13_Compatibility("FIREWORK", "LEGACY_FIREWORK"), true, true, "flyhack");
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
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
				np.addWarn(this);
				boolean mayCancel = false;
				if (np.getWarn(this) > 5)
					mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, this,
							Utils.parseInPorcent((int) i * 50),
							"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this));
				else
					mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this,
							Utils.parseInPorcent((int) i * 50),
							"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this));
				if (isSetBack() && mayCancel) {
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
