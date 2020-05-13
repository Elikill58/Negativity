package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.Identifiable;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;

public class InventoryClickManagerEvent {

	@Listener
	public void onClick(ClickInventoryEvent e, @First Player p) {
		if (e.getTransactions().isEmpty())
			return;

		Identifiable invId = e.getTargetInventory().getProperty(Identifiable.class, Inv.INV_ID_KEY).orElse(null);
		if (invId == null)
			return;

		if (invId.equals(Inv.FREEZE_INV_ID)) {
			e.setCancelled(true);
		}
	}

	public static void updateItemName(ClickInventoryEvent e, boolean state, SlotTransaction transaction, ItemStackSnapshot clickedItem, Player player, SpongeNegativityPlayer np, String diplaynameKey, String statePlaceholder) {
		ItemStack stack = clickedItem.createStack();
		String stateString = Messages.getStringMessage(player, "inventory.manager." + (state ? "enabled" : "disabled"));
		Text displayName = Messages.getMessage(np.getAccount(), diplaynameKey, statePlaceholder, stateString);
		stack.offer(Keys.DISPLAY_NAME, displayName);
		transaction.setCustom(stack);
		e.getCursorTransaction().setValid(false);
	}

	public static void delayedInvClose(Player player) {
		delayed(player::closeInventory);
	}

	public static void delayed(Runnable action) {
		Task.builder()
				.execute(action)
				.submit(SpongeNegativity.getInstance());
	}
}
