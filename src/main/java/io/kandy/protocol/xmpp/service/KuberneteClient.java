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

  public String discoverServiceURL(String labelKey, String labelValue) {
    String hostIp = null;


    return hostIp;
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
