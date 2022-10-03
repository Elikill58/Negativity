package com.elikill58.negativity.api.ray.block;

import java.util.function.Function;

public enum BlockRaySearch {

	POSITION(builder -> !builder.positions.isEmpty()),
	TYPE_SPECIFIC(builder -> builder.neededType != null && builder.neededType.length > 0),
	TYPE_NOT_AIR(builder -> true);

	private final Function<BlockRayBuilder, Boolean> checkIfValid;

	private BlockRaySearch(Function<BlockRayBuilder, Boolean> checkIfValid) {
		this.checkIfValid = checkIfValid;
	}

	public boolean isValid(BlockRayBuilder builder) {
		return checkIfValid.apply(builder);
	}
}
