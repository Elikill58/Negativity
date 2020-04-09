package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
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

		if (p.getVehicle().isPresent() || np.justDismounted) {
			// Some cases like jumping with a horse may trigger false positives,
			// dismounting while it is jumping also triggers false positives
			return;
		}

		if (p.get(Keys.CAN_FLY).orElse(false) || p.get(Keys.IS_ELYTRA_FLYING).orElse(false))
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
		if (!(p.get(Keys.IS_SPRINTING).orElse(false) && (toPosition.getY() - fromPosition.getY()) > 0)) {
			if (blockTypeBelow != BlockTypes.SPONGE
					&& (p.getVehicle().isPresent() || p.get(Keys.CAN_FLY).orElse(false))
					&& np.getFallDistance() == 0.0F
					&& p.getLocation().getBlockRelative(Direction.UP).getBlockType() == BlockTypes.AIR
					&& distance > 1.25D && !p.isOnGround()) {
				ReportType type = np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING;
				mayCancel = SpongeNegativity.alertMod(type, p, this, UniversalUtils.parseInPorcent((int) distance * 50),
						"Player not in ground, distance: " + distance + ". Warn for fly: " + np.getWarn(this));
				if (isSetBack() && mayCancel) {
					Location<World> loc = p.getLocation();
					while (loc.getBlockType().equals(BlockTypes.AIR)) {
						loc = loc.sub(Vector3i.UNIT_Y);
					}
					p.setLocation(loc.add(Vector3i.UNIT_Y));
				}
			}
	
			if (!np.hasOtherThanExtended(p.getLocation(), BlockTypes.AIR)
					&& !np.hasOtherThanExtended(p.getLocation().add(0, -1, 0), BlockTypes.AIR)
					&& !np.hasOtherThanExtended(p.getLocation().add(0, -2, 0), BlockTypes.AIR)
					&& fromPosition.getY() <= toPosition.getY()) {
				double d = toPosition.getY() - fromPosition.getY();
				int nb = getNbAirBlockDown(np);
				int porcent = UniversalUtils.parseInPorcent(nb * 15 + d);
				if (np.hasOtherThan(p.getLocation().add(0, -3, 0), BlockTypes.AIR))
					porcent = UniversalUtils.parseInPorcent(porcent - 15);
				mayCancel = SpongeNegativity.alertMod(
						np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this, porcent,
						"Player not in ground (" + nb + " air blocks down), distance Y: " + d + ". Warn for fly: " + np.getWarn(this));
				if (isSetBack() && mayCancel) {
					Utils.teleportPlayerOnGround(p);
				}
			}
		}
		
		Vector3d to = new Vector3d(toPosition.getX(), fromPosition.getX(), toPosition.getZ());
		double distanceWithoutY = to.distance(fromPosition);
		if(distanceWithoutY == distance && !p.isOnGround() && distance != 0 && p.getLocation().add(Vector3i.UNIT_Y).getBlockType().equals(BlockTypes.AIR)) {
			mayCancel = SpongeNegativity.alertMod(
					np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this, 98,
					"Player not in ground but not moving Y. DistanceWithoutY: " + distanceWithoutY);
		}
		if (isSetBack() && mayCancel) {
			Utils.teleportPlayerOnGround(p);
		}
	}

	private int getNbAirBlockDown(SpongeNegativityPlayer np) {
		Location<World> loc = np.getPlayer().getLocation();
		int i = 0;
		while (!np.hasOtherThanExtended(loc, BlockTypes.AIR) && i < 20) {
			loc = loc.sub(Vector3i.UNIT_Y);
			i++;
		}
		return i;
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
