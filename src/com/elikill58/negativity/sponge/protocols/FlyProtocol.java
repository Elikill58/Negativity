package com.elikill58.negativity.sponge.protocols;

import java.util.Optional;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class FlyProtocol extends Cheat {

	public FlyProtocol() {
		super("FLY", true, ItemTypes.FIREWORKS, true, true, "flyhack");
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p){
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		if(np.hasPotionEffect(PotionEffectTypes.SPEED)){
			int speed = 0;
			for(PotionEffect pe : np.getActiveEffects())
				if(pe.getType().equals(PotionEffectTypes.SPEED))
					speed = speed + pe.getAmplifier() + 1;
			if(speed > 40)
				return;
		}
		Optional<ItemStack> optChestplate = p.getChestplate();
		if(optChestplate.isPresent())
			if(optChestplate.get().getType().equals(ItemTypes.ELYTRA))
				return;
		
		double i = e.getToTransform().getPosition().distance(e.getFromTransform().getPosition());
		if (!p.getLocation().copy().sub(0, 1, 0).getBlock().getType().equals(BlockTypes.SPONGE)) {
			if (!(p.getVehicle() != null || np.isFlying()))
				if ((np.getFallDistance() == 0.0F)
						&& (p.getLocation().copy().add(0, 1, 0).getBlock().getType().equals(BlockTypes.AIR))
						&& i > 1.25D && !p.isOnGround()) {
					ReportType type = np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING;
					boolean mayCancel = SpongeNegativity.alertMod(type, p, this, Utils.parseInPorcent((int) i * 50),
							"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this));
					if(isSetBack() && mayCancel){
						Location<World> loc = p.getLocation().copy();
						while(loc.getBlock().getType().equals(BlockTypes.AIR)){
							loc.sub(0, 1, 0);
						}
						p.setLocation(loc.add(0, 1, 0));
					}
				}
		}
	}
	
}
