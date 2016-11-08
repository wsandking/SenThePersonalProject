package io.kandy.protocol.xmpp.service;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.fabric8.kubernetes.api.model.EndpointAddress;
import io.fabric8.kubernetes.api.model.EndpointSubset;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kandy.protocol.xmpp.model.IMMessage;

@Service
@Scope("singleton")
public class KuberneteClient {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private KubernetesClient client;
  private String localIp;


  @Autowired
  private ConfigurationService configurationService;

  @PostConstruct
  public void initIn() {

    /*
     * Initialize the the connection to kuberMaster
     */
    Config config =
        new ConfigBuilder().withMasterUrl(configurationService.getKubernetesMasterUrl()).build();
    client = new DefaultKubernetesClient(config);
    localIp = this.instanceInfoStamp().get(1);
  }

  public String brocastPlainMessage(String username, String to, String msg) {
    /*
     * Make message first
     */
    String messageId = null;
    logger.info("Start brocast message:  " + msg);
    IMMessage im = new IMMessage(to, msg);

    for (String url : this.getMessageBrocastURLs(username)) {
      logger.info("Querying url: " + url);
      messageId = this.forwardPlainTextRequest(url, im);
      if (null != messageId)
        break;
    }
    logger.info("Message brocast finished!");
    return messageId;
  }

  private List<String> getMessageBrocastURLs(String username) {
    /*
     * Get a properties for this later, maybe
     */
    String applicationPath = configurationService.getXmppContextUrl();
    String messageBrocast = configurationService.getXmppBrocastMessageUrl();

    return this.makeURLs(applicationPath, username, messageBrocast);

  }

  private List<String> makeURLs(String applicationPath, String username, String brocastPath) {
    List<String> urls = new ArrayList<String>();
    int servicePort = Integer.parseInt(configurationService.getXmppServicePort());
    logger.info("Start making URLs");
    for (Endpoints endpoint : client.endpoints()
        .withLabel("name", configurationService.getXmppServiceLabel()).list().getItems()) {
      logger.info("Start making URLs");
      for (EndpointSubset subset : endpoint.getSubsets()) {
        for (EndpointAddress address : subset.getAddresses()) {
          if (!address.getIp().equals(this.localIp))
            urls.add(String.format("http://%s:%d%s%s%s", address.getIp(), servicePort,
                applicationPath, username, brocastPath));
          else
            logger.info("Self IP Address");
        }
      }
    }
    return urls;
  }

  public String forwardPlainTextRequest(String url, IMMessage im) {
    String messageId = null;

    RestTemplate client = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML));
    HttpEntity<IMMessage> entity = new HttpEntity<IMMessage>(im, headers);

    try {
      ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, entity, String.class);
      if (HttpStatus.NOT_FOUND != response.getStatusCode())
        messageId = response.getBody();
    } catch (HttpServerErrorException e) {
      logger.warn(String.format("System error in %s ", url));
    } catch (Exception e) {
      logger.warn(String.format("User not found in %s ", url));
    }

    return messageId;
  }

  /**
   * A hack, should be removed soon
   * 
   * @return
   */
  private List<String> instanceInfoStamp() {

    ArrayList<String> ips = new ArrayList<String>();
    Enumeration<NetworkInterface> e;
    try {
      e = NetworkInterface.getNetworkInterfaces();
      while (e.hasMoreElements()) {
        NetworkInterface n = (NetworkInterface) e.nextElement();
        Enumeration<InetAddress> ee = n.getInetAddresses();
        while (ee.hasMoreElements()) {
          InetAddress i = (InetAddress) ee.nextElement();
          ips.add(i.getHostAddress());
        }
      }

    } catch (SocketException e1) {
      // TODO Auto-generated catch block
      logger.info("Cannot read IP address");
      e1.printStackTrace();
    }
    return ips;
  }
}
