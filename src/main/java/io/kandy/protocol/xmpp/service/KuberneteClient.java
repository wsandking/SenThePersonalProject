package io.kandy.protocol.xmpp.service;

 
import java.util.Map;

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
import io.kandy.protocol.xmpp.model.ServicesType;

@Service
@Scope("singleton")
public class KuberneteClient {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private KubernetesClient client;
  private Map<String, String> imRoutingLabels;


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
    loadLabels();
    logger.info("Kubernetes Client Initialization finished");

  }

  private void loadLabels() {
    
  }

  public String discoverServiceURL(ServicesType service) throws Exception {
    String destionationUrl = null;

    switch (service) {
      case imRoutingServices:
        logger.info("Try to discover im");
        client.services().withLabels(this.imRoutingLabels);
        break;
      default:
        throw new Exception("Unknown services type: " + service);
    }
    return destionationUrl;
  }

}
