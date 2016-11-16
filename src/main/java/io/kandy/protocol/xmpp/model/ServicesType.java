package io.kandy.protocol.xmpp.model;

public enum ServicesType {
  imRoutingServices("IM_ROUTING_SERVICE");

  private final String description;

  ServicesType(String description) {
    this.description = description;
  }

  public String toString() {
    return description;
  }
}
