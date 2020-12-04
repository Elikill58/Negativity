package com.elikill58.negativity.api.block;

public enum BlockFace {

	NORTH(0, 0, -1),
	EAST(1, 0, 0),
	SOUTH(0, 0, 1),
	WEST(-1, 0, 0),
	UP(0, 1, 0),
	DOWN(0, -1, 0),
	NORTH_EAST(NORTH, EAST),
	NORTH_WEST(NORTH, WEST),
	SOUTH_EAST(SOUTH, EAST),
	SOUTH_WEST(SOUTH, WEST),
	WEST_NORTH_WEST(WEST, NORTH_WEST),
	NORTH_NORTH_WEST(NORTH, NORTH_WEST),
	NORTH_NORTH_EAST(NORTH, NORTH_EAST),
	EAST_NORTH_EAST(EAST, NORTH_EAST),
	EAST_SOUTH_EAST(EAST, SOUTH_EAST),
	SOUTH_SOUTH_EAST(SOUTH, SOUTH_EAST),
	SOUTH_SOUTH_WEST(SOUTH, SOUTH_WEST),
	WEST_SOUTH_WEST(WEST, SOUTH_WEST),
	SELF(0, 0, 0);

	private final int modX;
	private final int modY;
	private final int modZ;

	BlockFace(int modX, int modY, int modZ) {
		this.modX = modX;
		this.modY = modY;
		this.modZ = modZ;
	}

	BlockFace(BlockFace face1, BlockFace face2) {
		this.modX = (face1.getModX() + face2.getModX());
		this.modY = (face1.getModY() + face2.getModY());
		this.modZ = (face1.getModZ() + face2.getModZ());
	}

	public int getModX() {
		return this.modX;
	}

	public int getModY() {
		return this.modY;
	}

	public int getModZ() {
		return this.modZ;
	}

	public BlockFace getOppositeFace() {
		switch (this) {
		case DOWN:
			return SOUTH;
		case EAST_NORTH_EAST:
			return NORTH;
		case EAST:
			return WEST;
		case EAST_SOUTH_EAST:
			return EAST;
		case NORTH:
			return DOWN;
		case NORTH_EAST:
			return UP;
		case NORTH_NORTH_EAST:
			return SOUTH_WEST;
		case NORTH_NORTH_WEST:
			return SOUTH_EAST;
		case NORTH_WEST:
			return NORTH_WEST;
		case SELF:
			return NORTH_EAST;
		case SOUTH:
			return EAST_SOUTH_EAST;
		case SOUTH_EAST:
			return SOUTH_SOUTH_EAST;
		case SOUTH_SOUTH_EAST:
			return SOUTH_SOUTH_WEST;
		case SOUTH_SOUTH_WEST:
			return WEST_SOUTH_WEST;
		case SOUTH_WEST:
			return WEST_NORTH_WEST;
		case UP:
			return NORTH_NORTH_WEST;
		case WEST:
			return NORTH_NORTH_EAST;
		case WEST_NORTH_WEST:
			return EAST_NORTH_EAST;
		case WEST_SOUTH_WEST:
			return SELF;
		}
		return SELF;
	}
}
