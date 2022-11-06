package com.elikill58.negativity.universal.pluginMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

public class NegativityMessagesManager {

	public static final Map<Byte, Supplier<NegativityMessage>> MESSAGES_BY_ID;

	public static final String CHANNEL_ID = "negativity:msg";
	public static final int PROTOCOL_VERSION = 4;

	static {
		Map<Byte, Supplier<NegativityMessage>> messages = new HashMap<>();
		messages.put(AlertMessage.MESSAGE_ID, AlertMessage::new);
		messages.put(ProxyPingMessage.MESSAGE_ID, ProxyPingMessage::new);
		messages.put(ReportMessage.MESSAGE_ID, ReportMessage::new);
		messages.put(ClientModsListMessage.MESSAGE_ID, ClientModsListMessage::new);
		messages.put(ProxyExecuteBanMessage.MESSAGE_ID, ProxyExecuteBanMessage::new);
		messages.put(ProxyRevokeBanMessage.MESSAGE_ID, ProxyRevokeBanMessage::new);
		messages.put(AccountUpdateMessage.MESSAGE_ID, AccountUpdateMessage::new);
		messages.put(RedisNegativityMessage.MESSAGE_ID, RedisNegativityMessage::new);
		messages.put(PlayerVersionMessage.MESSAGE_ID, PlayerVersionMessage::new);
		messages.put(ShowAlertStatusMessage.MESSAGE_ID, ShowAlertStatusMessage::new);
		messages.put(ProxyExecuteWarnMessage.MESSAGE_ID, ProxyExecuteWarnMessage::new);
		messages.put(ProxyRevokeWarnMessage.MESSAGE_ID, ProxyRevokeWarnMessage::new);
		MESSAGES_BY_ID = Collections.unmodifiableMap(messages);
	}

	/**
	 * Tries to read a message from the given input.
	 * <p>
	 * The message will be identified by the first byte of the input.
	 * If unknown, {@code null} will be returned.
	 *
	 * @param input the stream containing the message
	 * @return the message if it was valid, null otherwise
	 * @throws IOException if an error of some kind occurred whilst reading the message
	 */
	@Nullable
	public static NegativityMessage readMessage(DataInputStream input) throws IOException {
		byte messageId = input.readByte();
		Supplier<NegativityMessage> messageSupplier = MESSAGES_BY_ID.get(messageId);
		if (messageSupplier == null) {
			return null;
		}

		NegativityMessage message = messageSupplier.get();
		message.readFrom(input);
		return message;
	}

	/**
	 * Tries to read a message from the given raw data.
	 * <p>
	 * The message will be identified by the first byte of the input.
	 * If unknown, {@code null} will be returned.
	 *
	 * @param data the raw data of the message
	 * @return the message if it was valid, null otherwise
	 * @throws IOException if an error of some kind occurred whilst reading the message
	 */
	@Nullable
	public static NegativityMessage readMessage(byte[] data) throws IOException {
		try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(data))) {
			return readMessage(input);
		}
	}

	/**
	 * Writes the give message into a raw {@code byte[]}.
	 *
	 * @param message the message to write
	 * @return the raw data of the written message
	 * @throws IOException if an error of some kind occurred whilst writing the message
	 */
	public static byte[] writeMessage(NegativityMessage message) throws IOException {
		try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(); DataOutputStream dataOutput = new DataOutputStream(byteOutput)) {
			dataOutput.writeByte(message.messageId());
			message.writeTo(dataOutput);
			return byteOutput.toByteArray();
		}
	}
}
