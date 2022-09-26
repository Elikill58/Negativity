package com.elikill58.negativity.fabric.impl.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;

public class FabricTypeFilter implements TypeFilter<Entity, Entity> {

	private static FabricTypeFilter filter = new FabricTypeFilter();
	public static FabricTypeFilter getFilter() {
		return filter;
	}
	
	@Override
	public Entity downcast(Entity e) {
		return e;
	}

	@Override
	public Class<? extends Entity> getBaseClass() {
		return Entity.class;
	}

}
