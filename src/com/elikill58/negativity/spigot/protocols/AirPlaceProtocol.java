package com.elikill58.negativity.spigot.protocols;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;

import static com.elikill58.negativity.universal.CheatKeys.AIR_PLACE;

public class AirPlaceProtocol extends Cheat implements Listener {

	public static final List<BlockFace> BLOCK_FACES = Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

	public AirPlaceProtocol() {
		super(AIR_PLACE, false, Material.GLASS_BOTTLE, CheatCategory.WORLD, true, "liquidinteract", "liquid");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlaceBlock(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this) || e.isCancelled() || !e.getBlockAgainst().getType().equals(Material.AIR))
			return;
		Block theBlock = e.getBlock();
		Material type = theBlock.getType();
		if(type.equals(ItemUtils.WATER_LILY))
			return;
		StringJoiner blockNames = new StringJoiner(", ");
		
		for(BlockFace face : BLOCK_FACES) {
			Block b = theBlock.getRelative(face);
			String name = b.getType().name();
			if(name.contains("STAIRS") || !(name.contains("AIR") || name.contains("WATER") || name.contains("LAVA") || name.contains("CAVE")))
				return;
			blockNames.add(face.name().toLowerCase(Locale.ROOT) + ": " + name);
		}

		boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, 100, "Blocks: " + blockNames.toString(), new CheatHover.Literal("Any block around the placed block"), 2);
		if(mayCancel && isSetBack())
			e.setBuild(false);
	}
}
