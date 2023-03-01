package com.elikill58.negativity.minestom.impl.entity;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Entity;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;

public class MinestomEntityManager {

	public static com.elikill58.negativity.api.entity.@Nullable Player getPlayer(@Nullable Player p){
		return p == null ? null : NegativityPlayer.getPlayer(p.getUuid(), () -> new MinestomPlayer(p));
	}
	
	public static @Nullable Entity getEntity(net.minestom.server.entity.Entity e) {
		if(e == null)
			return null;
		if(e.getEntityType().equals(EntityType.PLAYER))
			return getPlayer((Player) e);
		else if(e.getEntityType().equals(EntityType.IRON_GOLEM))
			return new MinestomIronGolem(e);
		else if(e.getEntityType().equals(EntityType.ARROW))
			return new MinestomArrow(e);
		else if(e.getEntityType().equals(EntityType.POTION))
			return new MinestomSplashPotion(e);
		else
			return new MinestomEntity<>(e);
	}

	public static @Nullable CommandSender getExecutor(net.minestom.server.command.CommandSender src) {
		if(src == null)
			return null;
		if (src instanceof Player) {
			return new MinestomPlayer((Player) src);
		}
		return new MinestomCommandSender(src);
	}

	public static @Nullable Entity getProjectile(net.minestom.server.entity.Entity shooter) {
		if(shooter == null)
			return null;
		if (shooter instanceof Player)
			return getPlayer((Player) shooter);
		return new MinestomEntity<>((net.minestom.server.entity.Entity) shooter);
	}
}
