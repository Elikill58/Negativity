package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class NoPitchLimitProtocol extends Cheat {

	public static final DataType<Double> PITCH = new DataType<Double>("pitch", "Pitch", () -> new DoubleDataCounter());
	
	public NoPitchLimitProtocol() {
		super(CheatKeys.NO_PITCH_LIMIT, false, ItemTypes.SKULL, CheatCategory.PLAYER, true, "pitch");
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this))
			return;
		double pitch = p.getHeadRotation().getX();
		recordData(p.getUniqueId(), PITCH, pitch);
	    if (pitch <= -90.01D || pitch >= 90.01D) {
	    	boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(pitch < 0 ? pitch * -1 : pitch), "Strange head movements: " + pitch);
	    	if(mayCancel && isSetBack())
	    		e.setCancelled(true);
	    }
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> counter = data.getData(PITCH);
		return Utils.coloredMessage("&6Pitch &7Min: " + String.format("%.2f", counter.getMin()) + "&7, Max: " + String.format("%.2f", counter.getMax()) + " &8(Normal when -90 < pitch < 90)");
	}
}
