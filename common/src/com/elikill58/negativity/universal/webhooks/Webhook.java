package com.elikill58.negativity.universal.webhooks;

public interface Webhook {

	/**
	 * Get the webhook name
	 * 
	 * @return webhook name
	 */
	String getWebhookName();

	/**
	 * Send the given message with adapted style to own webhook
	 * 
	 * @param msg the message to send
	 * @return true if the message is well sent
	 */
	boolean send(WebhookMessage msg);
	
	/**
	 * Send test message to webhook
	 * 
	 * @param asker Who ask for webhook ping
	 * @return true if the message is well sent
	 */
	boolean ping(String asker);
}
