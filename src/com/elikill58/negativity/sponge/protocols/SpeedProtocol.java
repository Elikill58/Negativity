package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.RideEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class SpeedProtocol extends Cheat {

	public SpeedProtocol() {
		super(CheatKeys.SPEED, false, ItemTypes.BEACON, CheatCategory.MOVEMENT, true, "speed", "speedhack");
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

		if (np.justDismounted) {
			// Dismounting a boat teleports the player, triggering a false positive
			return;
		}

		Vector3d fromVect = e.getFromTransform().getPosition();
		Vector3d toVect = e.getToTransform().getPosition();
		if (p.getLocation().sub(Vector3i.UNIT_Y).getBlockType().equals(BlockTypes.SPONGE)
				|| np.isFlying() || fromVect.getY() > toVect.getY() || p.get(Keys.WALKING_SPEED).get() > 2.0F
				|| np.hasPotionEffect(PotionEffectTypes.SPEED) || np.hasPotionEffect("DOLPHINS_GRACE") || p.getVehicle().isPresent()) {
			return;
		}

		if (np.BYPASS_SPEED != 0) {
			np.BYPASS_SPEED--;
			return;
		}

		if (canBoostWithPackedIce(p.getLocation())) {
			return;
		}

		ReportType type = (np.getWarn(this) > 7) ? ReportType.VIOLATION : ReportType.WARNING;
		boolean mayCancel = false;
		double distance = toVect.sub(0, toVect.getY(), 0).distance(fromVect.sub(0, fromVect.getY(), 0));
		String proof = "In ground: " + p.isOnGround() + "WalkSpeed: " + p.get(Keys.WALKING_SPEED).get() + "  Distance between from/to location: " + distance;
		if (p.isOnGround() && distance >= 0.75D) {
			mayCancel = SpongeNegativity.alertMod(type, p, this, UniversalUtils.parseInPorcent(distance * 100 * 2), proof,
					"Distance Last/New position: " + distance + "\n(With same Y)\nPlayer on ground", "Distance Last-New position: " + distance);
		} else if (!p.isOnGround()) {
			if(distance >= 0.85D) {
				mayCancel = SpongeNegativity.alertMod(type, p, this, UniversalUtils.parseInPorcent(distance * 100 * 2), proof,
						"Distance Last/New position: " + distance + "\n(With same Y)\nPlayer jumping", "Distance Last-New position: " + distance);
			} else {
				BlockType under = e.getToTransform().getLocation().copy().sub(0, 1, 0).getBlockType();
				if (under.getId().contains("STEP")) {
					double distanceWithY = e.getFromTransform().getPosition().distance(e.getToTransform().getPosition());
					if (distanceWithY > 0.4) {
						np.SPEED_NB++;
						if (np.SPEED_NB > 4)
							mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.SPEED), 86 + np.SPEED_NB, "HighSpeed - Block under: " + under.getId() + ", Speed: " + distanceWithY + ", nb: " + np.SPEED_NB);
					} else
						np.SPEED_NB = 0;
				}
			}
		}
		if (mayCancel && isSetBack()) {
			e.setCancelled(true);
		}
	}

	@Listener
	public void onEntityMount(RideEntityEvent.Mount event, @First Player player) {
		SpongeNegativityPlayer.getNegativityPlayer(player).justDismounted = true;
	}

	@Listener
	public void onEntityDismount(RideEntityEvent.Dismount event, @First Player player) {
		Task.builder()
				.delayTicks(3)
				.execute(() -> SpongeNegativityPlayer.getNegativityPlayer(player).justDismounted = false)
				.submit(SpongeNegativity.getInstance());
	}

	private boolean canBoostWithPackedIce(Location<World> feetLocation) {
		if (feetLocation.sub(Vector3i.UNIT_Y).getBlockType() != BlockTypes.PACKED_ICE) {
			return false;
		}

		BlockType blockTypeAtHead = feetLocation.add(Vector3i.UNIT_Y).getBlockType();
		return blockTypeAtHead == BlockTypes.TRAPDOOR || blockTypeAtHead == BlockTypes.IRON_TRAPDOOR;
	}

	@Listener
	public void onEntityDamage(DamageEntityEvent e) {
		Entity damaged = e.getTargetEntity();
		if (damaged instanceof Player) {
			SpongeNegativityPlayer.getNegativityPlayer((Player) damaged).BYPASS_SPEED = 3;
		}
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
