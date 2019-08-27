package com.elikill58.negativity.sponge.protocols;

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
import com.elikill58.negativity.universal.ReportType;
import com.flowpowered.math.vector.Vector3d;

public class SpiderProtocol extends Cheat {

	public SpiderProtocol() {
		super("SPIDER", false, ItemTypes.WEB, false, true, "wallhack", "wall");
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		Location<World> loc = p.getLocation();
		if (!np.hasDetectionActive(this))
			return;
		if (np.getFallDistance() != 0.0F)
			return;
		BlockType playerLocType = loc.getBlock().getType(),
				underPlayer = loc.copy().sub(0, 1, 0).getBlock().getType(),
				underUnder = loc.copy().sub(0, 2, 0).getBlock().getType(),
				m3 = loc.copy().add(0, 3, 0).getBlock().getType();
		if (!underPlayer.equals(BlockTypes.AIR) || !underUnder.equals(BlockTypes.AIR) || playerLocType.equals(BlockTypes.VINE) || playerLocType.equals(BlockTypes.LADDER)
				|| underPlayer.equals(BlockTypes.VINE) || underPlayer.equals(BlockTypes.LADDER) || m3.equals(BlockTypes.VINE)
				|| m3.equals(BlockTypes.LADDER) || !playerLocType.equals(BlockTypes.AIR))
			return;
		double y = e.getToTransform().getLocation().getY() - e.getFromTransform().getLocation().getY(), last = np.lastY;
		np.lastY = y;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumIntegerDigits(4);
		boolean isAris = ((float) y) == p.get(Keys.WALKING_SPEED).get();
		if (((y > 0.499 && y < 0.7) || isAris || last == y) && hasOtherThan(loc, BlockTypes.AIR)) {
			boolean hasSlabStairs = false;
			for (int u = 0; u < 360; u += 3) {
				Location<World> flameloc = loc.copy();
				flameloc.add(Math.sin(u) * 3, 0, Math.cos(u) * 3);
				String name = flameloc.copy().getBlock().getType().getName(),
						secondname = flameloc.copy().add(0, 1, 0).getBlock().getType().getName();
				if (name.contains("SLAB") || name.contains("STAIRS") || secondname.contains("SLAB")
						|| secondname.contains("STAIRS"))
					hasSlabStairs = true;
			}
			if (hasSlabStairs)
				return;
			int relia = (int) (y * 450);
			if (isAris)
				relia = relia + 39;
			ReportType type =  (np.getWarn(this) > 6) ? ReportType.VIOLATION : ReportType.WARNING;
			boolean mayCancel = SpongeNegativity.alertMod(type, p, this, Utils.parseInPorcent(relia),
					"Nothing around him. To > From: " + y + " isAris: " + isAris + " has not stab slairs.");
			if(isSetBack() && mayCancel){
				Location<World> locc = p.getLocation();
				while(locc.getBlock().getType().equals(BlockTypes.AIR))
					locc.sub(0, 1, 0);
				p.setLocation(locc.add(0, 1, 0));
			}
		}
	}

	@Listener
	public void onPlayerMove2(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		Location<World> loc = p.getLocation();
		if (!np.hasDetectionActive(this))
			return;
		double y = e.getToTransform().getPosition().getY() - e.getFromTransform().getPosition().getY();
		boolean isAris = ((float) y) == p.get(Keys.WALKING_SPEED).get();
		if (np.lastSpiderLoc != null && np.lastSpiderLoc.getExtent().equals(loc.getExtent()) && y > 0) {
			loc.setPosition(new Vector3d(np.lastSpiderLoc.getX(), loc.getY(), np.lastSpiderLoc.getZ()));
			double tempDis = loc.getPosition().distance(np.lastSpiderLoc.getPosition());
			if (np.lastSpiderDistance == tempDis && tempDis != 0) {
				int porcent = Utils.parseInPorcent(tempDis * 450);
				if (SpongeNegativity.alertMod(ReportType.WARNING, p, this, porcent, "Nothing around him. To > From: "
						+ y + " isAris: " + isAris + ". Walk on wall with always same y.") && isSetBack()) {
					Location<World> locc = p.getLocation();
					while (locc.getBlock().getType().equals(BlockTypes.AIR))
						locc.sub(0, 1, 0);
					p.setLocation(locc.add(0, 1, 0));
				}
			}
			np.lastSpiderDistance = tempDis;
		}
		np.lastSpiderLoc = loc;
	}

	public boolean hasOtherThan(Location<World> loc, BlockType m) {
		if (!loc.copy().add(0, 0, 1).getBlock().getType().equals(m))
			return true;
		if (!loc.copy().add(1, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.copy().add(-1, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.copy().add(-1, 0, 1).getBlock().getType().equals(m))
			return true;
		return false;
	}
}
