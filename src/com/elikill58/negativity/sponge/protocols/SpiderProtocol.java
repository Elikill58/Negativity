package com.elikill58.negativity.sponge.protocols;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpiderProtocol extends Cheat {

	private static final NumberFormat NUMBER_FORMATTER = new DecimalFormat();

	static {
		NUMBER_FORMATTER.setMaximumIntegerDigits(4);
	}

	public SpiderProtocol() {
		super(CheatKeys.SPIDER, false, ItemTypes.SPIDER_EYE, CheatCategory.MOVEMENT, true, "wallhack", "wall");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}

		if (p.getVehicle().isPresent()) {
			// Mounting horses triggers false positives constantly
			return;
		}

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || np.getFallDistance() != 0 || p.get(Keys.IS_ELYTRA_FLYING).orElse(false)
				|| p.get(Keys.IS_FLYING).orElse(false) || np.hasPotionEffect(PotionEffectTypes.JUMP_BOOST)) {
			return;
		}
		
		Location<World> loc = p.getLocation(), from = e.getFromTransform().getLocation(), to = e.getToTransform().getLocation();
		if(from.getX() == to.getX() && from.getZ() == to.getZ())
			return;
		
		if (isClimbableBlock(loc.getBlockType()) || isClimbableBlock(loc.sub(0, 1, 0).getBlockType())
				|| isClimbableBlock(loc.sub(0, 2, 0).getBlockType())) {
			return;
		}
		// TODO implement Trident use
		if(hasBypassBlockAround(loc))
			return;

		double y = e.getToTransform().getLocation().getY() - e.getFromTransform().getLocation().getY();
		boolean isAris = y == p.get(Keys.WALKING_SPEED).get();
		if (((y > 0.499 && y < 0.7) || isAris) && !np.isUsingSlimeBlock) {
			int relia = (int) (y * 160);
			if (isAris) {
				relia += 39;
			}

			ReportType type = (np.getWarn(this) > 6) ? ReportType.WARNING : ReportType.VIOLATION;
			boolean mayCancel = SpongeNegativity.alertMod(type, p, this, UniversalUtils.parseInPorcent(relia),
					"Nothing around him. To > From: " + y + " isAris: " + isAris + " has not stab slairs.");
			if (isSetBack() && mayCancel) {
				Utils.teleportPlayerOnGround(p);
			}
		}
	}

	@Listener
	public void onPlayerMove2(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}
		if (p.getVehicle().isPresent()) {
			// Mounting horses triggers false positives constantly
			return;
		}

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		Location<World> loc = p.getLocation();
		if (!np.hasDetectionActive(this) || p.get(Keys.IS_FLYING).orElse(false))
			return;
		if (hasBypassBlockAround(loc) || np.hasExtended(loc, "STAIRS"))
			return;

		double y = e.getToTransform().getPosition().getY() - e.getFromTransform().getPosition().getY();
		if (np.lastSpiderLoc != null && np.lastSpiderLoc.getExtent().equals(loc.getExtent()) && y > 0) {
			double tempDis = loc.getY() - np.lastSpiderLoc.getY();
			if (np.lastSpiderDistance == tempDis && tempDis != 0) {
				np.SPIDER_SAME_DIST++;
				if (np.SPIDER_SAME_DIST > 2) {
					if (SpongeNegativity.alertMod(ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(tempDis * 400 + np.SPIDER_SAME_DIST),
							"Nothing strange around him. To > From: " + y + ". Walk on wall with always same y "
									+ np.SPIDER_SAME_DIST + " times")
							&& isSetBack()) {
						Utils.teleportPlayerOnGround(p);
					}
				} else
					np.SPIDER_SAME_DIST = 0;
			}
			np.lastSpiderDistance = tempDis;
		}
		np.lastSpiderLoc = loc;
	}

	private boolean hasBypassBlockAround(Location<World> loc) {
		if (has(loc, "SLAB", "STAIRS", "VINE", "LADDER", "WATER", "SCAFFOLD"))
			return true;
		loc = loc.copy().sub(0, 1, 0);
		if (has(loc, "SLAB", "STAIRS", "VINE", "LADDER", "WATER", "SCAFFOLD"))
			return true;
		return false;
	}

	public boolean has(Location<World> loc, String... m) {
		String b = loc.getBlock().getType().getId(),
				b1 = loc.copy().add(0, 0, 1).getBlock().getType().getId(),
				b2 = loc.copy().add(1, 0, -1).getBlock().getType().getId(),
				b3 = loc.copy().add(-1, 0, -1).getBlock().getType().getId(),
				b4 = loc.copy().add(-1, 0, 1).getBlock().getType().getId();
		for (String temp : m) {
			if (b.contains(temp))
				return true;
			if (b1.contains(temp))
				return true;
			if (b2.contains(temp))
				return true;
			if (b3.contains(temp))
				return true;
			if (b4.contains(temp))
				return true;
		}
		return false;
	}

	private static boolean isClimbableBlock(BlockType blockType) {
		return blockType == BlockTypes.VINE || blockType == BlockTypes.LADDER;
	}
}
