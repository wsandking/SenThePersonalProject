package io.kandy.protocol.xmpp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ConfigurationService {
  @Value("${xmpp.service}")
  private String defaultHostIp;

  @Value("${xmpp.server}")
  private String defaultService;

  @Value("${xmpp.domain}")
  private String xmppDomain;

  @Value("${xmpp.port}")
  private int defaultPort;

  @Value("${xmpp.deliver.receipt.enable}")
  private boolean defaultEnableMessageDeliverReceipt;

  @Value("${im.server.address}")
  private String imhost;

  @Value("${im.server.port}")
  private int port;

  @Value("${im.server.path}")
  private String impath;

  @Value("${im.server.domain.name}")
  private String domainName;

  @Value("${xmpp.services.label}")
  private String xmppServiceLabel;

  @Value("${xmpp.container.services.port}")
  private String xmppServicePort;

  @Value("${kubernetes.master.url}")
  private String kubernetesMasterUrl;

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  public String getDefaultHostIp() {
    return defaultHostIp;
  }

  public void setDefaultHostIp(String defaultHostIp) {
    this.defaultHostIp = defaultHostIp;
  }

  public String getDefaultService() {
    return defaultService;
  }

  public void setDefaultService(String defaultService) {
    this.defaultService = defaultService;
  }

  public int getDefaultPort() {
    return defaultPort;
  }

  public void setDefaultPort(int defaultPort) {
    this.defaultPort = defaultPort;
  }

  public boolean isDefaultEnableMessageDeliverReceipt() {
    return defaultEnableMessageDeliverReceipt;
  }

  public void setDefaultEnableMessageDeliverReceipt(boolean defaultEnableMessageDeliverReceipt) {
    this.defaultEnableMessageDeliverReceipt = defaultEnableMessageDeliverReceipt;
  }

  public String getXmppDomain() {
    return xmppDomain;
  }

  public void setXmppDomain(String xmppDomain) {
    this.xmppDomain = xmppDomain;
  }

  public String getImhost() {
    return imhost;
  }

  public void setImhost(String imhost) {
    this.imhost = imhost;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getImpath() {
    return impath;
  }

  public void setImpath(String impath) {
    this.impath = impath;
  }

  public String getXmppServiceLabel() {
    return xmppServiceLabel;
  }

  public void setXmppServiceName(String xmppServiceName) {
    this.xmppServiceLabel = xmppServiceName;
  }

  public String getXmppServicePort() {
    return xmppServicePort;
  }

  public void setXmppServicePort(String xmppServicePort) {
    this.xmppServicePort = xmppServicePort;
  }

  public String getKubernetesMasterUrl() {
    return kubernetesMasterUrl;
  }

  public void setKubernetesMasterUrl(String kubernetesMasterUrl) {
    this.kubernetesMasterUrl = kubernetesMasterUrl;
  }



}
