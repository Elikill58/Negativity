package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;

public class SpeedHackProtocol extends Cheat implements Listener {

	public SpeedHackProtocol() {
		super("SPEEDHACK", false, Material.BEACON, true, true, "speed");
	}

	@EventHandler (ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		Location from = e.getFrom().clone(), to = e.getTo().clone();
		double y = to.toVector().clone().setY(0).distance(from.toVector().clone().setY(0));
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SPONGE)
				|| p.getEntityId() == 100 || p.getVehicle() != null || p.getAllowFlight() || from.getY() > to.getY()
				|| p.getWalkSpeed() > 2.0F || p.getFlySpeed() > 3.0F || p.hasPotionEffect(PotionEffectType.SPEED) || p.isInsideVehicle())
			return;
		for(ItemStack item : p.getInventory().getArmorContents())
			if(item != null && item.getType().name().contains("ELYTRA"))
				return;
		if(np.BYPASS_SPEED != 0){
			np.BYPASS_SPEED--;
			return;
		}
		boolean mayCancel = false;
		if (p.isOnGround() && y >= 0.75D) {
			ReportType type = ReportType.WARNING;
			if (np.getWarn(this) > 7)
				type = ReportType.VIOLATION;
			mayCancel = SpigotNegativity.alertMod(type, p, this, Utils.parseInPorcent(y * 100 * 2),
					"Player in ground. WalkSpeed: " + p.getWalkSpeed() + " Distance between from/to location: " + y,
					"Distance Last/New position: " + y + "\n(With same Y)\nPlayer on ground");
			
		} else if (!p.isOnGround() && y >= 0.85D) {
			ReportType type = ReportType.WARNING;
			if (np.getWarn(this) > 7)
				type = ReportType.VIOLATION;
			mayCancel = SpigotNegativity.alertMod(type, p, this, Utils.parseInPorcent(y * 100 * 2),
					"Player NOT in ground. WalkSpeed: " + p.getWalkSpeed() + " Distance between from/to location: " + y,
					"Distance Last/New position: " + y + "\n(With same Y)\nPlayer jumping");
		}
		if(isSetBack() && mayCancel)
			e.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player)
			SpigotNegativityPlayer.getNegativityPlayer((Player) e.getEntity()).BYPASS_SPEED = 2;
	}
}
