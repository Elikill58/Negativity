package com.elikill58.negativity.spigot.impl;

import com.elikill58.negativity.api.potion.PotionEffectType;

public class SpigotPotionEffectType {

    public static org.bukkit.potion.PotionEffectType fromCommon(PotionEffectType type) {
        // TODO support direct namespaced
		/*if(Version.getVersion().isNewerOrEquals(Version.V1_13))
			return org.bukkit.potion.PotionEffectType.getByKey(NamespacedKey.minecraft(type.getId()));*/
        for (org.bukkit.potion.PotionEffectType pe : org.bukkit.potion.PotionEffectType.values()) {
            if (pe.getName().equalsIgnoreCase(type.name()) || type.getAlias().contains(pe.getName())) {
                return pe;
            }
        }
        return null;
    }

    public static PotionEffectType toCommon(org.bukkit.potion.PotionEffectType type) {
        for (PotionEffectType pe : PotionEffectType.values()) {
            if (type.getName().equalsIgnoreCase(pe.getId()) || type.getName().equalsIgnoreCase(pe.name())) {
                return pe;
            }
        }
        return null;
    }
}
