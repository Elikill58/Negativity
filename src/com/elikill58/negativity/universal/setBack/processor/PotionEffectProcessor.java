package com.elikill58.negativity.universal.setBack.processor;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.setBack.SetBackEntry;
import com.elikill58.negativity.universal.setBack.SetBackProcessor;

public class PotionEffectProcessor implements SetBackProcessor {

	private final PotionEffect potionEffect;
	
	public PotionEffectProcessor(SetBackEntry entry) {
		int duration = Integer.MAX_VALUE, amplifier = 0;
		if(entry.getValue().matches("\\*:\\*")) {
			String[] split =  entry.getValue().split(":");
			duration = Integer.parseInt(split[0]);
			amplifier = Integer.parseInt(split[1]);
		}
		this.potionEffect = new PotionEffect(PotionEffectType.fromName(entry.getKey()), duration, amplifier);
	}
	
	@Override
	public String getName() {
		return "potion_effect";
	}
	
	@Override
	public void perform(Player p) {
		Adapter.getAdapter().runSync(() -> p.addPotionEffect(potionEffect.clone()));
	}
}
