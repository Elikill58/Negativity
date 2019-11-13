package com.elikill58.negativity.universal.ban;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.support.BanPluginSupport;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanRequest {

	private NegativityAccount np = null;
	private UUID uuid = null;
	private String reason, by = "Negativity";
	private boolean def, isUnban;
	private BanType banType;
	private long fullTime = 0;
	private String ac = "unknow";
	private File f = null;

	public BanRequest(NegativityAccount np, String banReason, long time, boolean def, BanType banType, String ac,
			String by, boolean isUnban) {
		this.np = np;
		this.uuid = np.getPlayerId();
		this.reason = banReason;
		this.def = def;
		this.banType = banType;
		this.fullTime = System.currentTimeMillis() + time;
		this.ac = ac;
		this.by = by;
		this.isUnban = isUnban;
		if (Ban.banType.equals(BanType.FILE)) {
			f = new File(Ban.banDir, uuid + ".txt");
			if (!f.exists())
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public BanRequest(NegativityAccount np, String line) {
		this.np = np;
		this.uuid = UUID.fromString(np.getUUID());
		String[] content = line.split(":");
		this.fullTime = Long.valueOf(content[0]);
		for (String s : content) {
			String[] part = s.split("=", 2);
			if (part.length != 2)
				continue;
			String type = part[0], value = part[1];
			switch (type) {
			case "bantype":
				banType = BanType.valueOf(value.toUpperCase());
				break;
			case "def":
				def = Boolean.valueOf(value);
				break;
			case "uuid":
				uuid = UUID.fromString(value);
				break;
			case "reason":
				reason = value;
				break;
			case "ac":
				ac = value;
				break;
			case "by":
				by = value;
				break;
			case "unban":
				isUnban = Boolean.valueOf(value);
				break;
			default:
				Adapter.getAdapter().warn("Type " + type + " unknow. Value: " + value);
				break;
			}
		}
		if (Ban.banType.equals(BanType.FILE)) {
			f = new File(Ban.banDir, uuid + ".txt");
			if (!f.exists())
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public NegativityAccount getNegativityPlayer() {
		return np;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getReason() {
		return reason;
	}

	public boolean isDef() {
		return def;
	}

	public String getBy() {
		return by;
	}

	public String getCheatName() {
		return ac;
	}

	public BanType getBanType() {
		return banType;
	}

	public long getFullTime() {
		return fullTime;
	}

	public boolean isUnban() {
		return isUnban;
	}

	public void execute() {
		boolean kickCmd = true;
		Stats.updateStats(StatsType.BAN, "");
		Adapter ada = Adapter.getAdapter();
		NegativityPlayer nPlayer = ada.getNegativityPlayer(np.getPlayerId());
		if (nPlayer != null && Perm.hasPerm(nPlayer, "notBanned"))
			return;
		if (Ban.banType.equals(BanType.FILE)) {
			try {
				f = new File(Ban.banDir, uuid + ".txt");
				if (!f.exists())
					f.createNewFile();
				Files.write(f.toPath(),
						(fullTime + ":reason=" + reason.replaceAll(":", "") + ":def=" + def + ":bantype="
								+ banType.name() + ":ac=" + ac + ":by=" + by + ":unban=false\n").getBytes(),
						StandardOpenOption.APPEND);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(Ban.banType.equals(BanType.DATABASE)) {
			try {
				String values = ada.getStringInConfig("ban.db.column.uuid") + ","
						+ ada.getStringInConfig("ban.db.column.time") + "," + ada.getStringInConfig("ban.db.column.def")
						+ "," + ada.getStringInConfig("ban.db.column.reason") + ","
						+ ada.getStringInConfig("ban.db.column.cheat_detect") + ","
						+ ada.getStringInConfig("ban.db.column.by"), parentheses = "";
				List<String> content = new ArrayList<>();
				HashMap<String, String> hash = ada.getKeysListInConfig("ban.db.column.other");
				for (String keys : hash.keySet()) {
					values += "," + keys;
					parentheses += ",?";
					content.add(getWithReplaceOlder(hash.get(keys)));
				}
				PreparedStatement stm = Database.getConnection().prepareStatement(
						"INSERT INTO " + Database.table_ban + "(" + values + ") VALUES (?,?,?,?,?,?" + parentheses + ")");
				stm.setString(1, uuid.toString());
				stm.setInt(2, (int) (fullTime));
				stm.setBoolean(3, def);
				stm.setString(4, reason);
				stm.setString(5, ac);
				stm.setString(6, by);
				int i = 7;
				for (String cc : content) {
					String s = getWithReplaceOlder(cc);
					if (UniversalUtils.isInteger(s))
						stm.setInt(i++, Integer.parseInt(s));
					else
						stm.setString(i++, s);
				}
				stm.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(Ban.banType.equals(BanType.COMMAND)) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getWithReplaceOlder(Adapter.getAdapter().getStringInConfig("ban.command_ban")));
			kickCmd = false;
		} else if(Ban.banType.equals(BanType.PLUGIN)) {
			for (BanPluginSupport bp : Ban.BAN_SUPPORT) {
				if (np.getBanRequest().size() >= ada.getIntegerInConfig("ban.def.ban_time"))
					bp.banDef(np.getNegativityPlayer(), "Cheat (" + reason + ")", "Negativity");
				else
					bp.ban(np.getNegativityPlayer(), "Cheat (" + reason + ")", "Negativity", fullTime);
			}
		}

		if (nPlayer != null) {
			nPlayer.banEffect();
			if(kickCmd)
				nPlayer.kickPlayer(reason, new Timestamp(fullTime).toString().split("\\.", 2)[0], by, def);
		}
	}

	public void unban() {
		if(this.isUnban)
			return;
		try {
			this.isUnban = true;
			Adapter ada = Adapter.getAdapter();
			np.removeBanRequest(this);
			if(Ban.banType.equals(BanType.PLUGIN)) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getWithReplaceOlder(Adapter.getAdapter().getStringInConfig("ban.command_unban")));
			} else if (ada.getBooleanInConfig("ban.destroy_when_unban")) {
				if (Ban.banType.equals(BanType.FILE)) {
					Files.write(f.toPath(), "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
					f.delete();
					f.deleteOnExit();
				} else {
					PreparedStatement stm = Database.getConnection()
							.prepareStatement("DELETE FROM " + Database.table_ban + " WHERE uuid = ?");
					stm.setString(1, uuid.toString());
					stm.execute();
				}
			} else {
				if (Ban.banType.equals(BanType.FILE)) {
					List<String> lines = Files.readAllLines(f.toPath()), futurLines = new ArrayList<>();
					for (String l : lines) {
						if(l.contains("unban=false"))
							futurLines.add(l.replaceAll("unban=false", "unban=true")); // unbanning
						else if(!l.contains("unban=true"))
							futurLines.add(l + ":unban=true"); // unbanning with older version
						else futurLines.add(l); // already unban
					}
					BufferedWriter bw = new BufferedWriter(new PrintWriter(f.getAbsolutePath()));
					for (String l : futurLines) {
						bw.write(l);
						bw.newLine();
					}
					bw.close();
				} else {
					String uc = ada.getStringInConfig("ban.db.column.uuid");
					PreparedStatement stm = Database.getConnection().prepareStatement("UPDATE " + Database.table_ban
							+ " SET " + ada.getStringInConfig("ban.db.column.time") + " = ? WHERE " + uc + " = ?");
					stm.setInt(1, 0);
					stm.setString(2, uuid.toString());
					stm.execute();
					PreparedStatement stm2 = Database.getConnection().prepareStatement("UPDATE " + Database.table_ban
							+ " SET " + ada.getStringInConfig("ban.db.column.def") + " = ? WHERE " + uc + " = ?");
					stm2.setBoolean(1, false);
					stm2.setString(2, uuid.toString());
					stm2.execute();
				}
			}
		} catch (NoSuchFileException e) {
			// already deleted
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getWithReplaceOlder(String s) {
		String life = "?";
		String name = "???";
		String level = "?";
		String gamemode = "?";
		String walkSpeed = "?";
		NegativityPlayer nPlayer = Adapter.getAdapter().getNegativityPlayer(np.getPlayerId());
		if (nPlayer != null) {
			life = String.valueOf(nPlayer.getLife());
			name = nPlayer.getName();
			level = String.valueOf(nPlayer.getLevel());
			gamemode = nPlayer.getGameMode();
			walkSpeed = String.valueOf(nPlayer.getWalkSpeed());
		}

		return s.replaceAll("%uuid%", uuid.toString()).replaceAll("%name%", name).replaceAll("%reason%", reason)
				.replaceAll("%life%", life).replaceAll("%name%", name).replaceAll("%level%", level)
				.replaceAll("%gm%", gamemode).replaceAll("%walk_speed%", walkSpeed);
	}

	public static enum BanType {
		FILE, DATABASE, COMMAND, PLUGIN, MOD, CONSOLE, UNKNOW;
	}
}
