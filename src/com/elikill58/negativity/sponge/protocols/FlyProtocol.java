package com.elikill58.negativity.sponge.protocols;

import java.util.Optional;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
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
import com.elikill58.negativity.sponge.utils.LocationUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class FlyProtocol extends Cheat {

	public FlyProtocol() {
		super(CheatKeys.FLY, true, ItemTypes.FIREWORKS, CheatCategory.MOVEMENT, true, "flyhack");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;

		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;

		if (np.justDismounted) {
			// Some cases like jumping with a horse may trigger false positives,
			// dismounting while it is jumping also triggers false positives
			return;
		}

		if (p.get(Keys.CAN_FLY).orElse(false) || p.get(Keys.IS_ELYTRA_FLYING).orElse(false))
			return;
		
		if(np.hasPotionEffect(PotionEffectTypes.LEVITATION))
			return;

		BlockType blockTypeBelow = p.getLocation().sub(Vector3i.UNIT_Y).getBlockType();
		if (blockTypeBelow != BlockTypes.AIR || p.getLocation().sub(0, 2, 0).getBlockType() != BlockTypes.AIR) {
			return;
		}

		if (np.hasPotionEffect(PotionEffectTypes.SPEED)) {
			int speed = 0;
			for (PotionEffect pe : np.getActiveEffects())
				if (pe.getType().equals(PotionEffectTypes.SPEED))
					speed += pe.getAmplifier() + 1;
			if (speed > 40)
				return;
		}
		boolean mayCancel = false;
		Vector3d fromPosition = e.getFromTransform().getPosition();
		Vector3d toPosition = e.getToTransform().getPosition();
		double distance = toPosition.distance(fromPosition);
		boolean isInBoat = p.getVehicle().isPresent() && p.getVehicle().get().getType().equals(EntityTypes.BOAT);
		
		Location<World> loc = p.getLocation().copy(),
				locUnder = p.getLocation().copy().sub(0, 1, 0),
				locUnderUnder = p.getLocation().copy().sub(0, 2, 0);
		BlockType type = loc.getBlockType(), typeUpper = loc.copy().add(0, 1, 0).getBlockType();
		boolean isInWater = loc.getBlock().getType().equals(BlockTypes.WATER), isOnWater = locUnder.getBlock().getType().equals(BlockTypes.WATER);
		double y = fromPosition.getY() - toPosition.getY();
		if(String.valueOf(y).contains("E") && !String.valueOf(y).equalsIgnoreCase("2.9430145066276694E-4") && !p.getVehicle().isPresent()
				&& !LocationUtils.hasBoatAroundHim(loc) && !np.isInFight && !(isInWater || isOnWater)){
			mayCancel = SpongeNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, 97, "Suspicious Y: " + y);
		}
		
		if (!(p.get(Keys.IS_SPRINTING).orElse(false) && (toPosition.getY() - fromPosition.getY()) > 0)
				&& locUnder.getBlock().getType().equals(BlockTypes.AIR)
				&& locUnderUnder.getBlock().getType().equals(BlockTypes.AIR)
				&& (np.getFallDistance() == 0.0F || isInBoat)
				&& (typeUpper.equals(BlockTypes.AIR)) && distance > 0.8
				&& !p.isOnGround()) {
			mayCancel = SpongeNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, UniversalUtils.parseInPorcent((int) distance * 50),
					"Player not in ground, distance: " + distance + (isInBoat ? " On boat" : "")
					+ ". Warn for fly: " + np.getWarn(this), isInBoat ? hoverMsg("boat") : null);
		}

		if (!LocationUtils.hasOtherThanExtended(loc, BlockTypes.AIR) && !np.contentBoolean.getOrDefault("boat-falling", false)
				&& !LocationUtils.hasOtherThanExtended(locUnder, BlockTypes.AIR)
				&& !LocationUtils.hasOtherThanExtended(locUnderUnder, BlockTypes.AIR)
				&& (fromPosition.getY() <= toPosition.getY() || isInBoat)) {
			double nbTimeAirBelow = np.contentDouble.getOrDefault("fly-air-below", 0.0);
			np.contentDouble.put("fly-air-below", nbTimeAirBelow + 1);
			if(nbTimeAirBelow > 6) { // we don't care when player jump
				double d = toPosition.getY() - fromPosition.getY();
				int nb = LocationUtils.getNbAirBlockDown(p), porcent = UniversalUtils.parseInPorcent(nb * 15 + d);
				if (LocationUtils.hasOtherThan(p.getLocation().add(0, -3, 0), BlockTypes.AIR))
					porcent = UniversalUtils.parseInPorcent(porcent - 15);
				mayCancel = SpongeNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
						this, porcent, "Player not in ground (" + nb + " air blocks down), distance Y: " + d + (isInBoat ? " On boat" : "")
								+ ". Warn for fly: " + np.getWarn(this),
								hoverMsg(isInBoat ? "boat_air_below" : "air_below", "%nb%", nb));
			}
		} else
			np.contentDouble.remove("fly-air-below");
		
		Vector3d to = new Vector3d(toPosition.getX(), fromPosition.getX(), toPosition.getZ());
		double distanceWithoutY = to.distance(fromPosition);
		if(distanceWithoutY == distance && !p.isOnGround() && distance != 0 && p.getLocation().add(Vector3i.UNIT_Y).getBlockType().equals(BlockTypes.AIR)
				&& type.getId().contains("WATER") && !p.getVehicle().isPresent()) {
			if (np.contentBoolean.getOrDefault("fly-not-moving-y", false))
				mayCancel = SpongeNegativity.alertMod(
						np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this, 98,
						"Player not in ground but not moving Y. DistanceWithoutY: " + distanceWithoutY);
			np.contentBoolean.put("fly-not-moving-y", true);
		} else
			np.contentBoolean.put("fly-not-moving-y", false);
		if (isSetBack() && mayCancel) {
			Utils.teleportPlayerOnGround(p);
		}
	}

	@Listener
	public void boatManager(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		boolean nextValue = np.contentBoolean.getOrDefault("boat-falling", false);
		Optional<Entity> optVehicle = p.getVehicle();
		if(optVehicle.isPresent() && optVehicle.get().getType().equals(EntityTypes.BOAT)) {
			Location<World> from = e.getFromTransform().getLocation().copy(), to = e.getToTransform().getLocation().copy();
			double moveY = (to.getY() - from.getY());
			
			boolean wasWaterBelow = from.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
			boolean willWaterBelow = to.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
			if(wasWaterBelow && !willWaterBelow)
				nextValue = true;
			
			if(nextValue && !willWaterBelow && moveY >= 0)
				nextValue = false;
		} else {
			if(!nextValue)
				return; // already set to false, don't need to save it while put it in map
			nextValue = false;
		}
		
		np.contentBoolean.put("boat-falling", nextValue);
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
