package com.elikill58.negativity.spigot.utils;

import java.lang.reflect.Method;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.universal.Version;

public class InventoryUtils {

	
	public static void fillInventory(Inventory inv, ItemStack item) {
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, item);
	}
	
	public static String getInventoryTitle(InventoryView inv) {
		try {
			Object nextInv = inv;
			if(!Version.getVersion().isNewerOrEquals(Version.V1_14)) {
				nextInv = inv.getTopInventory();
			}
			Method getTitle = nextInv.getClass().getMethod("getTitle");
			getTitle.setAccessible(true);
			return (String) getTitle.invoke(nextInv);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getInventoryName(InventoryClickEvent e) {
		try {
			if(Version.getVersion().isNewerOrEquals(Version.V1_14)) {
				Method m = e.getView().getClass().getMethod("getTitle");
				m.setAccessible(true);
				return (String) m.invoke(e.getView());
			} else {
				Method m = e.getClickedInventory().getClass().getMethod("getName");
				m.setAccessible(true);
				return (String) m.invoke(e.getClickedInventory());
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
}
