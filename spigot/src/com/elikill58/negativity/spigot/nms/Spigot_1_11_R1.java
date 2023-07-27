package com.elikill58.negativity.spigot.nms;

import com.elikill58.negativity.spigot.utils.Utils;
import org.bukkit.OfflinePlayer;

public class Spigot_1_11_R1 extends SpigotVersionAdapter {

    public Spigot_1_11_R1() {
        super(316);
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
