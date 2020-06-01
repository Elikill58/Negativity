package com.elikill58.negativity.spigot.inventories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.elikill58.negativity.spigot.inventories.admin.AdminInventory;
import com.elikill58.negativity.spigot.inventories.admin.CheatManagerInventory;
import com.elikill58.negativity.spigot.inventories.admin.LangInventory;
import com.elikill58.negativity.spigot.inventories.admin.OneCheatInventory;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.ItemUtils;

public abstract class AbstractInventory implements Listener {

	private static final List<AbstractInventory> INVENTORIES = new ArrayList<>();
	private final InventoryType type;
	
	public AbstractInventory(InventoryType type) {
		this.type = type;
		INVENTORIES.add(this);
	}
	
	public InventoryType getType() {
		return type;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getClickedInventory() == null || !(e.getWhoClicked() instanceof Player))
			return;
		InventoryHolder holder = e.getClickedInventory().getHolder();
		if(!(holder instanceof NegativityHolder))
			return;
		if(isInstance((NegativityHolder) holder)) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			Material m = e.getCurrentItem().getType();
			if (m.equals(ItemUtils.MATERIAL_CLOSE))
				p.closeInventory();
			else
				manageInventory(e, m, p, (NegativityHolder) holder);
		}
	}

	public abstract boolean isInstance(NegativityHolder nh);
	public abstract void openInventory(Player p, Object... args);
	public void closeInventory(Player p, InventoryCloseEvent e) {}
	public abstract void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh);
	public void actualizeInventory(Player p, Object... args) {}
	
	public static Optional<AbstractInventory> getInventory(InventoryType type) {
		for(AbstractInventory inv : INVENTORIES)
			if(inv.getType().equals(type))
				return Optional.of(inv);
		return Optional.empty();
	}
	
	public static void open(InventoryType type, Player p, Object... args) {
		getInventory(type).ifPresent((inv) -> inv.openInventory(p, args));
	}
	
	public static void init(Plugin pl) {
		PluginManager pm = pl.getServer().getPluginManager();
		pm.registerEvents(new ActivedCheatInventory(), pl);
		pm.registerEvents(new AlertInventory(), pl);
		pm.registerEvents(new ModInventory(), pl);
		pm.registerEvents(new CheckMenuInventory(), pl);
		pm.registerEvents(new CheatManagerInventory(), pl);
		pm.registerEvents(new ForgeModsInventory(), pl);
		pm.registerEvents(new OneCheatInventory(), pl);
		pm.registerEvents(new AdminInventory(), pl);
		pm.registerEvents(new LangInventory(), pl);
		pm.registerEvents(new FreezeInventory(), pl);
	}
	
	public static enum InventoryType {
		ACTIVED_CHEAT,
		ADMIN,
		ALERT,
		CHECK_MENU,
		CHEAT_MANAGER,
		FREEZE,
		MOD,
		ONE_CHEAT,
		FORGE_MODS,
		LANG;
	}
}
