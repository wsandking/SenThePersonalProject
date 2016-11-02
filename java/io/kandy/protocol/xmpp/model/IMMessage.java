package io.kandy.protocol.xmpp.model;

public class IMMessage {

  private IMRequest imRequest;

  public IMMessage() {
    super();
    // TODO Auto-generated constructor stub
  }

  public IMMessage(String receiver, String message) {
    super();
    this.imRequest = new IMRequest();
    this.imRequest.setCharset("UTF-8");
    this.imRequest.setMessage(message);
    this.imRequest.setToUrl(receiver);
  }

  public IMRequest getImRequest() {
    return imRequest;
  }

  public void setImRequest(IMRequest imRequest) {
    this.imRequest = imRequest;
  }

}
