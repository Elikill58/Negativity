package com.elikill58.negativity.protocols;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerMoveEvent;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.FloatDataCounter;

public class NoPitchLimit extends Cheat implements Listeners {

	public static final DataType<Float> PITCH = new DataType<Float>("pitch", "Pitch", () -> new FloatDataCounter());
	
	public NoPitchLimit() {
		super(CheatKeys.NO_PITCH_LIMIT, false, Materials.SKELETON_SKULL, CheatCategory.PLAYER, true, "pitch");
	}
	
	@EventListener
	public void Check(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this))
			return;
		float pitch = p.getLocation().getPitch();
		recordData(p.getUniqueId(), PITCH, pitch);
	    if (pitch <= -90.01D || pitch >= 90.01D) {
	    	boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(pitch < 0 ? pitch * -1 : pitch), "Strange head movements: " + pitch);
	    	if(mayCancel && isSetBack())
	    		e.setCancelled(true);
	    }
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Float> counter = data.getData(PITCH);
		return Utils.coloredMessage("&6Pitch &7Min: " + String.format("%.2f", counter.getMin()) + "&7, Max: " + String.format("%.2f", counter.getMax()) + " &8(Normal when -90 < pitch < 90)");
	}
}
