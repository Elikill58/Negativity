package com.elikill58.negativity.fabric.impl.location;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.fabric.impl.block.FabricBlock;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.entity.EntityLookup;

public class FabricWorld extends World {

	private final net.minecraft.world.World w;

	public FabricWorld(net.minecraft.world.World w) {
		this.w = w;
	}

	@Override
	public String getName() {
		return w.asString();
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		return new FabricBlock(w.getBlockState(pos).getBlock(), w, pos);
	}

	@Override
	public Block getBlockAt(Location loc) {
		return getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entity> getEntities() {
		List<Entity> list = new ArrayList<>();
		w.getProfiler().visit("getEntities");
		EntityLookup<net.minecraft.entity.Entity> lookup = (EntityLookup<net.minecraft.entity.Entity>) ReflectionUtils.callMethod(w, "getEntityLookup");
		lookup.forEach(null, e -> list.add(FabricEntityManager.getEntity(e)));
		return list;
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(w.getDifficulty().toString());
	}
	
	@Override
	public int getMaxHeight() {
		return w.getHeight();
	}
	
	@Override
	public int getMinHeight() {
		return 0;
	}

	@Override
	public Object getDefault() {
		return w;
	}

}
