package com.elikill58.negativity.universal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;

public abstract class NegativityPlayer {

	private NegativityAccount na;
	private boolean gettedBan = false;
	private List<BanRequest> banRequest = new ArrayList<>();

	public boolean hasGettedBan() {
		return gettedBan;
	}

	public void setGettedBan(boolean b) {
		this.gettedBan = b;
	}

	public List<BanRequest> getBanRequest() {
		if (!hasGettedBan())
			loadBanRequest();
		List<BanRequest> br = new ArrayList<>();
		br.addAll(banRequest);
		return br;
	}

	public void loadBanRequest() {
		if (gettedBan)
			return;
		gettedBan = true;
		loadNegativityAccount();
		if (Ban.banFileActive) {
			File banFile = new File(Ban.banDir.getAbsolutePath(), getUUID() + ".txt");
			if (!banFile.exists())
				return;
			try {
				for (String line : Files.readAllLines(banFile.toPath(), UniversalUtils.getOs().getCharset()))
					addBanRequest(new BanRequest(this, line));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (Ban.banDbActive) {
			try {
				Adapter ada = Adapter.getAdapter();
				PreparedStatement stm = Database.getConnection()
						.prepareStatement("SELECT * FROM " + Database.table_ban + " WHERE uuid = ?");
				stm.setString(1, this.getUUID());
				ResultSet rs = stm.executeQuery();
				while (rs.next()) {
					addBanRequest(new BanRequest(this, rs.getString(ada.getStringInConfig("ban.db.column.reason")),
							rs.getInt(ada.getStringInConfig("ban.db.column.time")),
							rs.getBoolean(ada.getStringInConfig("ban.db.column.def")), BanType.UNKNOW, "unknow"));
				}
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loadNegativityAccount() {
		this.na = new NegativityAccount(this);
	}
	
	public NegativityAccount getNegativityAccount() {
		return na;
	}
	
	public void addBanRequest(BanRequest br) {
		List<BanRequest> brList = new ArrayList<>();
		brList.addAll(banRequest);
		for (BanRequest actualBr : brList)
			if (br.getBy().equals(actualBr.getBy()) && br.getReason().equals(actualBr.getReason())
					&& br.getFullTime() == actualBr.getFullTime())
				return;
		banRequest.add(br);
	}

	public String getBanReason() {
		if (banRequest.size() == 0)
			return "not banned";
		if (banRequest.size() == 1)
			return banRequest.get(0).getReason();
		String reason = "";
		List<String> cheatAlready = new ArrayList<>();
		for (BanRequest br : banRequest)
			if (!cheatAlready.contains(br.getCheatName()) && !br.getCheatName().equalsIgnoreCase("unknow")) {
				reason += (reason.equalsIgnoreCase("") ? "Cheat (" : ", ") + br.getCheatName();
				cheatAlready.add(br.getCheatName());
			}
		return reason + ")";
	}

	public String getBanTime() {
		if (banRequest.size() == 0)
			return "not banned";
		long l = 0;
		for (BanRequest br : banRequest) {
			if (br.isDef())
				return "always";
			else if ((br.getFullTime()) > l)
				l = br.getFullTime();
		}
		Timestamp time = new Timestamp(l);
		return time.toString().split("\\.", 2)[0];
	}

	public String getBanBy() {
		String by = "";
		List<String> byAlready = new ArrayList<>();
		for (BanRequest br : banRequest)
			if (!byAlready.contains(br.getBy())) {
				by += (by.equalsIgnoreCase("") ? "" : ", ") + br.getBy();
				byAlready.add(br.getBy());
			}
		return by;
	}

	public boolean isBanDef() {
		if (banRequest.size() == 0)
			return false;
		for (BanRequest br : banRequest)
			if (br.isDef())
				return true;
		return false;
	}
	
	public void removeBanRequest(BanRequest br) {
		banRequest.remove(br);
	}

	public abstract String getUUID();
	public abstract Object getPlayer();
	public abstract boolean hasDefaultPermission(String s);
	public abstract int getWarn(AbstractCheat c);
	public abstract double getLife();
	public abstract String getName();
	public abstract String getGameMode();
	public abstract float getWalkSpeed();
	public abstract int getLevel();
	public abstract void kickPlayer(String reason, String time, String by, boolean def);
	public abstract void banEffect();
	public abstract void startAnalyze(AbstractCheat c);
	public abstract void startAllAnalyze();
	
}
