package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoPitchLimitProtocol extends Cheat {

	public NoPitchLimitProtocol() {
		super(CheatKeys.NO_PITCH_LIMIT, false, ItemTypes.SKULL, CheatCategory.PLAYER, true, "pitch");
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if(!SpongeNegativityPlayer.getNegativityPlayer(p).hasDetectionActive(this))
			return;
		double pitch = p.getHeadRotation().getX();
	    if (pitch <= -90.01D || pitch >= 90.01D) {
	    	boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(pitch < 0 ? pitch * -1 : pitch), "Strange head movements: " + pitch);
	    	if(mayCancel && isSetBack())
	    		e.setCancelled(true);
	    }
	}

}
