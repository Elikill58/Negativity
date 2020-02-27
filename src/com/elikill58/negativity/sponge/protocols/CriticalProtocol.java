package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;

public class CriticalProtocol extends Cheat {

	public CriticalProtocol() {
		super(CheatKeys.CRITICAL, false, ItemTypes.FIRE_CHARGE, CheatCategory.COMBAT, true, "crit", "critic");
	}

	@Listener
	public void onEntityDamageByEntity(DamageEntityEvent e,
									   @First DamageSource damageSource) {
		Player damager = null;
		if (damageSource instanceof EntityDamageSource) {
			Entity entityDamage = ((EntityDamageSource) damageSource).getSource();
			if (entityDamage.getType() == EntityTypes.PLAYER) {
				damager = (Player) entityDamage;
			}
		}
		if(damager == null)
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(damager);
		if (!np.hasDetectionActive(this))
			return;

		if (!damager.gameMode().get().equals(GameModes.SURVIVAL) && !damager.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		
		if (damager.getVehicle().isPresent())
			return;
		
		if(!damager.isOnGround() && !damager.get(Keys.IS_FLYING).orElse(false)) {
			Location<World> loc = damager.getLocation();
			if (loc.getY() % 1.0D == 0.0D && loc.getBlockRelative(Direction.DOWN).getBlock().getType().getProperty(SolidCubeProperty.class).get().getValue()) {
				boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, damager, this, np.getAllWarn(this) > 5 ? 100 : 95, "", "", "");
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			}
		}
	}
}
