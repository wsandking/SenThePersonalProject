package io.kandy.protocol.xmpp.model;

public class RegisterRequest {

  private String xmppId;
  private String password;

  public String getXmppId() {
    return xmppId;
  }

  public void setXmppId(String xmppId) {
    this.xmppId = xmppId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
