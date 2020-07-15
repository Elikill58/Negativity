package com.elikill58.negativity.api.events.negativity;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.universal.Cheat;

public interface IPlayerCheatEvent extends Event {

	public Player getPlayer();

	public Cheat getCheat();

	public int getReliability();
}
