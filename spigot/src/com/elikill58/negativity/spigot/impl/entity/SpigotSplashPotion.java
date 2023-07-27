package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.api.entity.SplashPotion;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import org.bukkit.entity.ThrownPotion;

import java.util.List;
import java.util.stream.Collectors;

public class SpigotSplashPotion extends SpigotEntity<ThrownPotion> implements SplashPotion {

    public SpigotSplashPotion(ThrownPotion po) {
        super(po);
    }

    @Override
    public List<PotionEffect> getEffects() {
        return entity.getEffects().stream()
                .map(pe -> new PotionEffect(PotionEffectType.fromName(pe.getType().getName()), pe.getDuration(),
                        pe.getAmplifier()))
                .collect(Collectors.toList());
    }
}
