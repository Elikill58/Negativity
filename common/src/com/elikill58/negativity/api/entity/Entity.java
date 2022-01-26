package com.elikill58.negativity.api.entity;

import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;

public interface Entity extends CommandSender {

	/**
	 * Check if the given entity is currently on ground
	 * 
	 * @return true if is on ground
	 */
	boolean isOnGround();

	boolean isOp();
	
	List<Block> getTargetBlock(int maxDistance);
	
	Location getLocation();
	
	double getEyeHeight();
	
	Location getEyeLocation();
	
	Vector getRotation();
	
	EntityType getType();
	
	int getEntityId();
	
	BoundingBox getBoundingBox();
	
	/**
	 * Get current entity velocity
	 * 
	 * @return the entity velocity
	 */
	Vector getVelocity();
	
	void applyTheoricVelocity();
	
	/**
	 * Get the velocity that the entity SHOULD be take
	 *
	 * @return the theoric (and platform) velocity
	 */
	Vector getTheoricVelocity();
	
	/**
	 * Edit the entity velocity
	 * 
	 * @param vel the new velocity
	 */
	void setVelocity(Vector vel);
}
