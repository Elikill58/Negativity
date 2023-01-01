package com.elikill58.negativity.sponge.inventories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.scheduler.Task;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.inventories.admin.AdminInventory;
import com.elikill58.negativity.sponge.inventories.admin.CheatManagerInventory;
import com.elikill58.negativity.sponge.inventories.admin.LangInventory;
import com.elikill58.negativity.sponge.inventories.admin.OneCheatInventory;
import com.elikill58.negativity.sponge.inventories.holders.NegativityHolder;

public abstract class AbstractInventory<T extends NegativityHolder> {

	private static final List<AbstractInventory<? extends NegativityHolder>> INVENTORIES = new ArrayList<>();
	private final InventoryType type;
	
	public AbstractInventory(InventoryType type) {
		this.type = type;
		INVENTORIES.add(this);
	}
	
	public InventoryType getType() {
		return type;
	}
	
	@Listener
	public void onClick(ClickInventoryEvent e, @First Player p) {
		if (e.getTransactions().isEmpty() || !(e.getTargetInventory() instanceof CarriedInventory<?>))
			return;
		
		CarriedInventory<?> carriedInventory = (CarriedInventory<?>) e.getTargetInventory();
		Optional<?> optCarrier = carriedInventory.getCarrier();
		if(!optCarrier.isPresent() || !(optCarrier.get() instanceof NegativityHolder))
			return;
		if(isInstance((NegativityHolder) optCarrier.get())) {
			e.setCancelled(true);
			ItemType m = e.getTransactions().get(0).getOriginal().getType();
			if (m.equals(ItemTypes.BARRIER))
				delayedInvClose(p);
			else
				manageInventory(e, m, p, (T) optCarrier.get());
		}
	}

	public abstract boolean isInstance(NegativityHolder nh);
	public abstract void openInventory(Player p, Object... args);
	public void closeInventory(Player p, InteractInventoryEvent.Close e) {}
	public abstract void manageInventory(ClickInventoryEvent e, ItemType m, Player p, T nh);
	public void actualizeInventory(Player p, Object... args) {}
	
	public static Optional<AbstractInventory<? extends NegativityHolder>> getInventory(InventoryType type) {
		for(AbstractInventory<? extends NegativityHolder> inv : INVENTORIES)
			if(inv.getType().equals(type))
				return Optional.of(inv);
		return Optional.empty();
	}
	
	public static void open(InventoryType type, Player p, Object... args) {
		getInventory(type).ifPresent((inv) -> inv.openInventory(p, args));
	}
	
	public static void init(SpongeNegativity np) {
		EventManager pm = Sponge.getEventManager();
		pm.registerListeners(np, new ActivedCheatInventory());
		pm.registerListeners(np, new AlertInventory());
		pm.registerListeners(np, new ModInventory());
		pm.registerListeners(np, new CheckMenuInventory());
		pm.registerListeners(np, new CheckMenuOfflineInventory());
		pm.registerListeners(np, new CheatManagerInventory());
		pm.registerListeners(np, new ForgeModsInventory());
		pm.registerListeners(np, new OneCheatInventory());
		pm.registerListeners(np, new AdminInventory());
		pm.registerListeners(np, new LangInventory());
		pm.registerListeners(np, new FreezeInventory());
	}

	protected static void delayedInvClose(Player player) {
		delayed(player::closeInventory);
	}

	protected static void delayed(Runnable action) {
		Task.builder()
				.execute(action)
				.submit(SpongeNegativity.getInstance());
	}
	
	public static enum InventoryType {
		ACTIVED_CHEAT,
		ADMIN,
		ALERT,
		CHECK_MENU,
		CHECK_MENU_OFFLINE,
		CHEAT_MANAGER,
		FREEZE,
		MOD,
		ONE_CHEAT,
		FORGE_MODS,
		LANG;
	}
}
