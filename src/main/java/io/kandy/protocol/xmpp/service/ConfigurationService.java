package io.kandy.protocol.xmpp.service;


import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope("singleton")
public class ConfigurationService {

  public static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

  @PostConstruct
  public void initIn() {
    /*
     * Initialize the connection pool, at this point, initial XMPP Connection
     */
    /**
     * Read Environment variables, read service type label,
     */
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      logger.info(String.format("%s=%s%n", envName, env.get(envName)));

    }
  }


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

  @Value("${im.server.path}")
  private String impath;

  @Value("${im.server.domain.name}")
  private String domainName;

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

  public String getImpath() {
    return impath;
  }

  public void setImpath(String impath) {
    this.impath = impath;
  }

  public String getKubernetesMasterUrl() {
    return kubernetesMasterUrl;
  }

  public void setKubernetesMasterUrl(String kubernetesMasterUrl) {
    this.kubernetesMasterUrl = kubernetesMasterUrl;
  }

}
