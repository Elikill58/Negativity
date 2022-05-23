package com.elikill58.negativity.fabric.listeners;

import org.spongepowered.asm.mixin.Mixin;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.fabric.impl.block.FabricBlock;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public class BlockListeners {
	
	public BlockListeners() {
		PlayerBlockBreakEvents.BEFORE.register(this::onBlockBreak);
	}
	
	
	public boolean onBlockBreak(World w, PlayerEntity p, BlockPos pos, BlockState state, BlockEntity be) {
		BlockBreakEvent event = new BlockBreakEvent(FabricEntityManager.getPlayer((ServerPlayerEntity) p), new FabricBlock(state.getBlock(), w, pos));
		EventManager.callEvent(event);
		return event.isCancelled();
	}

	public void onBlockPlace(BlockPos pos, BlockState state) {
		/*BlockPlaceEvent event = new BlockPlaceEvent(FabricEntityManager.getPlayer(p), new FabricBlock(e.getTransactions().get(0).getOriginal()));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());*/
	}
}
