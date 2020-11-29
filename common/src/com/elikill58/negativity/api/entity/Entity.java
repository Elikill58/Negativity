package com.elikill58.negativity.api.entity;

import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;

public interface Entity extends CommandSender {

	boolean isOnGround();

	boolean isOp();
	
	List<Block> getTargetBlock(int maxDistance);
	
	Location getLocation();
	
	double getEyeHeight();
	
	Location getEyeLocation();
	
	Vector getRotation();
	
	EntityType getType();
	
	int getEntityId();
}
