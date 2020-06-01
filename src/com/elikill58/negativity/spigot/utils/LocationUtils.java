package com.elikill58.negativity.spigot.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

public class LocationUtils {

	/**
	 * Check if there is material around specified location
	 * (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param ms Material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasMaterialAround(Location loc, Material... ms) {
		List<Material> m = Arrays.asList(ms);
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		return false;
	}

	/**
	 * Check if there is material around specified location
	 * (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param ms Material's name that we are searchingWarn: For 'REDSTONE', we will also find 'REDSTONE_BLOCK' and all other block with contains name ...
	 * @return true if one of specified material if around
	 */
	public static boolean hasMaterialsAround(Location loc, String... ms) {
		for(String s : ms) {
			if (loc.add(0, 0, 1).getBlock().getType().name().contains(s))
				return true;
			if (loc.add(1, 0, 0).getBlock().getType().name().contains(s))
				return true;
			if (loc.add(0, 0, -1).getBlock().getType().name().contains(s))
				return true;
			if (loc.add(0, 0, -1).getBlock().getType().name().contains(s))
				return true;
			if (loc.add(-1, 0, 0).getBlock().getType().name().contains(s))
				return true;
			if (loc.add(-1, 0, 0).getBlock().getType().name().contains(s))
				return true;
			if (loc.add(0, 0, 1).getBlock().getType().name().contains(s))
				return true;
			if (loc.add(0, 0, 1).getBlock().getType().name().contains(s))
				return true;
		}
		return false;
	}

	/**
	 * Check if there is other than material around specified location.
	 * (2 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m The material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThanExtended(Location loc, Material m) {
		return hasOtherThanExtended(loc, m.name());
	}

	/**
	 * Check if there is other than material around specified location.
	 * (2 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m the name that we are searching in material names
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThanExtended(Location loc, String m) {
		Location tempLoc = loc.clone();
		loc = loc.clone();
		if (!loc.getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		loc = tempLoc;
		if (!loc.add(0, 0, 2).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, -1).getBlock().getType().name().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, 1).getBlock().getType().name().contains(m))
				return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		return false;
	}

	/**
	 * Check if there is material around specified location.
	 * (2 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m the name that we are searching in material names
	 * @return true if one of specified material if around
	 */
	public static boolean hasExtended(Location loc, String m) {
		Location tempLoc = loc.clone();
		loc = loc.clone();
		if (loc.getBlock().getType().name().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(0, 0, -1).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(0, 0, -1).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		loc = tempLoc;
		if (loc.add(0, 0, 2).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(0, 0, -1).getBlock().getType().name().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(0, 0, 1).getBlock().getType().name().contains(m))
				return true;
		if (loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		return false;
	}

	/**
	 * Check if there is other than material around specified location.
	 * (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m The material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThan(Location loc, Material m) {
		return hasOtherThan(loc, m.name());
	}

	/**
	 * Check if there is other than material around specified location.
	 * (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m the name that we are searching in material names
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThan(Location loc, String name) {
		loc = loc.clone();
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(name))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(name))
			return true;
		return false;
	}
}
