package com.elikill58.negativity.sponge.utils;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.protocols.*;
import com.elikill58.negativity.universal.AbstractCheat;
import com.elikill58.negativity.universal.adapter.Adapter;

import ninja.leaping.configurate.ConfigurationNode;

public enum Cheat implements AbstractCheat {

	FORCEFIELD(true, true, ItemTypes.DIAMOND_SWORD, ForceFieldProtocol.class, aliases_forcefield),
	FASTPLACE(true, false, ItemTypes.DIRT, FastPlaceProtocol.class, aliases_fastplace),
	SPEEDHACK(true, false, ItemTypes.BEACON, SpeedHackProtocol.class, aliases_speedhack),
	AUTOCLICK(true, false, ItemTypes.FISHING_ROD, AutoClickProtocol.class, aliases_autoclick),
	FLY(true, true, ItemTypes.FIREWORKS, FlyProtocol.class, aliases_fly),
	ANTIPOTION(true, true, ItemTypes.POTION, AntiPotionProtocol.class, aliases_antipotion),
	AUTOEAT(true, true, ItemTypes.COOKED_BEEF, AutoEatProtocol.class, aliases_autoeat),
	AUTOREGEN(true, true, ItemTypes.GOLDEN_APPLE, AutoRegenProtocol.class, aliases_autoregen),
	ANTIKNOCKBACK(true, false, ItemTypes.STICK, AntiKnockbackProtocol.class, aliases_antiknockback),
	JESUS(true, false, ItemTypes.WATER_BUCKET, JesusProtocol.class, aliases_jesus),
	NOFALL(true, false, ItemTypes.WOOL, NoFallProtocol.class, aliases_nofall),
	BLINK( true, true, ItemTypes.COAL_BLOCK, BlinkProtocol.class, aliases_blink),
	SPIDER(true, false, ItemTypes.WEB, SpiderProtocol.class, aliases_spider),
	SNEAK(true, true, ItemTypes.BLAZE_POWDER, SneakProtocol.class, aliases_sneak),
	FASTBOW(true, true, ItemTypes.BOW, FastBowProtocol.class, aliases_fastbow),
	SCAFFOLD(true, false, ItemTypes.GRASS, ScaffoldProtocol.class, aliases_scaffold),
	STEP(true, false, ItemTypes.BRICK_STAIRS, StepProtocol.class, aliases_step),
	NOSLOWDOWN(true, false, ItemTypes.SOUL_SAND, NoSlowDownProtocol.class, aliases_noslowdown),
	FASTLADDERS(true, false, ItemTypes.LADDER, FastLadderProtocol.class, aliases_fastladders),
	PHASE(true, false, ItemTypes.STAINED_GLASS, PhaseProtocol.class, aliases_phase),
	AUTOSTEAL(true, false, ItemTypes.CHEST, AutoStealProtocol.class, aliases_autosteal),
	EDITED_CLIENT(true, false, ItemTypes.FEATHER, EditedClientProtocol.class, aliases_edited_client),
	ALL(false, true, ItemTypes.AIR, null, aliases_all);

	private boolean needPacket;
	private String name;
	private ItemType m;
	private Class<?> protocolClass;
	private String[] aliases;

	Cheat(boolean hasName, boolean needPacket, ItemType m, Class<?> protocolClass, String... aliases) {
		this.needPacket = needPacket;
		this.m = m;
		this.protocolClass = protocolClass;
		ConfigurationNode config = SpongeNegativity.getConfig();
		String exact_name = Adapter.getAdapter().getStringInConfig("cheats." + this.name().toLowerCase() + ".exact_name");
		if (exact_name == null) {
			config.getNode("cheats").getNode(this.name().toLowerCase()).getNode("exact_name")
					.setValue(this.name().toLowerCase());
			config.getNode("cheats").getNode(this.name().toLowerCase()).getNode("isActive").setValue(true);
			config.getNode("cheats").getNode(this.name().toLowerCase()).getNode("autoVerif").setValue(false);
		}
		if (!hasName)
			this.name = this.name();
		else
			this.name = config.getNode("cheats").getNode(this.name().toLowerCase()).getNode("exact_name").getString();
		String[] tempAlias = new String[aliases.length + 1];
		int i = 0;
		for(String s : aliases)
			tempAlias[i++] = s;
		tempAlias[i] = this.name().toLowerCase();
		this.aliases = tempAlias;
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + this.name().toLowerCase() + ".isActive");
	}

	public boolean needPacket() {
		return needPacket;
	}

	public ItemType getMaterial() {
		return m;
	}

	public Class<?> getProtocolClass() {
		return protocolClass;
	}

	public void run() {
		try {
			System.out.println("Run a faire.");
			// ((BukkitRunnable)
			// protocolClass.newInstance()).runTaskTimer(Negativity.getInstance(), 20, 20);
		} catch (Exception e) {
		}
	}

	public boolean isAutoVerif() {
		if (this.equals(ALL))
			return false;
		else
			return Adapter.getAdapter().getBooleanInConfig("cheats." + this.name().toLowerCase() + ".autoVerif");
	}

	public int getReliabilityAlert() {
		return Adapter.getAdapter().getIntegerInConfig("cheats." + this.name().toLowerCase() + ".reliability_alert");
	}

	public boolean isSetBack() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + this.name().toLowerCase() + ".setBack");
	}

	public int getAlertToKick() {
		return Adapter.getAdapter().getIntegerInConfig("cheats." + this.name().toLowerCase() + ".alert_to_kick");
	}

	public boolean allowKick() {
		return Adapter.getAdapter().getBooleanInConfig("cheats." + this.name().toLowerCase() + ".kick");
	}

	public boolean setAllowKick(boolean b) {
		for (Player p : Utils.getOnlinePlayers())
			SpongeNegativity.manageAutoVerif(p);
		Adapter.getAdapter().set("cheats." + this.name().toLowerCase() + ".kick", b);
		return b;
	}

	public boolean setBack(boolean b) {
		Adapter.getAdapter().set("cheats." + this.name().toLowerCase() + ".setBack", b);
		return b;
	}

	public boolean setAutoVerif(boolean b) {
		Adapter.getAdapter().set("cheats." + this.name().toLowerCase() + ".autoVerif", b);
		return b;
	}

	public boolean setActive(boolean active) {
		Adapter.getAdapter().set("cheats." + this.name().toLowerCase() + ".isActive", active);
		return active;
	}

	@Override
	public int getMaxAlertPing() {
		return Adapter.getAdapter().getIntegerInConfig("cheats" + this.name().toLowerCase() + "ping");
	}
	
	public static Optional<Cheat> getCheatFromString(String name) {
		for (Cheat c : Cheat.values()) {
			try {
				if (c.name().equalsIgnoreCase(name.toLowerCase()) || c.getName().equalsIgnoreCase(name))
					return Optional.of(c);
			} catch (NullPointerException e) {
				System.out.println("NPE: " + e.getMessage() + " FOR Cheat: " + c);
				System.out.println("getName: " + c.getName() + " name: " + c.name());
			}
		}
		return Optional.empty();
	}

	@Override
	public String[] getAliases() {
		return aliases;
	}
}