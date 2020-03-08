package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.support.WorldGuardSupport;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AntiKnockbackProtocol extends Cheat implements Listener {
	
	public AntiKnockbackProtocol() {
		super(CheatKeys.ANTI_KNOCKBACK, false, Material.STICK, CheatCategory.COMBAT, true, "antikb", "anti-kb", "no-kb", "nokb");
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(final EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player) || e.isCancelled())
			return;
		Player p = (Player) e.getEntity();

		if (p.isInsideVehicle()) {
			// Knockback is not applied to entities riding other entities
			return;
		}

		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.hasPotionEffect(PotionEffectType.POISON))
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_9)) {
			if(p.getItemInHand() != null && p.getItemInHand().getType().name().contains("SHIELD"))
				return;
			try {
				Object itemInOffHand = p.getInventory().getClass().getMethod("getItemInOffHand").invoke(p.getInventory());
				if(itemInOffHand != null && itemInOffHand instanceof ItemStack) {
					if(((ItemStack) itemInOffHand).getType().name().contains("SHIELD"))
						return;
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		EntityType damagerType = e.getDamager().getType();
		if(damagerType.equals(EntityType.EGG) || damagerType.equals(EntityType.SNOWBALL) || (SpigotNegativity.worldGuardSupport && WorldGuardSupport.isInRegionProtected(p)) || e.isCancelled())
			return;
		if(damagerType.name().contains("TNT") || np.isTargetByIronGolem())
			return;
		if(e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player)
			if(((Player) ((Arrow) e.getDamager()).getShooter()).equals(p))
				return;
		
		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {

			@Override
			public void run() {
				if(e.isCancelled())
					return;
				final Location last = p.getLocation();
				p.damage(0D);
				p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.CUSTOM, 0D));
				Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
					@Override
					public void run() {
						Location actual = p.getLocation();
						if(last.getWorld() != actual.getWorld() || p.isDead())
							return;
						double d = last.distance(actual);
						int ping = Utils.getPing(p), relia = UniversalUtils.parseInPorcent(100 - d);
						if (d < 0.1 && !actual.getBlock().getType().equals(Utils.getMaterialWith1_15_Compatibility("WEB", "COBWEB")) && !p.isSneaking()){
							boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.ANTI_KNOCKBACK), relia,
									"Distance after damage: " + d + "; Damager: " + e.getDamager().getType().name().toLowerCase() + " Ping: " + ping, "Distance after damage: " + d);
							if(isSetBack() && mayCancel)
								p.setVelocity(p.getVelocity().add(new Vector(0, 1, 0)));
						}
					}
				}, 5);
			}
		}, 0);
	}
}
