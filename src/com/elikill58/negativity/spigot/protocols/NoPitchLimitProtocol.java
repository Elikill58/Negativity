package com.elikill58.negativity.spigot.protocols;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.FloatDataCounter;

public class NoPitchLimitProtocol extends Cheat implements Listener {

	public static final DataType<Float> PITCH = new DataType<Float>(() -> new FloatDataCounter("pitch", "Pitch"));
	
	public NoPitchLimitProtocol() {
		super(CheatKeys.NO_PITCH_LIMIT, false, ItemUtils.SKELETON_SKULL, CheatCategory.PLAYER, true, "pitch");
	}
	
	@EventHandler
	public void Check(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(!np.ACTIVE_CHEAT.contains(this))
			return;
		float pitch = p.getLocation().getPitch();
		np.verificatorForMod.forEach((s, verif) -> verif.getVerifData(this).ifPresent((data) -> data.getData(PITCH).add(pitch)));
	    if (pitch <= -90.01D || pitch >= 90.01D) {
	    	boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(pitch < 0 ? pitch * -1 : pitch), "Strange head movements: " + pitch);
	    	if(mayCancel && isSetBack())
	    		e.setCancelled(true);
	    }
	}
	
	@Override
	public String compile(VerifData data, NegativityPlayer np) {
		DataCounter<Float> counter = data.getData(PITCH);
		return Utils.coloredMessage("&6Pitch &7Min: " + String.format("%.2f", counter.getMin()) + "&7, Max: " + String.format("%.2f", counter.getMax()) + " &8(Normal when -90 < pitch < 90)");
	}
}
