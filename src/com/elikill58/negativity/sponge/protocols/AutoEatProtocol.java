package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.NegativityPlayer;

public class AutoEatProtocol extends Cheat {

	public AutoEatProtocol() {
		super("AUTOEAT", true, ItemTypes.COOKED_BEEF, false, true, "fasteat");
	}
	
	@Listener
	public void onItemConsume(UseItemStackEvent.Finish e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		np.flyingReason = FlyingReason.EAT;
		np.eatMaterial = p.getItemInHand(HandTypes.MAIN_HAND).get().getType();
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
