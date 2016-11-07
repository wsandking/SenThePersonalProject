package io.kandy.protocol.xmpp.service;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.EndpointAddress;
import io.fabric8.kubernetes.api.model.EndpointSubset;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;

@Service
@Scope("singleton")
public class KuberneteClient {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private KubernetesClient client;


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

  }

  public IMMessageReceipt brocastPlainMessage(String username, String to, String msg) {

    /*
     * Make message first
     */



    for (String url : this.getPodsURLs()) {

    }

    return null;
  }

  private List<String> getPodsURLs() {
    /*
     * Use label
     */
    List<String> urls = new ArrayList<String>();
    int servicePort = Integer.parseInt(configurationService.getXmppServicePort());

    for (Endpoints endpoint : client.endpoints()
        .withLabel("name", configurationService.getXmppServiceLabel()).list().getItems()) {

      for (EndpointSubset subset : endpoint.getSubsets()) {

        for (EndpointAddress address : subset.getAddresses()) {
          urls.add(String.format("http://%s:%d", address.getIp(), servicePort));
        }
      }
    }
    return urls;
  }

  public IMMessageReceipt forwardPlainTextRequest(String url, IMMessage msg) {

    return null;
  }
}
