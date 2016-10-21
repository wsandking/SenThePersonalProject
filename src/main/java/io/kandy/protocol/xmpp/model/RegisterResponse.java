package io.kandy.protocol.xmpp.model;

public class RegisterResponse {

	private int statusCode;
	private boolean success;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public RegisterResponse(int statusCode, boolean success) {
		super();
		this.statusCode = statusCode;
		this.success = success;
	}

	public RegisterResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

}
