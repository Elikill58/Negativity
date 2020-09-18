package com.elikill58.negativity.api.inventory;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.item.Material;

public abstract class AbstractInventory<T extends NegativityHolder> {

	public static final List<AbstractInventory<?>> INVENTORIES = new ArrayList<>();
	
	private final NegativityInventory type;
	private final Class<T> holderExample;
	
	public AbstractInventory(NegativityInventory type, Class<T> holderExample) {
		this.type = type;
		this.holderExample = holderExample;
		INVENTORIES.add(this);
	}
	
	/**
	 * The Negativity inventory type
	 * @return
	 */
	public NegativityInventory getType() {
		return type;
	}

	/**
	 * Check if the given inventory holder instance of this inventory holder
	 * 
	 * @param nh the holder to check
	 * @return true if the holder instance of this inventory's holder
	 */
	public boolean isInstance(NegativityHolder nh) {
		return nh.getClass().isAssignableFrom(holderExample);
	}
	
	/**
	 * Open the inventory to the specified player.
	 * Some arguments can be required for some inventory.
	 * 
	 * @param p the player that will see the inventory
	 * @param args if there is something required
	 */
	public abstract void openInventory(Player p, Object... args);
	/**
	 * Event when the inventory is closed.
	 * 
	 * @param p the player which say it
	 * @param e the called inventory
	 */
	public void closeInventory(Player p, InventoryCloseEvent e) {}
	/**
	 * Event called when click and already checked
	 * (Check done: item/Inventory exist, available slot)
	 * 
	 * @param e the called event
	 * @param m the material of the clicked item
	 * @param p the player
	 * @param nh the holder
	 */
	public abstract void manageInventory(InventoryClickEvent e, Material m, Player p, T nh);
	/**
	 * Actualize inventory
	 * Same as {@link AbstractInventory#openInventory}, args can be required
	 * 
	 * @param p the player which see the inventory
	 * @param args the args
	 */
	public void actualizeInventory(Player p, Object... args) {}
	
	public static enum NegativityInventory {
		ACTIVED_CHEAT,
		ADMIN,
		ALERT,
		CHECK_MENU,
		CHEAT_MANAGER,
		FREEZE,
		MOD,
		ONE_CHEAT,
		FORGE_MODS,
		LANG,
		REPORT;
	}
}
