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

import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.UniversalUtils;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class BanRequest {

	private NegativityPlayer np = null;
	private UUID uuid = null;
	private String reason, by = "Negativity";
	private boolean def;
	private BanType banType;
	private long fullTime = 0;
	private String ac = "unknow";
	private File f = null;

	public BanRequest(NegativityPlayer np, String banReason, long time, boolean def, BanType banType, String ac) {
		this.np = np;
		this.uuid = UUID.fromString(np.getUUID());
		this.reason = banReason;
		this.def = def;
		this.banType = banType;
		this.fullTime = time;
		this.ac = ac;
		if (Ban.banFileActive) {
			f = new File(Ban.banDir, uuid + ".txt");
			if (!f.exists())
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public BanRequest(NegativityPlayer np, String banReason, long time, boolean def, BanType banType, String ac,
			String by) {
		this.np = np;
		this.uuid = UUID.fromString(np.getUUID());
		this.reason = banReason;
		this.def = def;
		this.banType = banType;
		this.fullTime = System.currentTimeMillis();
		this.ac = ac;
		this.by = by;
		if (Ban.banFileActive) {
			f = new File(Ban.banDir, uuid + ".txt");
			if (!f.exists())
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public BanRequest(NegativityPlayer np, String line) {
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
			default:
				Adapter.getAdapter().warn("Type " + type + " unknow. Value: " + value);
				break;
			}
		}
		if (Ban.banFileActive) {
			f = new File(Ban.banDir, uuid + ".txt");
			if (!f.exists())
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public NegativityPlayer getNegativityPlayer() {
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

	public void execute() {
		Adapter ada = Adapter.getAdapter();
		if (Perm.hasPerm(np, "notBanned"))
			return;
		if (Ban.banFileActive) {
			try {
				f = new File(Ban.banDir, uuid + ".txt");
				if (!f.exists())
					f.createNewFile();
				Files.write(f.toPath(),
						(fullTime + ":reason=" + reason.replaceAll(":", "") + ":def=" + def
								+ ":bantype=" + banType.name() + ":ac=" + ac + "\n").getBytes(),
						StandardOpenOption.APPEND);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (Ban.banDbActive) {
			try {
				String values = ada.getStringInConfig("ban.db.column.uuid") + ","
						+ ada.getStringInConfig("ban.db.column.time") + "," + ada.getStringInConfig("ban.db.column.def")
						+ "," + ada.getStringInConfig("ban.db.column.reason"), parentheses = "";
				List<String> content = new ArrayList<>();
				HashMap<String, String> hash = ada.getKeysListInConfig("ban.db.column.other");
				for (String keys : hash.keySet()) {
					values += "," + keys;
					parentheses += ",?";
					content.add(hash.get(keys));
				}
				PreparedStatement stm = Database.getConnection()
						.prepareStatement("INSERT INTO " + Database.table_ban + "(" + values + ") VALUES (?,?,?,?" + parentheses +  ")");
				stm.setString(1, uuid.toString());
				stm.setInt(2, (int) (fullTime));
				stm.setBoolean(3, def);
				stm.setString(4, reason);
				int i = 5;
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
		}
		np.banEffect();
		np.kickPlayer(reason, new Timestamp(fullTime).toString().split("\\.", 2)[0], by, def);
	}
	
	public void unban() {
		try {
			Adapter ada = Adapter.getAdapter();
			np.removeBanRequest(this);
			if(ada.getBooleanInConfig("ban.destroy_when_unban")) {
				if(Ban.banFileActive) {
					Files.write(f.toPath(), "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
					f.delete();
					f.deleteOnExit();
				}
				if(Ban.banDbActive) {
					PreparedStatement stm = Database.getConnection().prepareStatement("DELETE FROM " + Database.table_ban + " WHERE uuid = ?");
					stm.setString(1, uuid.toString());
					stm.execute();
				}
			} else {
				if(Ban.banFileActive) {
					List<String> lines = Files.readAllLines(f.toPath()), futurLines = new ArrayList<>();
					for(String l : lines)
						futurLines.add(l.replaceAll("def=true", "def=false").replaceAll(l.split(":")[0], "0"));
					BufferedWriter bw = new BufferedWriter(new PrintWriter(f.getAbsolutePath()));
					for(String l : futurLines) {
						bw.write(l);
						bw.newLine();
					}
					bw.close();
				}
				if(Ban.banDbActive) {
					String uc = ada.getStringInConfig("ban.db.column.uuid");
					PreparedStatement stm = Database.getConnection().prepareStatement("UPDATE " + Database.table_ban + " SET " + ada.getStringInConfig("ban.db.column.time") + " = ? WHERE " + uc + " = ?");
					stm.setInt(1, 0);
					stm.setString(2, uuid.toString());
					stm.execute();
					PreparedStatement stm2 = Database.getConnection().prepareStatement("UPDATE " + Database.table_ban + " SET " + ada.getStringInConfig("ban.db.column.def") + " = ? WHERE " + uc + " = ?");
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
		return s.replaceAll("%uuid%", uuid.toString()).replaceAll("%name%", "").replaceAll("%reason%", reason)
				.replaceAll("%life%", String.valueOf(np.getLife())).replaceAll("%name%", np.getName())
				.replaceAll("%level%", String.valueOf(np.getLevel())).replaceAll("%gm%", np.getGameMode())
				.replaceAll("%walk_speed%", String.valueOf(np.getWalkSpeed()));
	}

	public static enum BanType {
		PLUGIN, MOD, UNKNOW;
	}
}
