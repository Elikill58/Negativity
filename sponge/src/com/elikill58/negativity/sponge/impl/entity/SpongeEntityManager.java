package com.elikill58.negativity.sponge.impl.entity;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.golem.IronGolem;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.Potion;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.projectile.source.ProjectileSource;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Entity;

import net.kyori.adventure.audience.Audience;

public class SpongeEntityManager {

	public static com.elikill58.negativity.api.entity.@Nullable Player getPlayer(@Nullable ServerPlayer p){
		return p == null ? null : NegativityPlayer.getPlayer(p.uniqueId(), () -> new SpongePlayer(p));
	}
	
	public static @Nullable Entity getEntity(org.spongepowered.api.entity.@Nullable Entity e) {
		if(e == null)
			return null;
		if(e instanceof ServerPlayer)
			return getPlayer((ServerPlayer) e);
		else if(e instanceof IronGolem)
			return new SpongeIronGolem((IronGolem) e);
		else if(e instanceof Arrow)
			return new SpongeArrow((Arrow) e);
		else if(e instanceof Potion)
			return new SpongeSplashPotion((Potion) e);
		else
			return new SpongeEntity<>(e);
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static @Nullable CommandSender getExecutor(@Nullable Audience src) {
		if(src == null)
			return null;
		if(src instanceof ServerPlayer)
			return new SpongePlayer((ServerPlayer) src);
		return new SpongeCommandSender(src);
	}

	public static @Nullable Entity getProjectile(@Nullable ProjectileSource shooter) {
		if(shooter == null)
			return null;
		if(shooter instanceof ServerPlayer)
			return getPlayer((ServerPlayer) shooter);
		else if(shooter instanceof org.spongepowered.api.entity.Entity)
			return new SpongeEntity<>((org.spongepowered.api.entity.Entity) shooter);
		return null;
	}
}
