package io.kandy.protocol.xmpp.model;

public class IMMessage {
	private String charset;
	private String toUrl;
	private String Message;

	public IMMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IMMessage(String toUrl, String message) {
		super();
		this.toUrl = toUrl;
		this.Message = message;
		this.charset = "UTF-8";
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getToUrl() {
		return toUrl;
	}

	public void setToUrl(String toUrl) {
		this.toUrl = toUrl;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

}
