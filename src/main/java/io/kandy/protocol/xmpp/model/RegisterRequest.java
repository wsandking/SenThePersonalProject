package io.kandy.protocol.xmpp.model;

public class RegisterRequest {

	private String xmppid;
	private String password;

	public String getXmppid() {
		return xmppid;
	}

	public void setXmppid(String xmppid) {
		this.xmppid = xmppid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
