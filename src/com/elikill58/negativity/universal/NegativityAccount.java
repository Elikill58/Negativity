package com.elikill58.negativity.universal;

public class NegativityAccount {
	
	private NegativityPlayer np;
	private String lang = "";
	
	public NegativityAccount(NegativityPlayer np) {
		this.np = np;
		
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public String getLang() {
		return lang;
	}
	
	public void setLang(String s) {
		this.lang = s;
	}
}
