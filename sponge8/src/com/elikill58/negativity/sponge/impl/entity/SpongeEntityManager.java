package com.elikill58.negativity.sponge.impl.entity;

import org.spongepowered.api.entity.living.golem.IronGolem;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.projectile.source.ProjectileSource;
import org.spongepowered.api.util.Nameable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Entity;

import net.kyori.adventure.audience.Audience;

public class SpongeEntityManager {

	public static com.elikill58.negativity.api.entity.Player getPlayer(ServerPlayer p){
		return NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpongePlayer(p)).getPlayer();
	}
	
	public static Entity getEntity(org.spongepowered.api.entity.Entity e) {
		if(e == null)
			return null;
		if(e instanceof ServerPlayer)
			return getPlayer((ServerPlayer) e);
		else if(e instanceof IronGolem)
			return new SpongeIronGolem((IronGolem) e);
		else if(e instanceof Arrow)
			return new SpongeArrow((Arrow) e);
		else
			return new SpongeEntity(e);
	}

	public static CommandSender getExecutor(Object src) {
		if(src == null)
			return null;
		if(src instanceof ServerPlayer)
			return new SpongePlayer((ServerPlayer) src);
		if (src instanceof Audience && src instanceof Nameable)
			return new SpongeCommandSender((Audience) src);
		throw new RuntimeException("Could not find appropriate CommandSender implementation for " + src);
	}

	public static Entity getProjectile(ProjectileSource shooter) {
		if(shooter == null)
			return null;
		if(shooter instanceof ServerPlayer)
			return getPlayer((ServerPlayer) shooter);
		else if(shooter instanceof org.spongepowered.api.entity.Entity)
			return new SpongeEntity((org.spongepowered.api.entity.Entity) shooter);
		return null;
	}
}
