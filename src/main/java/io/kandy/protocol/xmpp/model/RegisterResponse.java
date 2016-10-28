package io.kandy.protocol.xmpp.model;

public class RegisterResponse {
  private int statusCode;
  private String xmppId;
  private String streamId;

  public RegisterResponse() {
    super();
    // TODO Auto-generated constructor stub
  }

  public RegisterResponse(String xmppid, String streamId) {
    super();
    this.setXmppId(xmppid);
    this.streamId = streamId;
  }

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getXmppId() {
    return xmppId;
  }

  public void setXmppId(String xmppId) {
    this.xmppId = xmppId;
  }

}
