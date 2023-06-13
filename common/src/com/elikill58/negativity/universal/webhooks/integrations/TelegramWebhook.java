package com.elikill58.negativity.universal.webhooks.integrations;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.api.json.parser.ParseException;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.webhooks.Webhook;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage.WebhookMessageType;

public class TelegramWebhook extends Webhook {

	private final String token, chatId;

	public TelegramWebhook(Configuration config) {
		super("Telegram", config);
		this.token = config.getString("token");
		String chat = config.getString("chat_id");
		this.chatId = (!chat.startsWith("-") && !chat.startsWith("+") ? "-" : "") + chat;
	}

	@Override
	public void addToQueue(WebhookMessage msg) {
		if (msg == null || !enabled || !msg.canBeSend(config.getSection("messages." + msg.getMessageType().name().toLowerCase(Locale.ROOT))))
			return;
		queue.add(msg); // maybe another that can be combined will come
	}

	@Override
	public boolean ping(String asker) {
		try {
			sendMessageOn("This is a ping test by " + asker);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void sendMessageOn(String text) throws Exception {
		UniversalUtils.getContentFromURLWithException("https://api.telegram.org/bot" + token + "/sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "UTF-8"), "")
				.ifPresent(result -> {
					try {
						JSONObject resp = (JSONObject) new JSONParser().parse(result);
						if (resp.get("ok").toString() != "true") {
							Adapter.getAdapter().getLogger().warn("Failed to send webhook to telegram: " + result + ". Please report this.");
						}
					} catch (ParseException e) {
						Adapter.getAdapter().getLogger().printError("Failed to parse JSON result for '" + result + "'", e);
					}
				});
	}

	@Override
	protected void sendAsync(WebhookMessageType msgType, Player p, List<WebhookMessage> msg) {
		Configuration confMsg = config.getSection("messages." + msgType.name().toLowerCase(Locale.ROOT));
		if (confMsg == null) // not config
			return;
		List<String> header = confMsg.getStringList("header");
		List<String> perPlayer = confMsg.getStringList("per-player");
		List<String> footer = confMsg.getStringList("footer");

		StringJoiner text = new StringJoiner("\n");
		for (String line : perPlayer) {
			if (line.contains("%cheat%")) {// should be duplicated
				for (WebhookMessage eachMsg : msg) {
					text.add(eachMsg.applyPlaceHolders(line));
				}
			} else
				text.add(msg.get(0).applyPlaceHolders(line));
		}
		try {
			sendMessageOn(String.join("\n", header) + text.toString() + String.join("\n", footer));
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().printError("Failed to send telegram content", e);
		}
	}
}
