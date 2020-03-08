package com.elikill58.negativity.spigot.protocols;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoPitchLimitProtocol extends Cheat implements Listener {

	public NoPitchLimitProtocol() {
		super(CheatKeys.NO_PITCH_LIMIT, false, Utils.getMaterialWith1_15_Compatibility("SKULL_ITEM", "LEGACY_SKULL_ITEM"), CheatCategory.PLAYER, true, "pitch");
	}
	
	@EventHandler
	public void Check(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!SpigotNegativityPlayer.getNegativityPlayer(p).ACTIVE_CHEAT.contains(this))
			return;
		float pitch = p.getLocation().getPitch();
	    if (pitch <= -90.01D || pitch >= 90.01D) {
	    	boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(pitch < 0 ? pitch * -1 : pitch), "Strange head movements: " + pitch);
	    	if(mayCancel && isSetBack())
	    		e.setCancelled(true);
	    }
	}
}
