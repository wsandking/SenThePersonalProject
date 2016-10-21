package io.kandy.protocol.xmpp.model;

public class IMMessage {
	private String from;
	private String to;
	private String plainMessage;

	public IMMessage(String from, String to, String plainMessage) {
		super();
		this.from = from;
		this.to = to;
		this.plainMessage = plainMessage;
	}

	public IMMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getPlainMessage() {
		return plainMessage;
	}

	public void setPlainMessage(String plainMessage) {
		this.plainMessage = plainMessage;
	}

}
