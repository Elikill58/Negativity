package com.elikill58.negativity.sponge.protocols;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.flowpowered.math.vector.Vector3d;

public class AntiKnockbackProtocol extends Cheat {

	public AntiKnockbackProtocol() {
		super("ANTIKNOCKBACK", false, ItemTypes.STICK, false, true, "antikb", "anti-kb", "no-kb");
	}

	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e,
									   @First DamageSource damageSource,
									   @Getter("getTargetEntity") Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}

		if (p.getVehicle().isPresent()) {
			// Knockback is not applied to entities riding other entities
			return;
		}

		if (damageSource.getType() == DamageTypes.FALL) {
			// Falling deals damage but does not necessarily moves the player
			return;
		}

		if (damageSource instanceof EntityDamageSource) {
			EntityDamageSource entityDamageSource = (EntityDamageSource) damageSource;
			EntityType damagingEntityType = entityDamageSource.getSource().getType();
			if (damagingEntityType == EntityTypes.EGG
					|| damagingEntityType == EntityTypes.SNOWBALL
					|| damagingEntityType == EntityTypes.IRON_GOLEM
					|| damagingEntityType.getName().contains("TNT")) {
				return;
			} else if(damagingEntityType.getId().contains("ARROW")) {
				ProjectileSource source = ((Arrow) entityDamageSource.getSource()).getShooter();
				if(source instanceof Player && ((Player) source).equals(p))
					return;
			}
		}

		if(p.getItemInHand(HandTypes.MAIN_HAND).isPresent() && p.getItemInHand(HandTypes.MAIN_HAND).get().getType().getId().toUpperCase().contains("SHIELD"))
			return;
		
		if(p.getItemInHand(HandTypes.OFF_HAND).isPresent() && p.getItemInHand(HandTypes.OFF_HAND).get().getType().getId().toUpperCase().contains("SHIELD"))
			return;
		
		Task.builder().delay(20, TimeUnit.MILLISECONDS).execute(() -> {
			final Location<World> last = p.getLocation();
			p.damage(0D, DamageSources.MAGIC);
			Task.builder().delay(250, TimeUnit.MILLISECONDS).execute(() -> {
				Location<World> actual = p.getLocation();
				double d = last.getPosition().distance(actual.getPosition());
				int ping = Utils.getPing(p), relia = Utils.parseInPorcent(100 - d);
				if (d < 0.1 && actual.getBlockType() != BlockTypes.WEB && !p.get(Keys.IS_SNEAKING).orElse(false)) {
					boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, relia,
							"Distance after damage: " + d + "; Ping: " + ping, "Distance after damage: " + d);
					if (isSetBack() && mayCancel) {
						p.setVelocity(p.getVelocity().add(Vector3d.UP));
					}
				}
			}).submit(SpongeNegativity.getInstance());
		}).submit(SpongeNegativity.getInstance());
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
