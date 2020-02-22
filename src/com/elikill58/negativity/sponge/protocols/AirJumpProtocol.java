package com.elikill58.negativity.sponge.protocols;

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

public class AirJumpProtocol extends Cheat {

	public AirJumpProtocol() {
		super(CheatKeys.AIR_JUMP, false, ItemTypes.FEATHER, false, true, "air jump", "air", "jump");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (p.get(Keys.IS_FLYING).orElse(false) || p.getVehicle().isPresent() || p.get(Keys.IS_ELYTRA_FLYING).orElse(false))
			return;
		Location<World> loc = p.getLocation();
		double temp = e.getToTransform().getLocation().getY() - e.getFromTransform().getLocation().getY();
		if (temp > 0.35 && np.lastYDiff < temp && !np.hasOtherThanExtended(loc.copy(), BlockTypes.AIR)
				&& !np.hasOtherThanExtended(loc.copy().sub(0, 1, 0), BlockTypes.AIR)
				&& !np.hasOtherThanExtended(loc.copy().sub(0, 2, 0), BlockTypes.AIR)) {
			boolean mayCancel = SpongeNegativity.alertMod(
					temp > 0.5 && np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					Utils.parseInPorcent((int) (temp * 210) - Utils.getPing(p)),
					"Actual diff Y: " + np.lastYDiff + ", last diff Y: " + temp + ", ping: " + Utils.getPing(p)
							+ ". Warn for AirJump: " + np.getWarn(this));
			if (isSetBack() && mayCancel)
				Utils.teleportPlayerOnGround(p);
		}
		np.lastYDiff = temp;
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
