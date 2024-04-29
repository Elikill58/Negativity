package com.elikill58.negativity.api.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.impl.server.CompensatedWorld;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.block.BlockRay;
import com.elikill58.negativity.api.ray.block.BlockRayBuilder;
import com.elikill58.negativity.api.ray.block.BlockRayResult;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.logger.Debug;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;

public abstract class AbstractPlayer implements Player {

	protected int protocolVersion = 0;
	protected Version playerVersion;
	protected Location location;
	protected Vector velocity = null;
	protected CompensatedWorld world;
	
	protected void init() {
		this.world = new CompensatedWorld(this);
		
		this.protocolVersion = PlayerVersionManager.getPlayerProtocolVersion(this);
		this.playerVersion = Version.getVersionByProtocolID(getProtocolVersion());
		
		if(location != null)
			location.setWorld(world);
	}

	@Override
	public Version getPlayerVersion() {
		return playerVersion;
	}
	
	@Override
	public void setPlayerVersion(Version version) {
		playerVersion = version;
		protocolVersion = version.getFirstProtocolNumber();
	}

	@Override
	public int getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public void setProtocolVersion(int protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	
	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@Override
	public List<Block> getTargetBlock(int maxDistance) {
		BlockRay ray = new BlockRayBuilder(this).maxDistance(maxDistance).ignoreAir(true).build();
		BlockRayResult result = ray.compile();
		if(!result.getRayResult().isFounded()) {
			Adapter.getAdapter().debug(Debug.BEHAVIOR, "Begin: " + getLocation() + ", vec: " + ray.getVector().toString());
			//Adapter.getAdapter().debug("Tested locs: " + result.getAllTestedLoc().toString());
			return new ArrayList<>();
		}
		return Arrays.asList(result.getBlock());
	}
	
	@Override
	public void applyTheoricVelocity() {
		this.velocity = getTheoricVelocity();
	}
	
	@Override
	public Vector getVelocity() {
		return velocity == null ? getTheoricVelocity() : velocity;
	}
	
	@Override
	public CompensatedWorld getWorld() {
		return world;
	}
	
	@Override
	public void setInternalWorld(CompensatedWorld world) {
		this.world = world;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
