package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ReportMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 2;

	private String reported;
	private String reason;
	private String reporter;

	public ReportMessage() {
		this("", "", "");
	}

	public ReportMessage(String reported, String reason, String reporter) {
		this.reported = reported;
		this.reason = reason;
		this.reporter = reporter;
	}

	@Override
	public byte messageId() {
		return 2;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		reported = input.readUTF();
		reason = input.readUTF();
		reporter = input.readUTF();
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeUTF(reported);
		output.writeUTF(reason);
		output.writeUTF(reporter);
	}

	public String getReported() {
		return reported;
	}

	public String getReason() {
		return reason;
	}

	public String getReporter() {
		return reporter;
	}
}
