package com.elikill58.negativity.fabric.impl.inventory.holders;

import java.util.Optional;
import java.util.function.BiFunction;

import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;

import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FabricNegativityHolder extends PlatformHolder implements ScreenHandlerContext {

	private final NegativityHolder holder;
	
	public FabricNegativityHolder(NegativityHolder holder) {
		this.holder = holder;
	}
	
	public NegativityHolder getHolder() {
		return holder;
	}
	
	@Override
	public <T> Optional<T> get(BiFunction<World, BlockPos, T> getter) {
		return Optional.empty();
	}

	@Override
	public PlatformHolder getBasicHolder() {
		return new FabricHolder(this);
	}

}
