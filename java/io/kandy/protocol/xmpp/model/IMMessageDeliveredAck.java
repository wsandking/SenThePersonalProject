package io.kandy.protocol.xmpp.model;

public class IMMessageDeliveredAck {

  private String messageId;
  private int statusCode;

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

}
