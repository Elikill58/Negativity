package com.elikill58.negativity.api.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.block.BlockRay;
import com.elikill58.negativity.api.ray.block.BlockRayBuilder;
import com.elikill58.negativity.api.ray.block.BlockRayResult;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

public abstract class AbstractPlayer implements Player {

	private int protocolVersion = 0;
	private Version playerVersion;
	protected Location location;
	protected Vector velocity = null;

	@Override
	public Version getPlayerVersion() {
		return isVersionSet() ? playerVersion : (playerVersion = Version.getVersionByProtocolID(getProtocolVersion()));
	}
	
	@Override
	public void setPlayerVersion(Version version) {
		playerVersion = version;
		protocolVersion = version.getFirstProtocolNumber();
	}

	private boolean isVersionSet() {
		return playerVersion != null && !playerVersion.equals(Version.HIGHER) && !playerVersion.equals(Version.LOWER);
	}

	@Override
	public int getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public void setProtocolVersion(int protocolVersion) {
		if (this.protocolVersion == 0 || !isVersionSet()) { // if his using default values
			this.playerVersion = Version.getVersionByProtocolID(protocolVersion);
			Adapter.getAdapter().debug("Setting ProtocolVersion: " + protocolVersion + ", founded: "
					+ playerVersion.name() + " (previous: " + this.protocolVersion + ")");
			if (protocolVersion == 0 && this.protocolVersion != 0)
				return;// prevent losing good value
			this.protocolVersion = protocolVersion;
		}
	}
	
	@Override
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@Override
	public List<Block> getTargetBlock(int maxDistance) {
		BlockRay ray = new BlockRayBuilder(this).maxDistance(maxDistance).ignoreAir(true).build();
		BlockRayResult result = ray.compile();
		if(!result.getRayResult().isFounded()) {
			Adapter.getAdapter().debug("Begin: " + getLocation() + ", vec: " + ray.getVector().toString());
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
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
