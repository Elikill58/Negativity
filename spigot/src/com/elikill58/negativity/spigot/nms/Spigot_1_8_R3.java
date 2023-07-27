package com.elikill58.negativity.spigot.nms;

import com.elikill58.negativity.spigot.utils.Utils;
import org.bukkit.OfflinePlayer;

public class Spigot_1_8_R3 extends SpigotVersionAdapter {

    public Spigot_1_8_R3() {
        super(47);
    }

    @Override
    public String getTpsFieldName() {
        return "h";
    }

    @Override
    public org.bukkit.inventory.ItemStack createSkull(OfflinePlayer owner) { // method used by old versions
        return Utils.createSkullOldVersion(owner);
    }
}
