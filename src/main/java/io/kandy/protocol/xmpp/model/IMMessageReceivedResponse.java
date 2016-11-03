package io.kandy.protocol.xmpp.model;

public class IMMessageReceivedResponse {
  private String message;
  private int statusCode;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

}
