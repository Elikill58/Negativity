package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.support.WorldGuardSupport;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class AntiKnockbackProtocol extends Cheat implements Listener {
	
	public static final DataType<Double> DISTANCE_DAMAGE = new DataType<Double>("distance_damage", "Distance after Damage", () -> new DoubleDataCounter());
	
	public AntiKnockbackProtocol() {
		super(CheatKeys.ANTI_KNOCKBACK, false, Material.STICK, CheatCategory.COMBAT, true, "antikb", "anti-kb", "no-kb", "nokb");
	}

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
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.hasPotionEffect(PotionEffectType.POISON))
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_9)) {
			ItemStack inHand = Utils.getItemInHand(p);
			if(inHand != null && inHand.getType().name().contains("SHIELD"))
				return;
			ItemStack inOffHand = Utils.getItemInOffHand(p);
			if(inOffHand != null && inOffHand.getType().name().contains("SHIELD"))
				return;
		}
		EntityType damagerType = e.getDamager().getType();
		if(damagerType.equals(EntityType.EGG) || damagerType.equals(EntityType.SNOWBALL) || (SpigotNegativity.worldGuardSupport && WorldGuardSupport.isInRegionProtected(p)))
			return;
		if(damagerType.name().contains("TNT") || np.isTargetByIronGolem())
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		final Entity damager = e.getDamager();
		if(damager instanceof Arrow && ((Arrow) damager).getShooter() instanceof Player)
			if(((Player) ((Arrow) damager).getShooter()).equals(p))
				return;
		
		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> {
			if(e.isCancelled())
				return;
			final Location last = p.getLocation().clone();
			if(damager instanceof LivingEntity) { // check if fight living entity
				if(last.clone().add(0, 2, 0).getBlock().getType().isSolid()) { // check for block upper
					Vector vector = ((LivingEntity) damager).getEyeLocation().getDirection();
					Location locBehind = last.clone().add(vector.clone());
					locBehind.setY(last.getY());
					Material typeBehind = locBehind.getBlock().getType();
					if(typeBehind.isSolid())// cannot move
						return;
				}
			}
			p.damage(0D);
			p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.CUSTOM, 0D));
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> {
				Location actual = p.getLocation();
				if(last.getWorld() != actual.getWorld() || p.isDead())
					return;
				double d = last.distance(actual);
				recordData(p.getUniqueId(), DISTANCE_DAMAGE, d);
				int ping = Utils.getPing(p), relia = UniversalUtils.parseInPorcent(100 - d);
				if (d < 0.1 && !actual.getBlock().getType().equals(ItemUtils.WEB) && !p.isSneaking()){
					boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, relia,
							"Distance after damage: " + d + "; Damager: " + e.getDamager().getType().name().toLowerCase() + " Ping: " + ping, hoverMsg("main", "%distance%", d));
					if(isSetBack() && mayCancel)
						p.setVelocity(p.getVelocity().add(new Vector(0, 1, 0)));
				}
			}, 5);
		}, 0);
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> counter = data.getData(DISTANCE_DAMAGE);
		double av = counter.getAverage(), low = counter.getMin();
		String colorAverage = (av < 1 ? (av < 0.5 ? "&c" : "&6") : "&a");
		String colorLow = (low < 1 ? (low < 0.5 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("&6Distance after damage: &7Average: " + colorAverage + String.format("%.2f", av) + "&7, Lower: " + colorLow + String.format("%.2f", low) + " &7(In " + counter.getSize() + " hits)");
	}
}
