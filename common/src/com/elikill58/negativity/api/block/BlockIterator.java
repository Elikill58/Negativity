package com.elikill58.negativity.api.block;

import static com.elikill58.negativity.universal.utils.Maths.floor;
import static com.elikill58.negativity.universal.utils.Maths.round;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class BlockIterator implements Iterator<Block> {

	private final World world;
	private final int maxDistance;
	private boolean end = false;
	private Block[] blockQueue = new Block[3];
	private int currentBlock = 0;
	private int currentDistance = 0;
	private int maxDistanceInt;
	private int secondError;
	private int thirdError;
	private int secondStep;
	private int thirdStep;
	private BlockFace mainFace;
	private BlockFace secondFace;
	private BlockFace thirdFace;

	public BlockIterator(World world, Vector start, Vector direction, double yOffset, int maxDistance) {
		this.world = world;
		this.maxDistance = maxDistance;

		Vector startClone = start.clone();

		startClone.setY(startClone.getY() + yOffset);

		this.currentDistance = 0;

		double mainDirection = 0.0D;
		double secondDirection = 0.0D;
		double thirdDirection = 0.0D;

		double mainPosition = 0.0D;
		double secondPosition = 0.0D;
		double thirdPosition = 0.0D;

		Block startBlock = this.world.getBlockAt(floor(startClone.getX()), floor(startClone.getY()),
				floor(startClone.getZ()));
		if (getXLength(direction) > mainDirection) {
			this.mainFace = getXFace(direction);
			mainDirection = getXLength(direction);
			mainPosition = getXPosition(direction, startClone, startBlock);

			this.secondFace = getYFace(direction);
			secondDirection = getYLength(direction);
			secondPosition = getYPosition(direction, startClone, startBlock);

			this.thirdFace = getZFace(direction);
			thirdDirection = getZLength(direction);
			thirdPosition = getZPosition(direction, startClone, startBlock);
		}
		if (getYLength(direction) > mainDirection) {
			this.mainFace = getYFace(direction);
			mainDirection = getYLength(direction);
			mainPosition = getYPosition(direction, startClone, startBlock);

			this.secondFace = getZFace(direction);
			secondDirection = getZLength(direction);
			secondPosition = getZPosition(direction, startClone, startBlock);

			this.thirdFace = getXFace(direction);
			thirdDirection = getXLength(direction);
			thirdPosition = getXPosition(direction, startClone, startBlock);
		}
		if (getZLength(direction) > mainDirection) {
			this.mainFace = getZFace(direction);
			mainDirection = getZLength(direction);
			mainPosition = getZPosition(direction, startClone, startBlock);

			this.secondFace = getXFace(direction);
			secondDirection = getXLength(direction);
			secondPosition = getXPosition(direction, startClone, startBlock);

			this.thirdFace = getYFace(direction);
			thirdDirection = getYLength(direction);
			thirdPosition = getYPosition(direction, startClone, startBlock);
		}
		double d = mainPosition / mainDirection;
		double secondd = secondPosition - secondDirection * d;
		double thirdd = thirdPosition - thirdDirection * d;

		this.secondError = floor(secondd * 1.6777216E7D);
		this.secondStep = round(secondDirection / mainDirection * 1.6777216E7D);
		this.thirdError = floor(thirdd * 1.6777216E7D);
		this.thirdStep = round(thirdDirection / mainDirection * 1.6777216E7D);
		if (this.secondError + this.secondStep <= 0) {
			this.secondError = (-this.secondStep + 1);
		}
		if (this.thirdError + this.thirdStep <= 0) {
			this.thirdError = (-this.thirdStep + 1);
		}
		Block lastBlock = startBlock.getRelative(this.mainFace.getOppositeFace());
		if (this.secondError < 0) {
			this.secondError += 16777216;
			lastBlock = lastBlock.getRelative(this.secondFace.getOppositeFace());
		}
		if (this.thirdError < 0) {
			this.thirdError += 16777216;
			lastBlock = lastBlock.getRelative(this.thirdFace.getOppositeFace());
		}
		this.secondError -= 16777216;
		this.thirdError -= 16777216;

		this.blockQueue[0] = lastBlock;
		this.currentBlock = -1;

		scan();

		for (int cnt = this.currentBlock; cnt >= 0; cnt--) {
			if (blockEquals(this.blockQueue[cnt], startBlock)) {
				this.currentBlock = cnt;
				throw new IllegalStateException("Start block missed in BlockIterator");
			}
		}
		
		this.maxDistanceInt = round(maxDistance / (Math.sqrt(
				mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection)
				/ mainDirection));
	}

	private boolean blockEquals(Block a, Block b) {
		return (a.getX() == b.getX()) && (a.getY() == b.getY()) && (a.getZ() == b.getZ());
	}

	private BlockFace getXFace(Vector direction) {
		return direction.getX() > 0.0D ? BlockFace.EAST : BlockFace.WEST;
	}

	private BlockFace getYFace(Vector direction) {
		return direction.getY() > 0.0D ? BlockFace.UP : BlockFace.DOWN;
	}

	private BlockFace getZFace(Vector direction) {
		return direction.getZ() > 0.0D ? BlockFace.SOUTH : BlockFace.NORTH;
	}

	private double getXLength(Vector direction) {
		return Math.abs(direction.getX());
	}

	private double getYLength(Vector direction) {
		return Math.abs(direction.getY());
	}

	private double getZLength(Vector direction) {
		return Math.abs(direction.getZ());
	}

	private double getPosition(double direction, double position, int blockPosition) {
		return direction > 0.0D ? position - blockPosition : blockPosition + 1 - position;
	}

	private double getXPosition(Vector direction, Vector position, Block block) {
		return getPosition(direction.getX(), position.getX(), block.getX());
	}

	private double getYPosition(Vector direction, Vector position, Block block) {
		return getPosition(direction.getY(), position.getY(), block.getY());
	}

	private double getZPosition(Vector direction, Vector position, Block block) {
		return getPosition(direction.getZ(), position.getZ(), block.getZ());
	}

	public BlockIterator(Location loc, double yOffset, int maxDistance) {
		this(loc.getWorld(), loc.toVector(), loc.getDirection(), yOffset, maxDistance);
	}

	public BlockIterator(Location loc, double yOffset) {
		this(loc.getWorld(), loc.toVector(), loc.getDirection(), yOffset, 10);
	}

	public BlockIterator(Location loc) {
		this(loc, 0.0D);
	}

	public BlockIterator(Entity entity, int maxDistance) {
		this(entity.getLocation(), entity.getEyeHeight(), maxDistance);
	}

	public BlockIterator(Entity entity) {
		this(entity, 0);
	}

	@Override
	public boolean hasNext() {
		scan();
		return this.currentBlock != -1;
	}

	@Override
	public Block next() {
		scan();
		if (this.currentBlock <= -1) {
			throw new NoSuchElementException();
		}
		return this.blockQueue[(this.currentBlock--)];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("[BlockIterator] doesn't support block removal");
	}

	private void scan() {
		if (this.currentBlock >= 0) {
			return;
		}
		if ((this.maxDistance != 0) && (this.currentDistance > this.maxDistanceInt)) {
			this.end = true;
			return;
		}
		if (this.end) {
			return;
		}
		this.currentDistance += 1;

		this.secondError += this.secondStep;
		this.thirdError += this.thirdStep;
		if ((this.secondError > 0) && (this.thirdError > 0)) {
			this.blockQueue[2] = this.blockQueue[0].getRelative(this.mainFace);
			if (this.secondStep * this.thirdError < this.thirdStep * this.secondError) {
				this.blockQueue[1] = this.blockQueue[2].getRelative(this.secondFace);
				this.blockQueue[0] = this.blockQueue[1].getRelative(this.thirdFace);
			} else {
				this.blockQueue[1] = this.blockQueue[2].getRelative(this.thirdFace);
				this.blockQueue[0] = this.blockQueue[1].getRelative(this.secondFace);
			}
			this.thirdError -= 16777216;
			this.secondError -= 16777216;
			this.currentBlock = 2;
			return;
		}
		if (this.secondError > 0) {
			this.blockQueue[1] = this.blockQueue[0].getRelative(this.mainFace);
			this.blockQueue[0] = this.blockQueue[1].getRelative(this.secondFace);
			this.secondError -= 16777216;
			this.currentBlock = 1;
			return;
		}
		if (this.thirdError > 0) {
			this.blockQueue[1] = this.blockQueue[0].getRelative(this.mainFace);
			this.blockQueue[0] = this.blockQueue[1].getRelative(this.thirdFace);
			this.thirdError -= 16777216;
			this.currentBlock = 1;
			return;
		}
		this.blockQueue[0] = this.blockQueue[0].getRelative(this.mainFace);
		this.currentBlock = 0;
	}

}
