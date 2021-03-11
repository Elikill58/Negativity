package com.elikill58.negativity.sponge.protocols;

import static com.elikill58.negativity.universal.CheatKeys.AIR_PLACE;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Direction;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;

public class AirPlaceProtocol extends Cheat {

	public static final List<Direction> BLOCK_FACES = Arrays.asList(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

	public AirPlaceProtocol() {
		super(AIR_PLACE, false, ItemTypes.GLASS_BOTTLE, CheatCategory.WORLD, true, "liquidinteract", "liquid");
	}

	@Listener
	public void onBlockPlace(ChangeBlockEvent.Place e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this) || e.isCancelled())
			return;

		BlockSnapshot theBlock = e.getTransactions().get(0).getOriginal();
		BlockType type = theBlock.getState().getType();
		if(type.equals(BlockTypes.WATERLILY))
			return;
		
		StringJoiner blockNames = new StringJoiner(", ");
		
		for(Direction direction : BLOCK_FACES) {
			BlockState b = theBlock.getLocation().get().getRelative(direction).getBlock();
			String name = b.getId();
			if(name.contains("STAIRS") || !(name.contains("AIR") || name.contains("WATER") || name.contains("LAVA") || name.contains("CAVE")))
				return;
			blockNames.add(direction.name().toLowerCase() + ": " + name);
		}

		boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, 100, "Blocks: " + blockNames.toString(), new CheatHover.Literal("Any block around the placed block"), 2);
		if(mayCancel && isSetBack())
			e.setCancelled(true);
	}
}
