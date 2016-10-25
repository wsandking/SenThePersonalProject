package io.kandy.protocol.xmpp.model;

public class IMMessage {
	private String charset;
	private String toUrl;
	private String message;

	public IMMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IMMessage(String charset, String toUrl, String message) {
		super();
		this.charset = charset;
		this.toUrl = toUrl;
		this.message = message;
	}

	public IMMessage(String toUrl, String message) {
		super();
		this.toUrl = toUrl;
		this.message = message;
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
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
