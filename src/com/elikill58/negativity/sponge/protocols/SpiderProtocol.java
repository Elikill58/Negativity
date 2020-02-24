package com.elikill58.negativity.sponge.protocols;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
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
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class SpiderProtocol extends Cheat {

	private static final NumberFormat NUMBER_FORMATTER = new DecimalFormat();

	static {
		NUMBER_FORMATTER.setMaximumIntegerDigits(4);
	}

	public SpiderProtocol() {
		super(CheatKeys.SPIDER, false, ItemTypes.WEB, CheatCategory.MOVEMENT, true, "wallhack", "wall");
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
		if (!np.hasDetectionActive(this) || np.getFallDistance() != 0
				|| p.get(Keys.IS_ELYTRA_FLYING).orElse(false) || p.get(Keys.IS_FLYING).orElse(false)) {
			return;
		}

		Location<World> loc = p.getLocation();
		if (isClimbableBlock(loc.getBlockType())
				|| isClimbableBlock(loc.sub(0, 1, 0).getBlockType())
				|| isClimbableBlock(loc.sub(0, 2, 0).getBlockType())
				|| isClimbableBlock(loc.add(0, 3, 0).getBlockType())) {
			return;
		}

		double y = e.getToTransform().getLocation().getY() - e.getFromTransform().getLocation().getY();
		double last = np.lastYDiff;
		np.lastYDiff = y;
		boolean isAris = y == p.get(Keys.WALKING_SPEED).get();
		if (((y > 0.499 && y < 0.7) || isAris || last == y) && hasOtherThan(loc, BlockTypes.AIR)) {
			if (hasBypassBlockAround(loc)) {
				return;
			}

			int relia = (int) (y * 200);
			if (isAris) {
				relia += 39;
			}

			ReportType type = (np.getWarn(this) > 6) ? ReportType.WARNING : ReportType.VIOLATION;
			boolean mayCancel = SpongeNegativity.alertMod(type, p, this, Utils.parseInPorcent(relia),
					"Nothing around him. To > From: " + y + " isAris: " + isAris + " has not stab slairs.");
			if (isSetBack() && mayCancel) {
				Location<World> locc = p.getLocation();
				while (locc.getBlockType().equals(BlockTypes.AIR)) {
					locc = locc.sub(Vector3i.UNIT_Y);
				}
				p.setLocation(locc.add(Vector3i.UNIT_Y));
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
		if (!np.hasDetectionActive(this) || p.get(Keys.IS_FLYING).orElse(false)) {
			return;
		}

		double y = e.getToTransform().getPosition().getY() - e.getFromTransform().getPosition().getY();
		boolean isAris = y == p.get(Keys.WALKING_SPEED).get();
		if (np.lastSpiderLoc != null && np.lastSpiderLoc.getExtent().equals(loc.getExtent()) && y > 0) {
			if (hasBypassBlockAround(loc)) {
				np.lastSpiderLoc = loc;
				return;
			}
			loc.setPosition(new Vector3d(np.lastSpiderLoc.getX(), loc.getY(), np.lastSpiderLoc.getZ()));
			double tempDis = loc.getPosition().distance(np.lastSpiderLoc.getPosition());
			if (np.lastSpiderDistance == tempDis && tempDis != 0) {
				int porcent = Utils.parseInPorcent(tempDis * 450);
				if (SpongeNegativity.alertMod(ReportType.WARNING, p, this, porcent, "Nothing around him. To > From: "
						+ y + " isAris: " + isAris + ". Walk on wall with always same y.") && isSetBack()) {
					Location<World> locc = p.getLocation();
					while (locc.getBlockType().equals(BlockTypes.AIR)) {
						locc = locc.sub(Vector3i.UNIT_Y);
					}
					p.setLocation(locc.add(Vector3i.UNIT_Y));
				}
			}
			np.lastSpiderDistance = tempDis;
		}
		np.lastSpiderLoc = loc;
	}

	public boolean hasOtherThan(Location<World> loc, BlockType m) {
		if (!loc.add(0, 0, 1).getBlockType().equals(m))
			return true;
		if (!loc.add(1, 0, -1).getBlockType().equals(m))
			return true;
		if (!loc.add(-1, 0, -1).getBlockType().equals(m))
			return true;
		if (!loc.add(-1, 0, 1).getBlockType().equals(m))
			return true;
		return false;
	}

	private boolean hasBypassBlockAround(Location<World> loc) {
		if(hasOtherThan(loc, "SLAB") || hasOtherThan(loc, "STAIRS"))
			return true;
		loc = loc.copy().sub(0, 1, 0);
		if(hasOtherThan(loc, "SLAB") || hasOtherThan(loc, "STAIRS"))
			return true;
		return loc.getBlockType().getName().contains("WATER")
				|| loc.sub(Vector3i.UNIT_Y).getBlockType().getName().contains("WATER");
	}

	public boolean hasOtherThan(Location<World> loc, String m) {
		if (!loc.add(0, 0, 1).getBlockType().getId().contains(m))
			return true;
		if (!loc.add(1, 0, -1).getBlockType().getId().contains(m))
			return true;
		if (!loc.add(-1, 0, -1).getBlockType().getId().contains(m))
			return true;
		if (!loc.add(-1, 0, 1).getBlockType().getId().contains(m))
			return true;
		return false;
	}

	private static boolean isClimbableBlock(BlockType blockType) {
		return blockType == BlockTypes.VINE || blockType == BlockTypes.LADDER;
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
