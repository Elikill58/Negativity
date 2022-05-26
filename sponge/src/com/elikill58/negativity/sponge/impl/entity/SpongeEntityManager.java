package com.elikill58.negativity.sponge.impl.entity;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.golem.IronGolem;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.ThrownPotion;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Entity;

public class SpongeEntityManager {

	public static com.elikill58.negativity.api.entity.@Nullable Player getPlayer(@Nullable Player p){
		return p == null ? null : NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpongePlayer(p)).getPlayer();
	}
	
	public static @Nullable Entity getEntity(org.spongepowered.api.entity.@Nullable Entity e) {
		if(e == null)
			return null;
		if(e.getType().equals(EntityTypes.PLAYER))
			return getPlayer((Player) e);
		else if(e.getType().equals(EntityTypes.IRON_GOLEM))
			return new SpongeIronGolem((IronGolem) e);
		else if(e.getType().getId().contains("ARROW"))
			return new SpongeArrow((Arrow) e);
		else if(e.getType().equals(EntityTypes.SPLASH_POTION))
			return new SpongeSplashPotion((ThrownPotion) e);
		else
			return new SpongeEntity<>(e);
	}

	public static @Nullable CommandSender getExecutor(@Nullable CommandSource src) {
		if(src == null)
			return null;
		if(src instanceof Player)
			return new SpongePlayer((Player) src);
		return new SpongeCommandSender(src);
	}

	public static @Nullable Entity getProjectile(@Nullable ProjectileSource shooter) {
		if(shooter == null)
			return null;
		if(shooter instanceof Player)
			return getPlayer((Player) shooter);
		else if(shooter instanceof org.spongepowered.api.entity.Entity)
			return new SpongeEntity<>((org.spongepowered.api.entity.Entity) shooter);
		return null;
	}
}
