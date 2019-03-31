package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.flowpowered.math.vector.Vector3d;

public class SpeedHackProtocol extends Cheat {

	public SpeedHackProtocol() {
		super("SPEEDHACK", false, ItemTypes.BEACON, true, true, "speed");
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		Transform<World> from = e.getFromTransform(), to = e.getToTransform();
		Vector3d toVect = to.getPosition(), fromVect = from.getPosition();
		double y = toVect.clone().sub(0, toVect.getY(), 0).distance(fromVect.clone().sub(0, fromVect.getY(), 0));
		if (p.getLocation().sub(0, 1, 0).getBlock().getType().equals(BlockTypes.SPONGE) || p.getVehicle() != null
				|| np.isFlying() || from.getYaw() > to.getYaw() || p.get(Keys.WALKING_SPEED).get() > 2.0F
				|| p.getOrCreate(PotionEffectData.class).get().contains(PotionEffect.of(PotionEffectTypes.SPEED, 1, 1)))
			return;
		if (np.BYPASS_SPEED != 0) {
			np.BYPASS_SPEED--;
			return;
		}
		ReportType type = (np.getWarn(this) > 7) ? ReportType.VIOLATION : ReportType.WARNING;
		boolean mayCancel = false;
		String proof = "In ground: " + p.isOnGround() + "WalkSpeed: " + p.get(Keys.WALKING_SPEED) + "  Distance between from/to location: " + y;
		if (p.isOnGround() && y >= 0.75D) {
			SpongeNegativity.alertMod(type, p, this, Utils.parseInPorcent(y * 100 * 2), proof,
					"Distance Last/New position: " + y + "\n(With same Y)\nPlayer on ground");
		} else if (!p.isOnGround() && y >= 0.85D) {
			mayCancel = SpongeNegativity.alertMod(type, p, this, Utils.parseInPorcent(y * 100 * 2), proof,
					"Distance Last/New position: " + y + "\n(With same Y)\nPlayer jumping");
		}
		if (mayCancel && isSetBack())
			e.setCancelled(true);
	}

	@Listener
	public void onEntityDamage(DamageEntityEvent e, @First Player p) {
		SpongeNegativityPlayer.getNegativityPlayer(p).BYPASS_SPEED = 2;
	}
}
