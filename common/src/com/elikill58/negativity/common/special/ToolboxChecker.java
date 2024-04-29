package com.elikill58.negativity.common.special;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.BedrockClientData;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerManager;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.SpecialKeys;
import com.elikill58.negativity.universal.logger.Debug;

public class ToolboxChecker extends Special implements Listeners {

	public ToolboxChecker() {
		super(SpecialKeys.TOOLBOX_CHECKER, Materials.CHEST);
	}

	@EventListener
	public void onConnect(PlayerConnectEvent e) {
		Player p = e.getPlayer();
		BedrockClientData data = BedrockPlayerManager.getBedrockClientData(p.getUniqueId());
		if (data == null)
			return; // not bedrock -> can't use toolbox
		if (!data.getDeviceOs().isPhone()) {
			Adapter.getAdapter().debug(Debug.CHECK, "Not phone");
			return;
		}
		if (data.getDeviceModel() == null) {
			Adapter.getAdapter().debug(Debug.CHECK, "Device model unknown");
			return;
		}
		String modelName = data.getDeviceModel();
		Pattern pattern = Pattern.compile("^([^\\s]+)");
		Matcher matcher = pattern.matcher(modelName);
		matcher.find();
		modelName = modelName.substring(matcher.start(), matcher.end());
		if (modelName.toLowerCase().equals(modelName) || isForbiddenName(modelName)) {
			if(getConfig().getBoolean("kick", false)) {
				p.kick(Messages.getMessage(NegativityAccount.get(p.getUniqueId()), "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
			} else
				Adapter.getAdapter().getLogger().info("Player " + p.getName() + " just joined with toolbox.");
		} else
			Adapter.getAdapter().debug(Debug.CHECK, "Not invalid name: " + modelName);
	}

	private boolean isForbiddenName(String modelName) {
		return getConfig().getStringList("forbidden-name").stream().filter(modelName::equals).count() > 0;
	}
}
