package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.report.ReportType;

import java.util.EnumSet;

public class GroundSpoof extends Cheat implements Listeners {
    private static final EnumSet<BlockFace> totalFaces = EnumSet.of(BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH,
            BlockFace.NORTH, BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST);

    public GroundSpoof() {
        super("GROUND_SPOOF", CheatCategory.MOVEMENT, Materials.DIRT, false, false, "groundspoof");
    }

    @EventListener
    public void onGroundSpoof(PlayerMoveEvent e) {
        if (!((Entity) e.getPlayer()).isOnGround()) {
            return;
        }
        if (isOnGround(e.getPlayer())) {
            return;
        }
        final Block downBlock = e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (isNotAir(downBlock.getRelative(BlockFace.NORTH))
                && isNotAir(downBlock.getRelative(BlockFace.SOUTH))
                || (isNotAir(downBlock.getRelative(BlockFace.EAST))
                && isNotAir(downBlock.getRelative(BlockFace.WEST)))) {
            return;
        }
        Negativity.alertMod(ReportType.WARNING, e.getPlayer(), this, 99, "groundspoof",
                "",
                new CheatHover.Literal("Ground Spoof (Fly, NoFall, and other movement hacks)"));
    }

    private static boolean isNotAir(Block block) {
        return !block.getType().equals(Materials.AIR);
    }

    public static boolean isOnGround(Player player) {
        final Block block = player.getLocation().getBlock();
        final Block downBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

        if (isNotAir(block.getRelative(BlockFace.DOWN))) {
            return true;
        }

        for (final BlockFace face : totalFaces) {
            if (isNotAir(downBlock.getRelative(face))) {
                if (isSupportedBy(player.getLocation(), face)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSupportedBy(final Location playerLoc, final BlockFace face) {
        switch (face) {
            case NORTH:
                final double northRequiredZP = 0.31;
                final double northRequiredZN = 0.69;
                final double northPlayerZ = Math.abs(playerLoc.getZ() - ((int) playerLoc.getZ()));
                if (playerLoc.getZ() < 0) {
                    return northPlayerZ >= northRequiredZN;
                }
                return northPlayerZ <= northRequiredZP;
            case EAST:
                final double eastRequiredXP = 0.69;
                final double eastRequiredXN = 0.31;
                final double eastPlayerX = Math.abs(playerLoc.getX() - ((int) playerLoc.getX()));
                if (playerLoc.getX() < 0) {
                    return eastPlayerX <= eastRequiredXN;
                }
                return eastPlayerX >= eastRequiredXP;
            case SOUTH:
                final double southRequiredZP = 0.69;
                final double southRequiredZN = 0.31;
                final double southPlayerZ = Math.abs(playerLoc.getZ() - ((int) playerLoc.getZ()));
                if (playerLoc.getZ() < 0) {
                    return southPlayerZ <= southRequiredZN;
                }
                return southPlayerZ >= southRequiredZP;
            case WEST:
                final double westRequiredXP = 0.31;
                final double westRequiredXN = 0.69;
                final double westPlayerX = Math.abs(playerLoc.getX() - ((int) playerLoc.getX()));
                if (playerLoc.getX() < 0) {
                    return westPlayerX >= westRequiredXN;
                }
                return westPlayerX <= westRequiredXP;
            case NORTH_EAST:
                return isSupportedBy(playerLoc, BlockFace.NORTH) || isSupportedBy(playerLoc, BlockFace.EAST);
            case SOUTH_EAST:
                return isSupportedBy(playerLoc, BlockFace.SOUTH) || isSupportedBy(playerLoc, BlockFace.EAST);
            case SOUTH_WEST:
                return isSupportedBy(playerLoc, BlockFace.SOUTH) || isSupportedBy(playerLoc, BlockFace.WEST);
            case NORTH_WEST:
                return isSupportedBy(playerLoc, BlockFace.NORTH) || isSupportedBy(playerLoc, BlockFace.WEST);
            default:
                return false;
        }
    }
}
