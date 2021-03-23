package com.elikill58.negativity.sponge.protocols;

import static com.elikill58.negativity.sponge.utils.LocationUtils.hasOtherThan;

import java.text.NumberFormat;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
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
import com.elikill58.negativity.sponge.support.EssentialsSupport;
import com.elikill58.negativity.sponge.utils.LocationUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class SpeedProtocol extends Cheat {

	private NumberFormat numberFormat = NumberFormat.getInstance();
	
	public SpeedProtocol() {
		super(CheatKeys.SPEED, false, ItemTypes.BEACON, CheatCategory.MOVEMENT, true, "speed", "speedhack");
		numberFormat.setMaximumFractionDigits(4);
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
		np.MOVE_TIME++;
		if (np.MOVE_TIME > 60) {
			boolean b = SpongeNegativity.alertMod(np.MOVE_TIME > 100 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, UniversalUtils.parseInPorcent(np.MOVE_TIME * 2), "Move " + np.MOVE_TIME + " times. Ping: "
							+ Utils.getPing(p) + " Warn for Speed: " + np.getWarn(this));
			if (b && isSetBack())
				e.setCancelled(true);
		}
		if (np.justDismounted) {
			// Dismounting a boat teleports the player, triggering a false positive
			return;
		}
		Location<World> from = e.getFromTransform().getLocation(), to = e.getToTransform().getLocation();
		Vector3d fromVect = e.getFromTransform().getPosition().clone();
		Vector3d toVect = e.getToTransform().getPosition().clone();
		if (p.getLocation().sub(Vector3i.UNIT_Y).getBlockType().equals(BlockTypes.SPONGE)
				|| np.isFlying() || np.getWalkSpeed() > 2.0F || hasEnderDragonAround(p) || p.get(Keys.FLYING_SPEED).get() > 3.0F
				|| np.hasPotionEffect(PotionEffectTypes.SPEED) || np.hasPotionEffect("DOLPHINS_GRACE") || p.getVehicle().isPresent()) {
			return;
		}

		if (np.BYPASS_SPEED != 0) {
			np.BYPASS_SPEED--;
			return;
		}

		if (canBoostWithPackedIce(p.getLocation()) || LocationUtils.has(p.getLocation(), "SLAB", "STAIRS"))
			return;
		if (LocationUtils.has(p.getLocation().copy().add(0, 1, 0), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET")
				|| LocationUtils.has(p.getLocation().copy().add(0, 2, 0), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET")
				|| LocationUtils.has(p.getLocation().copy().sub(0, 1, 0), "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET"))
			return;

		ReportType type = (np.getWarn(this) > 7) ? ReportType.VIOLATION : ReportType.WARNING;
		boolean mayCancel = false;
		double moveY = toVect.sub(0, toVect.getY(), 0).distance(fromVect.sub(0, fromVect.getY(), 0));
		if (p.isOnGround()) {
			double walkSpeed = SpongeNegativity.essentialsSupport ? (np.getWalkSpeed() - EssentialsSupport.getEssentialsRealMoveSpeed(p)) : np.getWalkSpeed();
			boolean walkTest = moveY > walkSpeed * 3.1 && moveY > 0.65D, walkWithEssTest = (moveY - walkSpeed > (walkSpeed * 2.5));
			if((SpongeNegativity.essentialsSupport ? (walkWithEssTest || (np.getWalkSpeed() < 0.35 && moveY >= 0.75D)) : moveY >= 0.75D) || walkTest){
				int porcent = UniversalUtils.parseInPorcent(moveY * 50 + UniversalUtils.getPorcentFromBoolean(walkTest, 20)
						+ UniversalUtils.getPorcentFromBoolean(walkWithEssTest == walkTest, 20)
						+ UniversalUtils.getPorcentFromBoolean(walkWithEssTest, 10));
				mayCancel = SpongeNegativity.alertMod(type, p, this, porcent,
						"Player in ground. WalkSpeed: " + walkSpeed + ", Distance between from/to location: " + moveY + ", walkTest: " + walkTest +
						", walkWithEssentialsTest: " + walkWithEssTest, hoverMsg("distance_ground", "%distance%", numberFormat.format(moveY)));
			}
			double calculatedSpeedWithoutY = Utils.getSpeed(from, to);
			if(np.getWalkSpeed() < 1.0 && calculatedSpeedWithoutY > (np.getWalkSpeed() + 0.01) && p.getVelocity().getY() < calculatedSpeedWithoutY && hasOtherThan(from.copy().add(0, 1, 0), BlockTypes.AIR)) { // "+0.01" if to prevent lag"
				mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, 90, "Calculated speed: " + calculatedSpeedWithoutY + ", Walk Speed: " + np.getWalkSpeed() + ", Velocity Y: " + p.getVelocity().getY());
			}
		} else if (!p.isOnGround()) {
			for(Entity et : p.getNearbyEntities(5))
				if(et.getType().equals(EntityTypes.CREEPER))
					return;
			if(moveY >= 0.85D && (np.getWalkSpeed() * 1.1) < moveY) {
				String proof = "In ground: " + p.isOnGround() + " WalkSpeed: " + p.get(Keys.WALKING_SPEED).get() + "  Distance between from/to location: " + moveY;
				mayCancel = SpongeNegativity.alertMod(type, p, this, UniversalUtils.parseInPorcent(moveY * 100 * 2), proof,
						hoverMsg("distance_jumping", "%distance%", numberFormat.format(moveY)));
			} else {
				BlockType under = to.copy().sub(0, 1, 0).getBlockType();
				if (!under.getId().contains("STEP")) {
					double distance = fromVect.distance(toVect);

					Vector3d fromPosition = e.getFromTransform().getPosition();
					Vector3d toPosition = e.getToTransform().getPosition();
					Vector3d toVec = new Vector3d(toPosition.getX(), fromPosition.getX(), toPosition.getZ());
					double distanceWithoutY = toVec.distance(fromPosition);
					if (distance > 0.4 && (distance > (distanceWithoutY * 2)) && np.getFallDistance() < 1) {
						np.SPEED_NB++;
						if (np.SPEED_NB > 4)
							mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(86 + np.SPEED_NB),
									"HighSpeed - Block under: " + under.getId() + ", Speed: " + distance + ", nb: " + np.SPEED_NB + ", fallDistance: " + np.getFallDistance());
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
	
	private boolean hasEnderDragonAround(Player p) {
		for(Entity et : p.getWorld().getEntities())
			if(et.getType().equals(EntityTypes.ENDER_DRAGON) && et.getLocation().getPosition().distance(p.getLocation().getPosition()) < 15)
				return true;
		return false;
	}

	@Listener
	public void onEntityDamage(DamageEntityEvent e) {
		Entity damaged = e.getTargetEntity();
		if (damaged instanceof Player)
			SpongeNegativityPlayer.getNegativityPlayer((Player) damaged).BYPASS_SPEED = 3;
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
