package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class NukerProtocol extends Cheat {

	public NukerProtocol() {
		super(CheatKeys.NUKER, true, ItemTypes.BEDROCK, CheatCategory.WORLD, true, "breaker", "bed breaker", "bedbreaker");
	}
	
	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if(e.getTransactions().size() == 0)
			return;
		BlockSnapshot breakedBlock = e.getTransactions().get(0).getOriginal();
		BlockRay<World> blockRay = BlockRay.from(p).skipFilter(BlockRay.onlyAirFilter()).stopFilter(BlockRay.onlyAirFilter()).build();
		BlockRayHit<World> target = blockRay.end().orElse(null);
		if(target != null) {
			double distance = target.getLocation().getPosition().distance(breakedBlock.getLocation().get().getPosition());
			if (e.getTransactions().stream().filter(tr -> !tr.getOriginal().getState().getType().equals(breakedBlock.getState().getType())).count() > 0 && distance > 3.5) {
				boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent(distance * 15 - Utils.getPing(p)), "BlockDig " + breakedBlock.getState().getType().getName() + ", player see " + target.getLocation().getBlock().getType().getName() + ". Distance between blocks " + distance + " block. Ping: " + Utils.getPing(p) + ". Warn: " + np.getWarn(this));				
				if(isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		long temp = System.currentTimeMillis(), dis = temp - np.LAST_BLOCK_BREAK;
		if(dis < 50 && breakedBlock.getProperty(SolidCubeProperty.class).get().getValue()) {
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, (int) (100 - dis),
					"Type: " + breakedBlock.getState().getType().getName() + ". Last: " + np.LAST_BLOCK_BREAK + ", Now: " + temp + ", diff: " + dis + " (ping: " + Utils.getPing(p) + "). Warn: " + np.getWarn(this));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
		np.LAST_BLOCK_BREAK = temp;
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
