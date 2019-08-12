package com.elikill58.negativity.universal.ban.support;

import java.util.List;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ban.BanRequest;

public abstract class BanPluginSupport {

	public abstract void ban(NegativityPlayer ac, String reason, String banner, long time);
	public abstract void banDef(NegativityPlayer ac, String reason, String banner);
	public abstract void kick(NegativityPlayer ac, String reason);
	public abstract List<BanRequest> getBan(NegativityPlayer ac);
	
}
