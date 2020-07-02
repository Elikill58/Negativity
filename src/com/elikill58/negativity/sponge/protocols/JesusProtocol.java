package com.elikill58.negativity.sponge.protocols;

import static com.elikill58.negativity.sponge.utils.LocationUtils.has;
import static com.elikill58.negativity.sponge.utils.LocationUtils.hasBoatAroundHim;
import static com.elikill58.negativity.sponge.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

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
import com.elikill58.negativity.universal.ReportType;
import com.flowpowered.math.vector.Vector3d;

public class JesusProtocol extends Cheat {

	public JesusProtocol() {
		super(CheatKeys.JESUS, false, ItemTypes.WATER_BUCKET, CheatCategory.MOVEMENT, true, "waterwalk", "water");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;

		if (p.get(Keys.IS_ELYTRA_FLYING).orElse(false) || p.getVehicle().isPresent())
			return;

		Location<World> loc = p.getLocation();
		Vector3d from = e.getFromTransform().getPosition(), to = e.getToTransform().getPosition();
		Location<World> under = loc.copy().sub(0, 1, 0);
		if (has(loc, "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET", "WATER_LILY")
				|| has(loc.copy().sub(0, 1, 0), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET", "WATER_LILY"))
			return;
		BlockType type = loc.getBlockType();
		BlockType underType = under.getBlockType();
		boolean isInWater = type.getId().contains("WATER"), isOnWater = underType.getId().contains("WATER");
		int ping = Utils.getPing(p);
		double dif = from.getY() - to.getY();
		boolean mayCancel = false;

		if (!isInWater && isOnWater && !hasBoatAroundHim(loc) && !hasOtherThan(under, BlockTypes.WATER) && !p.get(Keys.IS_FLYING).orElse(false)) {

			double reliability = 0;
			if (dif < 0.0005 && dif > 0.00000005)
				reliability = dif * 10000000 - 1;
			else if (dif < 0.1 && dif > 0.08)
				reliability = dif * 1000;
			else if (dif == 0.5)
				reliability = 75;
			else if (dif < 0.30001 && dif > 0.3000)
				reliability = dif * 100 * 2.5;
			else if (dif < 0.002 && dif > -0.002 && dif != 0.0)
				reliability = Math.abs(dif * 5000);
			else if (dif == 0.0)
				reliability = 90;
			mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(reliability),
					"Warn for Jesus: " + np.getWarn(this) + " (Stationary_water aroud him) Diff: " + dif + " and ping: "
							+ ping);
		}

		double distanceAbs = to.distance(from) - Math.abs(from.getY() - to.getY());
		Location<?> upper = loc.copy().add(0, 1, 0);
		float distanceFall = np.getFallDistance();
		if (isInWater && isOnWater && distanceFall < 1 && !upper.getBlock().get(Keys.IS_WET).orElse(false)
				&& !hasOtherThan(under, BlockTypes.WATER)) {
			if (distanceAbs > p.get(Keys.WALKING_SPEED).orElse(Double.MAX_VALUE)
					&& !has(upper, "WATER_LILY") && !p.get(Keys.IS_FLYING).orElse(false)) {
				mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.JESUS), 98,
						"In water, distance: " + distanceAbs, hoverMsg("main", "%distance%", distanceAbs));
			}
		}

		if (dif == -0.5 && (isInWater || isOnWater)) {
			mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(98), "Warn for Jesus: "
					+ np.getWarn(this) + ", dif: -0.5, isIn: " + isInWater + ", isOn: " + isOnWater + " " + ping);
		}

		boolean jesusState = np.contentBoolean.getOrDefault("jesus-state", false);
		if (dif == np.contentDouble.getOrDefault("jesus-last-y-" + jesusState, 0.0) && isInWater && !np.isInFight) {
			if (!hasOtherThan(under, BlockTypes.WATER)) {
				mayCancel = SpongeNegativity.alertMod(np.getWarn(this) > 10 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, parseInPorcent((dif + 5) * 10),
						"Warn for Jesus: " + np.getWarn(this) + " (Stationary_water aroud him) Difference between 2 y: "
								+ dif + " (other: "
								+ np.contentDouble.getOrDefault("jesus-last-y-" + (!jesusState), 0.0)
								+ ") and ping: " + ping);
			}
		}
		np.contentDouble.put("jesus-last-y-" + jesusState, dif);
		np.contentBoolean.put("jesus-state", !jesusState);

		if (isSetBack() && mayCancel)
			to.sub(0, 1, 0);
	}
}
