package com.elikill58.negativity.common.events.negativity;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.ReportType;

public interface IPlayerCheatAlertEvent extends Event {
	
	public boolean isCancelled();

	public void setCancelled(boolean cancel);
	
	public ReportType getReportType();
	
	public Player getPlayer();
	
	public Cheat getCheat();
	
	public int getReliability();
	
	public boolean hasManyReliability() ;
	
	public void setAlert(boolean b);
	
	public boolean isAlert();
	
	public int getPing();
	
	public String getProof();
	
	public CheatHover getHover();
	
	public int getNbAlert();
	
	public String getAlertMessageKey();
	
	public int getNbAlertConsole();
	
	public void clearNbAlertConsole();
	
	public IPlayerCheatAlertEvent update(ReportType type, int reliability, boolean hasRelia, int ping, String proof, CheatHover hover, int nbAlert, int nbAlertConsole);
}
