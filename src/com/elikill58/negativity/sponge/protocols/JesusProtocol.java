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

public class JesusProtocol extends Cheat {

	public JesusProtocol() {
		super("JESUS", false, ItemTypes.WATER_BUCKET, false, true, "waterwalk", "water");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		Location<?> loc = p.getLocation();
		BlockType m = loc.getBlock().getType();
		BlockType under = loc.copy().add(0, -1, 0).getBlock().getType();
		if (m.equals(BlockTypes.WATER))
			np.isInWater = true;
		else
			np.isInWater = false;
		if (under.equals(BlockTypes.WATER))
			np.isOnWater = true;
		else
			np.isOnWater = false;
		double dif = e.getFromTransform().getYaw() - e.getToTransform().getYaw();
		Optional<EntitySnapshot> vehicle = p.get(Keys.VEHICLE);
		if(vehicle.isPresent())
			if(vehicle.get().getType().equals(EntityTypes.BOAT))
				return;
		if (!np.isInWater && np.isOnWater && !hasBoatAroundHim(loc)) {
			if (!np.hasOtherThan(loc.copy().sub(0, 1, 0), BlockTypes.WATER)
					&& !p.getLocation().getBlock().getType().equals(BlockTypes.WATERLILY)) {
				boolean has = false, hasWaterLily = hasWaterLily(loc.copy().sub(0, 1, 0));
				for (int u = 0; u < 360; u += 3) {
					Location<World> futurLoc = new Location<World>(p.getWorld(), loc.getX() + Math.sin(u) * 3, loc.getY() - 1, loc.getZ() + Math.cos(u) * 3);
					if (!futurLoc.getBlock().getType().equals(BlockTypes.WATER)) {
						has = true;
						if (futurLoc.getBlock().getType().equals(BlockTypes.WATERLILY))
							hasWaterLily = true;
					}
				}
				if(hasWaterLily || has)
					return;
				double reliability = 0;
				boolean isCheating = true;
				ReportType type = ReportType.VIOLATION;
				if(dif < 0.0005 && dif > 0.00000005)
					reliability = dif * 10000000 - 1;
				else if(dif < 0.1 && dif > 0.08)
					reliability = dif * 1000;
				else if(dif == 0.5){
					reliability = 50;
					type = ReportType.WARNING;
				} else if(dif < 0.30001 && dif > 0.3000)
					reliability = dif * 100 * 2.5;
				else if(dif < 0.002 && dif > -0.002 && dif != 0.0)
					reliability = Math.abs(dif * 5000);
				else if(dif == 0.0)
					reliability = 95;
				else if(dif == p.get(Keys.WALKING_SPEED).get())
					reliability = 90;
				else isCheating = false;
				if(isCheating){
					boolean mayCancel = SpongeNegativity.alertMod(type, p, this, Utils.parseInPorcent(reliability), "Warn for Jesus: " + np.getWarn(this) + " (Stationary_water aroud him) WalkSpeed: " + p.get(Keys.WALKING_SPEED).get() + ". Diff: " + dif + " and ping: "
							+ Utils.getPing(p));
					if(isSetBack() && mayCancel)
						p.setLocation(p.getLocation().sub(0, 1, 0));
				}
			}
		}
	}
	
	private boolean hasWaterLily(Location<?> loc) {
		int fX = loc.getBlockX(), fY = loc.getBlockY(), fZ = loc.getBlockZ();
		for (int y = (fY - 1); y != (fY + 2); y++)
			for (int x = (fX - 2); x != (fX + 3); x++)
				for (int z = (fZ - 2); z != (fZ + 3); z++)
					if(new Location<World>(loc.getLocatableBlock().get().getWorld(), x, y, z).getBlockType().equals(BlockTypes.WATERLILY))
						return true;
		return false;
	}
	
	public boolean hasBoatAroundHim(Location<?> loc) {
		for(Player p : Utils.getOnlinePlayers()) {
			Location<?> l = p .getLocation();
			if(((World) loc.getExtent()).equals(l.getExtent()))
				if(l.getPosition().distance(loc.getPosition()) < 2)
					return true;
		}
		return false;
	}
}
