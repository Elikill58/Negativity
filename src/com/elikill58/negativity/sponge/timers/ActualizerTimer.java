package com.elikill58.negativity.sponge.timers;

import java.util.function.Consumer;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.property.Identifiable;
import org.spongepowered.api.scheduler.Task;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.inventories.AbstractInventory;
import com.elikill58.negativity.sponge.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ActualizerTimer implements Consumer<Task> {

    public static final boolean INV_FREEZE_ACTIVE = Adapter.getAdapter().getConfig().getBoolean("inventory.inv_freeze_active");

	@Override
    public void accept(Task task) {
        for (Player p : Inv.CHECKING.keySet()) {
            if (p.getOpenInventory().get().getName().get().equals(Inv.NAME_ACTIVED_CHEAT_MENU)) {
            } else if (p.getOpenInventory().get().getName().get().equals(Inv.NAME_CHECK_MENU))
                AbstractInventory.getInventory(InventoryType.CHECK_MENU).ifPresent((inv) -> inv.actualizeInventory(p, Inv.CHECKING.get(p)));
            else if (p.getOpenInventory().get().getName().get().equals(Inv.NAME_ALERT_MENU))
                AbstractInventory.getInventory(InventoryType.ALERT).ifPresent((inv) -> inv.actualizeInventory(p, Inv.CHECKING.get(p)));
            else
                Inv.CHECKING.remove(p);
        }
        for (Player p : Utils.getOnlinePlayers()) {
        	SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
            if (np.isFreeze && INV_FREEZE_ACTIVE) {
                Container openInventory = p.getOpenInventory().orElse(null);
                if (openInventory == null || !Inv.FREEZE_INV_ID.equals(openInventory.getProperty(Identifiable.class, Inv.INV_ID_KEY).orElse(null))) {
                    AbstractInventory.open(InventoryType.FREEZE, p);
                }
            }

            if(np.BETTER_CLICK < np.ACTUAL_CLICK)
                np.BETTER_CLICK = np.ACTUAL_CLICK;

            np.LAST_CLICK = np.ACTUAL_CLICK;
            np.ACTUAL_CLICK = 0;
        }
    }
}
