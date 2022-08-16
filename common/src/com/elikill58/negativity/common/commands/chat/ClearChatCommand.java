package com.elikill58.negativity.common.commands.chat;

import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.permissions.Perm;

public class ClearChatCommand implements CommandListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if(!Perm.hasPerm(sender, Perm.CHAT_CLEAR)) {
			Messages.sendMessage(sender, "not_permission");
			return false;
		}
		for(int i = 0; i < 100; i++)
			Adapter.getAdapter().broadcastMessage("  ");
		Messages.broadcastMessage("negativity.chat.cleared_broadcast", "%name%", sender.getName());
		Messages.sendMessage(sender, "negativity.chat.cleared", "%amount%", Adapter.getAdapter().getOnlinePlayers().size());
		return true;
	}

}
