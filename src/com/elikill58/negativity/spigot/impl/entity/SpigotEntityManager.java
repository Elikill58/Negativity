package com.elikill58.negativity.spigot.impl.entity;

import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.entity.Entity;

public class SpigotEntityManager {

	public static Entity getEntity(org.bukkit.entity.Entity bukkitEntity) {
		if(bukkitEntity == null)
			return null;
		switch (bukkitEntity.getType()) {
		case PLAYER:
			return new SpigotPlayer((Player) bukkitEntity);
		case IRON_GOLEM:
			return new SpigotIronGolem((IronGolem) bukkitEntity);
		default:
			return new SpigotEntity(bukkitEntity);
		}
	}
}
