package com.elikill58.negativity.common.protocols;

import java.util.TimerTask;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Arrow;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerDamageByEntityEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.support.WorldGuardSupport;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class AntiKnockback extends Cheat implements Listeners {

	public static final DataType<Double> DISTANCE_DAMAGE = new DataType<Double>("distance_damage",
			"Distance after Damage", () -> new DoubleDataCounter());

	public AntiKnockback() {
		super(CheatKeys.ANTI_KNOCKBACK, false, Materials.STICK, CheatCategory.COMBAT, true, "antikb", "anti-kb",
				"no-kb", "nokb");
	}

	@EventListener
	public void onDamage(PlayerDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		Player p = e.getEntity();

		if (p.isInsideVehicle()) {
			// Knockback is not applied to entities riding other entities
			return;
		}

		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || !checkActive("ticked"))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.hasPotionEffect(PotionEffectType.POISON))
			return;
		if (Version.getVersion().isNewerOrEquals(Version.V1_9)) {
			ItemStack inHand = p.getItemInHand();
			if (inHand != null && inHand.getType().getId().contains("SHIELD"))
				return;
			ItemStack inOffHand = p.getItemInOffHand();
			if (inOffHand != null && inOffHand.getType().getId().contains("SHIELD"))
				return;
		}
		EntityType damagerType = e.getDamager().getType();
		if (damagerType.equals(EntityType.EGG) || damagerType.equals(EntityType.SNOWBALL)
				|| (Negativity.worldGuardSupport && WorldGuardSupport.isInRegionProtected(p)))
			return;
		if (damagerType.name().contains("TNT") || np.isTargetByIronGolem())
			return;
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		final Entity damager = e.getDamager();
		if (damager.getType().equals(EntityType.ARROW) && ((Arrow) damager).getShooter() instanceof Player)
			if (((Player) ((Arrow) damager).getShooter()).equals(p))
				return;

		new java.util.Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (e.isCancelled())
					return;
				final Location last = p.getLocation().clone();
				/*if (damager instanceof LivingEntity) { // check if fight living entity
					if (last.clone().add(0, 2, 0).getBlock().getType().isSolid()) { // check for block upper
						Vector vector = ((LivingEntity) damager).getEyeLocation().getDirection();
						Location locBehind = last.clone().add(vector.clone());
						locBehind.setY(last.getY());
						Material typeBehind = locBehind.getBlock().getType();
						if (typeBehind.isSolid())// cannot move
							return;
					}
				}*/
				p.damage(0D);
				//p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.CUSTOM, 0D));
				new java.util.Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						Location actual = p.getLocation();
						if (last.getWorld() != actual.getWorld() || p.isDead())
							return;
						double d = last.distance(actual);
						recordData(p.getUniqueId(), DISTANCE_DAMAGE, d);
						int ping = p.getPing(), relia = UniversalUtils.parseInPorcent(100 - d);
						if (d < 0.1 && !actual.getBlock().getType().equals(Materials.WEB) && !p.isSneaking()) {
							boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, AntiKnockback.this, relia, "ticked",
									"Distance after damage: " + d + "; Damager: "
											+ e.getDamager().getType().name().toLowerCase() + " Ping: " + ping,
									hoverMsg("main", "%distance%", d));
							if (isSetBack() && mayCancel)
								p.setVelocity(p.getVelocity().add(new Vector(0, 1, 0)));
						}
					}
				}, 250);
			}
		}, 50);
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> counter = data.getData(DISTANCE_DAMAGE);
		double av = counter.getAverage(), low = counter.getMin();
		String colorAverage = (av < 1 ? (av < 0.5 ? "&c" : "&6") : "&a");
		String colorLow = (low < 1 ? (low < 0.5 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("&6Distance after damage: &7Average: " + colorAverage + String.format("%.2f", av)
				+ "&7, Lower: " + colorLow + String.format("%.2f", low) + " &7(In " + counter.getSize() + " hits)");
	}
}
