package io.kandy.protocol.xmpp.model;

public class RegisterResponse {
	private String xmppid;
	private String streamId;

	public RegisterResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RegisterResponse(String xmppid, String streamId) {
		super();
		this.xmppid = xmppid;
		this.streamId = streamId;
	}

	public String getXmppid() {
		return xmppid;
	}

	public void setXmppid(String xmppid) {
		this.xmppid = xmppid;
	}

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

}
