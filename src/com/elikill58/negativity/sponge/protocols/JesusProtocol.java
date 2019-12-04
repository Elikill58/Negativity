package com.elikill58.negativity.sponge.protocols;

import java.util.Optional;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityTypes;
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
import com.elikill58.negativity.universal.*;
import com.flowpowered.math.vector.Vector3i;

public class JesusProtocol extends Cheat {

	public JesusProtocol() {
		super("JESUS", false, ItemTypes.WATER_BUCKET, false, true, "waterwalk", "water");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		Location<?> loc = p.getLocation();
		BlockType m = loc.getBlockType();
		BlockType under = loc.sub(Vector3i.UNIT_Y).getBlockType();
		np.isInWater = m.equals(BlockTypes.WATER);
		np.isOnWater = under.equals(BlockTypes.WATER);
		Optional<EntitySnapshot> vehicle = p.get(Keys.VEHICLE);
		if (vehicle.isPresent() && vehicle.get().getType() == EntityTypes.BOAT) {
			return;
		}

		if (!np.isInWater && np.isOnWater && !hasBoatAroundHim(loc) && !np.hasOtherThan(loc.sub(0, 1, 0), BlockTypes.WATER)
				&& !p.getLocation().getBlockType().equals(BlockTypes.WATERLILY)) {
			boolean has = false, hasWaterLily = hasWaterLily(loc.sub(0, 1, 0));
			for (int u = 0; u < 360; u += 3) {
				Location<World> futurLoc = new Location<World>(p.getWorld(), loc.getX() + Math.sin(u) * 3, loc.getY() - 1, loc.getZ() + Math.cos(u) * 3);
				if (!futurLoc.getBlock().getType().equals(BlockTypes.WATER)) {
					has = true;
					if (futurLoc.getBlock().getType().equals(BlockTypes.WATERLILY))
						hasWaterLily = true;
				}
			}
			if (hasWaterLily || has)
				return;
			double reliability = 0;
			boolean isCheating = true;

			double dif = e.getFromTransform().getYaw() - e.getToTransform().getYaw();
			ReportType type = ReportType.VIOLATION;
			if (dif < 0.0005 && dif > 0.00000005)
				reliability = dif * 10000000 - 1;
			else if (dif < 0.1 && dif > 0.08)
				reliability = dif * 1000;
			else if (dif == 0.5) {
				reliability = 50;
				type = ReportType.WARNING;
			} else if (dif < 0.30001 && dif > 0.3000)
				reliability = dif * 100 * 2.5;
			else if (dif < 0.002 && dif > -0.002 && dif != 0.0)
				reliability = Math.abs(dif * 5000);
			else if (dif == 0.0)
				reliability = 95;
			else if (dif == p.get(Keys.WALKING_SPEED).get())
				reliability = 90;
			else isCheating = false;
			if (isCheating) {
				boolean mayCancel = SpongeNegativity.alertMod(type, p, this, Utils.parseInPorcent(reliability),
						"Warn for Jesus: " + np.getWarn(this) + " (Stationary_water aroud him) WalkSpeed: " + p.get(Keys.WALKING_SPEED).get() + ". Diff: " + dif + " and ping: " + Utils.getPing(p));
				if (isSetBack() && mayCancel) {
					p.setLocation(p.getLocation().sub(Vector3i.UNIT_Y));
				}
			}
		}
	}

	private boolean hasWaterLily(Location<?> loc) {
		int fX = loc.getBlockX(), fY = loc.getBlockY(), fZ = loc.getBlockZ();
		for (int y = (fY - 1); y != (fY + 2); y++) {
			for (int x = (fX - 2); x != (fX + 3); x++) {
				for (int z = (fZ - 2); z != (fZ + 3); z++) {
					if (loc.getExtent().getBlockType(x, y, z) == BlockTypes.WATERLILY)
						return true;
				}
			}
		}
		return false;
	}

	public boolean hasBoatAroundHim(Location<?> loc) {
		for (Player p : Utils.getOnlinePlayers()) {
			Location<?> l = p.getLocation();
			if (loc.getExtent().equals(l.getExtent()) && l.getPosition().distance(loc.getPosition()) < 2) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
