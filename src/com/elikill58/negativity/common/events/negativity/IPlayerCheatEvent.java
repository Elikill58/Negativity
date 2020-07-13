package com.elikill58.negativity.common.events.negativity;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;
import com.elikill58.negativity.universal.Cheat;

public interface IPlayerCheatEvent extends Event {

	public Player getPlayer();

	public Cheat getCheat();

	public int getReliability();
}
