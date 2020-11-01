package com.elikill58.negativity.api.events.player;

import java.net.InetAddress;
import java.util.UUID;

import com.elikill58.negativity.api.events.Event;

public class LoginEvent implements Event {

	private final UUID uuid;
	private final String name;
	private final InetAddress address;
	private Result result;
	private String kickMessage;
	
	public LoginEvent(UUID uuid, String name, Result result, InetAddress address, String kickMessage) {
		this.uuid = uuid;
		this.name = name;
		this.result = result;
		this.address = address;
		this.kickMessage = kickMessage;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public Result getLoginResult() {
		return result;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public void setLoginResult(Result result) {
		this.result = result;
	}
	
	public String getKickMessage() {
		return kickMessage;
	}
	
	public void setKickMessage(String kickMessage) {
		this.kickMessage = kickMessage;
	}
	
	public static enum Result {
		  ALLOWED, KICK_FULL, KICK_BANNED, KICK_WHITELIST, KICK_OTHER;
	}
}
