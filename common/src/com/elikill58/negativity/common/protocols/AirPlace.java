package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.AIR_PLACE;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.report.ReportType;

public class AirPlace extends Cheat implements Listeners {

	public static final List<BlockFace> BLOCK_FACES = Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	public AirPlace() {
		super(AIR_PLACE, CheatCategory.WORLD, Materials.GLASS_BOTTLE, false, false, "liquidinteract", "liquid");
	}

	@Check(name = "block-around", conditions = { CheckConditions.SURVIVAL })
	public void onPlaceBlock(BlockPlaceEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if(e.isCancelled())
			return;
		Block theBlock = e.getBlock();
		Material type = theBlock.getType();
		if(type.equals(Materials.WATER_LILY))
			return;
		StringJoiner blockNames = new StringJoiner(", ");
		
		for(BlockFace face : BLOCK_FACES) {
			Block b = theBlock.getRelative(face);
			String name = b.getType().getId();
			if(name.contains("STAIRS") || !(name.contains("AIR") || name.contains("WATER") || name.contains("LAVA") || name.contains("CAVE")))
				return;
			blockNames.add(face.name().toLowerCase(Locale.ROOT) + ": " + name);
		}

		boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 100, "block-around", "Blocks: " + blockNames.toString(), new CheatHover.Literal("Any block around the placed block"), 2);
		if(mayCancel && isSetBack())
			e.setCancelled(false);
	}
}
