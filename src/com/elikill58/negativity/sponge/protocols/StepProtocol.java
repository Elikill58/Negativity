package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
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
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class StepProtocol extends Cheat {

	public StepProtocol() {
		super("STEP", false, ItemTypes.BRICK_STAIRS, true, true);
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		Location<World> from = e.getFromTransform().getLocation(), to = e.getToTransform().getLocation();
		double dif = from.getY() - to.getY();
		if (!np.hasPotionEffect(PotionEffectTypes.JUMP_BOOST)) {
			if (np.slime_block) {
				if (dif >= 0)
					np.slime_block = false;
			} else {
				Location<World> baseLoc = p.getLocation();
				boolean hasSlimeBlock = false;
				for (int u = 0; u < 360; u += 3)
					if (baseLoc.copy().add(Math.sin(u) * 3, -1, Math.cos(u) * 3).getBlock().getType().equals(BlockTypes.SLIME))
						hasSlimeBlock = true;
				if (hasSlimeBlock)
					np.slime_block = true;
				else {
					int ping = Utils.getPing(p), relia = Utils.parseInPorcent(dif * -500);
					if (dif > 0)
						return;
					if (dif < -1.499 && ping < 200) {
						boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, relia, "Warn for Step: "
								+ np.getWarn(this) + ". Move " + dif + "blocks up. ping: " + ping);
						if (isSetBack() && mayCancel)
							e.setCancelled(true);
					}
				}
			}
		}
	}

}
