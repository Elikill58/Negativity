package com.elikill58.negativity.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.negativity.IPlayerCheatAlertEvent;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityAccountManager;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NegativityPlayer {

	private static final Map<UUID, NegativityPlayer> players = new HashMap<>();
	public static ArrayList<UUID> INJECTED = new ArrayList<>();

	private final UUID playerId;
	private final Player p;
	
	public ArrayList<Cheat> ACTIVE_CHEAT = new ArrayList<>();
	public ArrayList<String> proof = new ArrayList<>();
	public HashMap<PacketType, Integer> PACKETS = new HashMap<>();
	public HashMap<Cheat, List<IPlayerCheatAlertEvent>> ALERT_NOT_SHOWED = new HashMap<>();

	public int ACTUAL_CLICK = 0, LAST_CLICK = 0, SEC_ACTIVE = 0;
	
	// setBack
	public int NO_FALL_DAMAGE = 0;
	
	public long TIME_INVINCIBILITY = 0;
	
	public boolean isInFight = false, already_blink = false, disableShowingAlert = false, isFreeze = false;
	private boolean mustToBeSaved = false;

	public NegativityPlayer(Player p) {
		this.p = p;
		this.playerId = p.getUniqueId();
		Adapter ada = Adapter.getAdapter();
		getAccount().setPlayerName(p.getName());
		ada.getAccountManager().save(playerId);
		ACTIVE_CHEAT.clear();
		//boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive()) {
				startAnalyze(c);
				//if (c.needPacket())
					//needPacket = true;
			}
		//if (needPacket && !SpigotNegativityPlayer.INJECTED.contains(p.getUniqueId()))
		//	SpigotNegativityPlayer.INJECTED.add(p.getUniqueId());
	}

	public NegativityAccount getAccount() {
		return NegativityAccount.get(playerId);
	}
	
	public UUID getUUID() {
		return playerId;
	}
	
	public String getName() {
		return getPlayer().getName();
	}
	
	public boolean hasDetectionActive(Cheat c) {
		if(!c.isActive())
			return false;
		if(!ACTIVE_CHEAT.contains(c))
			return false;
		return getPlayer().getPing() < c.getMaxAlertPing();
	}

	public int getWarn(Cheat c) {
		return getAccount().getWarn(c);
	}

	public int getAllWarn(Cheat c) {
		return getAccount().getWarn(c);
	}

	public Player getPlayer() {
		return p;
	}
	
	public void kickPlayer(String reason, String time, String by, boolean def) {
		getPlayer().kick(Messages.getMessage(getPlayer(), "ban.kick_" + (def ? "def" : "time"), "%reason%",
					reason, "%time%", String.valueOf(time), "%by%", by));
	}

	public void addWarn(Cheat c, int reliability) {
		addWarn(c, reliability, 1);
	}

	public void addWarn(Cheat c, int reliability, int amount) {
		if (System.currentTimeMillis() < TIME_INVINCIBILITY || c.getReliabilityAlert() > reliability)
			return;
		NegativityAccount account = getAccount();
		account.setWarnCount(c, account.getWarn(c) + amount);
		mustToBeSaved = true;
	}

	public void setWarn(Cheat c, int cheats) {
		NegativityAccount account = getAccount();
		account.setWarnCount(c, cheats);
		Adapter.getAdapter().getAccountManager().save(account.getPlayerId());
	}
	
	public void startAnalyze(Cheat c) {
		ACTIVE_CHEAT.add(c);
		if (c.needPacket() && !INJECTED.contains(getPlayer().getUniqueId()))
			INJECTED.add(getPlayer().getUniqueId());
		if (c.getKey().equalsIgnoreCase(CheatKeys.FORCEFIELD)) {
			/*if (timeStartFakePlayer == 0)
				timeStartFakePlayer = 1; // not on the player connection
			else
				makeAppearEntities();*/
		}
	}

	public void startAllAnalyze() {
		INJECTED.add(getPlayer().getUniqueId());
		for (Cheat c : Cheat.values())
			startAnalyze(c);
	}

	public void stopAnalyze(Cheat c) {
		ACTIVE_CHEAT.remove(c);
	}
	
	public String getReason(Cheat c) {
		String n = "";
		for(Cheat all : Cheat.values())
			if(getAllWarn(all) > 5 && all.isActive())
				n = n + (n.equals("") ? "" : ", ") + all.getName();
		if(!n.contains(c.getName()))
			n = n + (n.equals("") ? "" : ", ") + c.getName();
		return n;
	}

	public void logProof(String msg) {
		proof.add(msg);
	}
	
	public void saveProof() {
		Adapter.getAdapter().getLogger().info("saving ...");
		if(mustToBeSaved) {
			mustToBeSaved = false;
			Adapter.getAdapter().getAccountManager().save(getUUID());
		}
		if (proof.isEmpty()) {
			Adapter.getAdapter().getLogger().info("nothing to save");
			return;
		}
		try {
			File temp = new File(Adapter.getAdapter().getDataFolder().getAbsolutePath() + File.separator
					+ "user" + File.separator + "proof" + File.separator + getUUID() + ".txt");
			if (!temp.exists())
				temp.createNewFile();
			String msg = "";
			for (String s : proof)
				msg += s + "\n";
			Files.write(temp.toPath(), msg.getBytes(), StandardOpenOption.APPEND);
			proof.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Adapter.getAdapter().getLogger().info("DONE ...");
	}
	
	public List<IPlayerCheatAlertEvent> getAlertForAllCheat(){
		final List<IPlayerCheatAlertEvent> list = new ArrayList<>();
		ALERT_NOT_SHOWED.forEach((c, listAlerts) -> {
			if(!listAlerts.isEmpty())
				list.add(getAlertForCheat(c, listAlerts));
		});
		return list;
	}
	
	public IPlayerCheatAlertEvent getAlertForCheat(Cheat c, List<IPlayerCheatAlertEvent> list) {
		int nb = 0, nbConsole = 0;
		HashMap<Integer, Integer> relia = new HashMap<>();
		HashMap<Integer, Integer> ping = new HashMap<>();
		ReportType type = ReportType.NONE;
		boolean hasRelia = false;
		CheatHover hoverProof = null;
		for(IPlayerCheatAlertEvent e : list) {
			nb += e.getNbAlert();
			
			relia.put(e.getReliability(), relia.getOrDefault(e.getReliability(), 0) + 1);

			ping.put(e.getPing(), ping.getOrDefault(e.getPing(), 0) + 1);

			if(type == ReportType.NONE || (type == ReportType.WARNING && e.getReportType() == ReportType.VIOLATION))
				type = e.getReportType();

			hasRelia = e.hasManyReliability() ? true : hasRelia;
			
			if(hoverProof == null && e.getHover() != null)
				hoverProof = e.getHover();
			
			nbConsole += e.getNbAlertConsole();
			e.clearNbAlertConsole();
		}
		// Don't to 100% each times that there is more than 2 alerts, we made a summary, and a the nb of alert to upgrade it
		int newRelia = UniversalUtils.parseInPorcent(UniversalUtils.sum(relia) + nb);
		int newPing = UniversalUtils.sum(ping);
		// we can ignore "proof" and "stats_send" because they have been already saved and they are NOT showed to player
		return list.get(0).update(type, newRelia, hasRelia, newPing, "", hoverProof, nb, nbConsole);
	}

	private void destroy() {
		saveProof();
		NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
		accountManager.save(playerId);
		accountManager.dispose(playerId);
	}
	
	public static NegativityPlayer getNegativityPlayer(Player p) {
		return players.computeIfAbsent(p.getUniqueId(), id -> new NegativityPlayer(p));
	}

	public static NegativityPlayer getCached(UUID playerId) {
		return players.get(playerId);
	}
	
	public static Map<UUID, NegativityPlayer> getAllPlayers(){
		return players;
	}

	public static void removeFromCache(UUID playerId) {
		NegativityPlayer cached = players.remove(playerId);
		if (cached != null) {
			cached.destroy();
		}
	}

	public void fight() {
		// TODO Auto-generated method stub
		
	}

	public void unfight() {
		// TODO Auto-generated method stub
		
	}
}
