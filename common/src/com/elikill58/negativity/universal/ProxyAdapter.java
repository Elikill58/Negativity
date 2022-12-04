package com.elikill58.negativity.universal;

import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.FakePlayer;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;

public abstract class ProxyAdapter extends Adapter {

	@Override
	public Version getServerVersion() {
		return Version.HIGHER;
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		throw new UnsupportedOperationException("ItemRegistrar is unsupported on proxies");
	}

	@Override
	public ItemBuilder createItemBuilder(Material type) {
		throw new UnsupportedOperationException("ItemBuilder is unsupported on proxies");
	}

	@Override
	public ItemBuilder createItemBuilder(ItemStack item) {
		throw new UnsupportedOperationException("ItemBuilder is unsupported on proxies");
	}

	@Override
	public ItemBuilder createItemBuilder(String type) {
		throw new UnsupportedOperationException("ItemBuilder is unsupported on proxies");
	}

	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		throw new UnsupportedOperationException("ItemBuilder is unsupported on proxies");
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(OfflinePlayer owner) {
		throw new UnsupportedOperationException("ItemBuilder is unsupported on proxies");
	}
	
	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		throw new UnsupportedOperationException("Inventory is unsupported on proxies");
	}

	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		throw new UnsupportedOperationException("FakePlayer is unsupported on proxies");
	}
	
	/*@Override
	public Scheduler getScheduler() {
		throw new UnsupportedOperationException("Scheduler can't be used on proxy servers");
	}*/
	
	@Override
	public double[] getTPS() {
		throw new UnsupportedOperationException("Proxies don't have TPS");
	}
	
	@Override
	public double getLastTPS() {
		throw new UnsupportedOperationException("Proxies don't have TPS");
	}
	
	@Override
	public @Nullable UUID getUUID(String name) {
		Player p = getPlayer(name);
		return p == null ? null : p.getUniqueId();
	}
	
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(String name) {
		throw new UnsupportedOperationException("OfflinePlayer is unsupported on proxies");
	}
	
	@Override
	public @Nullable OfflinePlayer getOfflinePlayer(UUID uuid) {
		throw new UnsupportedOperationException("OfflinePlayer is unsupported on proxies");
	}

	@Override
	public VersionAdapter<?> getVersionAdapter() {
		throw new UnsupportedOperationException("VersionAdapter is unsupported on proxies");
	}
	
	@Override
	public World getServerWorld(Player p) {
		throw new UnsupportedOperationException("ServerWorld is unsupported on proxies");
	}
}
