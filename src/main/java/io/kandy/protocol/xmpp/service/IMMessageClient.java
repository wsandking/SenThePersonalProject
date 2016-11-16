package io.kandy.protocol.xmpp.service;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jivesoftware.smack.packet.Message;
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

import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageDeliveredAck;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;

@Service
@Scope("singleton")
public class IMMessageClient {


  @PostConstruct
  public void initIn() {
    /*
     * Initialize the connection pool, at this point, initial XMPP Connection
     */
    /**
     * Read Environment variables, read service type label,
     */
    String ip = null;
    int port = 0;
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      logger.info(String.format("%s=%s%n", envName, env.get(envName)));

      if ("IM_ROUTING_MANAGER_SERVICE_HOST".equals(envName)) {
        ip = env.get(envName);
      }
      if ("IM_ROUTING_MANAGER_SERVICE_PORT".equals(envName)) {
        port = Integer.parseInt(env.get(envName));
      }

    }
    imRoutingServicesUrl = ip + ":" + port;
    logger.info("imRoutingServicesUrl : " + imRoutingServicesUrl);
  }


  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private String imRoutingServicesUrl = null;


  @Autowired
  private ConfigurationService configurationService;

  public HttpStatus messageSendToIM(Message message) throws Exception {

    /**
     * Check if im routing host is visible
     */
    if (null == imRoutingServicesUrl) {
      /**
       * Try to let kuberneteClient to discover
       */
      logger.error("Cannot find address to connect to IM");
      throw new Exception("Cannot find address to connect to IM");
    }

    RestTemplate client = new RestTemplate();
    String receiver = this.makeUsername(message.getTo());
    IMMessage im = new IMMessage(receiver, message.getBody());

    HttpStatus ack = HttpStatus.NO_CONTENT;
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML));
    HttpEntity<IMMessage> entity = new HttpEntity<IMMessage>(im, headers);

    /*
     * senwangtest1@127.0.0.1/Spark
     */

    String sender = this.makeUsername(message.getFrom());

    /*
     * Comment it out for now
     */
    // String url = String.format("http://%s:%d/%s/%s",
    // configurationService.getImhost(),
    // configurationService.getPort(), configurationService.getImpath(),
    // sender + "/app/xmpp/im/receive");

    String url = String.format("http://%s%s/%s", this.imRoutingServicesUrl,
        configurationService.getImpath(), sender + "/app/xmpp/im/send");
    logger.info("Message forwarded to service url: " + url);
    System.out.println("**************Message forwarded to service url: " + url);
    /*
     * Should have a try and catch mechanism
     */
    try {
      ResponseEntity<?> response = client.exchange(url, HttpMethod.POST, entity, String.class);
      ack = response.getStatusCode();
    } catch (HttpServerErrorException e) {
      logger.error("Message deliver failed");
      ack = HttpStatus.CONFLICT;
    }
    return ack;
  }

  /*
   * For future handling
   */
  // public IMMessageReceivedResponse messageSendToIM(Message message) {
  // RestTemplate client = new RestTemplate();
  //
  // String receiver = this.makeUsername(message.getTo());
  //
  // IMMessage im = new IMMessage(receiver, message.getBody());
  //
  // IMMessageReceivedResponse ack = null;
  // HttpHeaders headers = new HttpHeaders();
  // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON,
  // MediaType.APPLICATION_ATOM_XML));
  // HttpEntity<IMMessage> entity = new HttpEntity<IMMessage>(im, headers);
  //
  // /*
  // * senwangtest1@127.0.0.1/Spark
  // */
  //
  // String sender = this.makeUsername(message.getFrom());
  //
  // /*
  // * Comment it out for now
  // */
  // // String url = String.format("http://%s:%d/%s/%s",
  // // configurationService.getImhost(),
  // // configurationService.getPort(), configurationService.getImpath(),
  // // sender + "/app/xmpp/im/receive");
  //
  // String url = String.format("http://%s:%d/%s/%s",
  // configurationService.getImhost(),
  // configurationService.getPort(), configurationService.getImpath(), sender
  // + "/app/xmpp/im/send");
  //
  // System.out.println("Message forwarded to service url: " + url);
  // ResponseEntity<IMMessageReceivedResponse> response =
  // client.postForEntity(url, entity,
  // IMMessageReceivedResponse.class);
  //
  // ack = response.getBody();
  // return ack;
  // }

  public IMMessageDeliveredAck messageDelievered(IMMessageReceipt message) {
    RestTemplate client = new RestTemplate();

    IMMessageDeliveredAck ack = null;
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML));
    HttpEntity<IMMessageReceipt> entity = new HttpEntity<IMMessageReceipt>(message, headers);

    /*
     * Waiting for message delivered API finished
     */
    String url = String.format("http://%s%s%s", "127.0.0.1:8080", configurationService.getImpath(),
        "/app/xmpp/im/deliver");
    logger.info(String.format("http://%s%s%s", "127.0.0.1:8080", configurationService.getImpath(),
        "/app/xmpp/im/deliver"));
    System.out.println("Message forwarded to service url: " + url);
    ResponseEntity<IMMessageDeliveredAck> response =
        client.postForEntity(url, entity, IMMessageDeliveredAck.class);

    ack = response.getBody();
    return ack;

  }

  private String makeUsername(String username) {

    if (username.contains("/")) {
      String[] parts = username.split("/");
      username = parts[0];

    }

    if (username.contains("@")) {
      String[] anotherparts = username.split("@");
      username = anotherparts[0] + "@" + configurationService.getDomainName();
    }

    return username;
  }
}
