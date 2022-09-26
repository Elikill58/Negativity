package com.elikill58.negativity.fabric.impl.inventory.holders;

import java.util.Optional;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.inventory.NegativityHolder;

import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FabricNegativityHolder extends NegativityHolder implements ScreenHandlerContext {

	private final NegativityHolder holder;
	
	public FabricNegativityHolder(@Nullable NegativityHolder holder) {
		this.holder = holder == null ? this : holder;
	}
	
	@Override
	public <T> Optional<T> get(BiFunction<World, BlockPos, T> getter) {
		return Optional.empty();
	}

	@Override
	public NegativityHolder getBasicHolder() {
		return this.holder;
	}

}
