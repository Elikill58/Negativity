package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class SpeedProtocol extends Cheat implements Listener {

	public SpeedProtocol() {
		super("SPEED", false, Material.BEACON, true, true, "speed", "speedhack");
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		Location from = e.getFrom().clone(), to = e.getTo().clone();
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SPONGE)
				|| p.getEntityId() == 100 || p.getVehicle() != null || p.getAllowFlight() || from.getY() > to.getY()
				|| p.getWalkSpeed() > 2.0F || p.getFlySpeed() > 3.0F || p.hasPotionEffect(PotionEffectType.SPEED)
				|| p.isInsideVehicle() || np.hasElytra() || hasEnderDragonAround(p) || p.getItemInHand().getType().name().contains("TRIDENT"))
			return;
		if (np.BYPASS_SPEED != 0) {
			np.BYPASS_SPEED--;
			return;
		}
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().name().contains("PACKED_ICE")
				&& np.hasOtherThan(p.getLocation().add(0, 1, 0).getBlock().getRelative(BlockFace.UP).getLocation(),
						"TRAPDOOR"))
			return;
		double y = to.toVector().clone().setY(0).distance(from.toVector().clone().setY(0));
		boolean mayCancel = false;
		if (p.isOnGround() && y >= 0.75D) {
			ReportType type = ReportType.WARNING;
			if (np.getWarn(this) > 7)
				type = ReportType.VIOLATION;
			mayCancel = SpigotNegativity.alertMod(type, p, this, Utils.parseInPorcent(y * 100 * 2),
					"Player in ground. WalkSpeed: " + p.getWalkSpeed() + " Distance between from/to location: " + y,
					"Distance Last/New position: " + y + "\n(With same Y)\nPlayer on ground", "Distance Last-New position: " + y);

		} else if (!p.isOnGround() && y >= 0.85D) {
			ReportType type = ReportType.WARNING;
			if (np.getWarn(this) > 7)
				type = ReportType.VIOLATION;
			mayCancel = SpigotNegativity.alertMod(type, p, this, Utils.parseInPorcent(y * 100 * 2),
					"Player NOT in ground. WalkSpeed: " + p.getWalkSpeed() + " Distance between from/to location: " + y,
					"Distance Last/New position: " + y + "\n(With same Y)\nPlayer jumping", "Distance Last-New position: " + y);
		}
		if (isSetBack() && mayCancel)
			e.setCancelled(true);
	}
	
	private boolean hasEnderDragonAround(Player p) {
		for(Entity et : p.getWorld().getEntities())
			if(et.getType().equals(EntityType.ENDER_DRAGON) && et.getLocation().distance(p.getLocation()) < 15)
				return true;
		return false;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player)
			SpigotNegativityPlayer.getNegativityPlayer((Player) e.getEntity()).BYPASS_SPEED = 3;
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
