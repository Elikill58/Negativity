package com.elikill58.negativity.minestom.impl.entity;

import java.util.Collections;
import java.util.List;

import com.elikill58.negativity.api.entity.SplashPotion;
import com.elikill58.negativity.api.potion.PotionEffect;

import net.minestom.server.entity.Entity;

public class MinestomSplashPotion extends MinestomEntity<Entity> implements SplashPotion {
	
	public MinestomSplashPotion(Entity entity) {
		super(entity);
	}

	@Override
	public List<PotionEffect> getEffects() {
		return Collections.emptyList(); // TODO fix splash potion
	}
}
