package io.kandy.protocol.xmpp.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import io.kandy.protocol.xmpp.message.listener.SmackListener;


/*
 * Shouldn't have put all logic here, should make a new class that is not singleton retrieve
 * connection from here
 */
@Service
@Scope("singleton")
public class XMPPSessionManager {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private volatile Map<String, AbstractXMPPConnection> xmppSessionPool;

  /*
   * Do we need to close if chat is not ongoing for while
   */
  private volatile Map<String, Chat> chatPool;

  @Autowired
  private ConfigurationService configurationService;

  /**
   * Probably be use in the future to discover
   * 
   * @Autowired private KuberneteClient kuberneteClient;
   */

  @Autowired
  private SmackListener listener;

  @PostConstruct
  public void initIn() {
    /*
     * Initialize the connection pool, at this point, initial XMPP Connection
     */
    xmppSessionPool = new HashMap<String, AbstractXMPPConnection>();
    chatPool = new HashMap<String, Chat>();
    ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE,
        new DeliveryReceipt.Provider());
    ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT,
        new DeliveryReceiptRequest().getNamespace(), new DeliveryReceiptRequest.Provider());

    DeliveryReceiptManager.setDefaultAutoReceiptMode(AutoReceiptMode.ifIsSubscribed);
    logger.info("XMPPSessionManager initialization finished.");
  }

  @PreDestroy
  public void cleanUp() throws Exception {

    for (String chatKey : chatPool.keySet()) {
      Chat chat = chatPool.get(chatKey);
      chat.close();
    }
    chatPool.clear();

    /*
     * Make sure all the connection inside has been closed properly
     */
    for (String connectionName : xmppSessionPool.keySet()) {
      AbstractXMPPConnection connection = xmppSessionPool.get(connectionName);
      connection.disconnect();
      xmppSessionPool.remove(connection);
    }
    xmppSessionPool.clear();

  }

  /*
   * Print the instance information
   */
  public List<String> instanceInfoStamp() {

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

  public String login(String username, String passwd) throws Exception {
    String streamId = null;

    username = this.retriveUsername(username);
    /*
     * Check if user has already logged in and have a connection, create one otherwise.
     */
    if (xmppSessionPool.containsKey(username)) {
      streamId = xmppSessionPool.get(username).getStreamId();
    } else {

      AbstractXMPPConnection connection =
          new XMPPTCPConnection(notSecureConnectionBuild(username, passwd));
      connection.connect();
      connection.login();

      /*
       * Add a chat listener here see if there is incoming message.
       */
      ChatManager chatManager = ChatManager.getInstanceFor(connection);
      chatManager.addChatListener(listener.getChatManagerListener());

      // chatManager.addChatListener(new GenericChatManagerListner());
      // chatManager.addChatListener(beanFactory.getBean(GenericChatManagerListner.class));
      // chatManager.addChatListener(config.getChatManagerListener());

      DeliveryReceiptManager.getInstanceFor(connection)
          .addReceiptReceivedListener(listener.getDeliveryReceiptReceivedListener());

      /*
       * Make sure connection pool operation thread safe, maybe use hashtable directly could be good
       */
      synchronized (xmppSessionPool) {
        xmppSessionPool.put(username, connection);
      }
      streamId = connection.getStreamId();
    }
    return streamId;
  }

  public String miroserviceSendPlainTextMessage(String username, String to, String msg)
      throws Exception {
    String messageId = null;
    username = this.retriveUsername(username);
    to = this.makeToUrl(to);
    /*
     * Check if user has already logged in and have a connection, create one otherwise.
     */
    if (xmppSessionPool.containsKey(username) && null != xmppSessionPool.get(username)
        && xmppSessionPool.get(username).isConnected()) {
      messageId = this.SendPlainTextMessage(username, to, msg);
    } else {
      /*
       * Brocast see if anyone on my subnet has it
       */
      // logger.info("Cannot find user try other system");
      // messageId = kuberneteClient.brocastPlainMessage(username, to, msg);
      // if (null == messageId)
      throw new Exception("Message deliver failure");
    }
    return messageId;
  }

  public boolean logout(String username) throws Exception {
    boolean result = false;
    username = this.retriveUsername(username);

    /*
     * Check if user has already logged in and have a connection, create one otherwise.
     */
    if (xmppSessionPool.containsKey(username)) {
      result = true;
      synchronized (xmppSessionPool) {
        if (null != xmppSessionPool.get(username) && xmppSessionPool.get(username).isConnected())
          xmppSessionPool.get(username).disconnect();
        xmppSessionPool.remove(username);
      }

      /*
       * Clean the chat, for the future, should use hazelcast to handle this thing
       */

      synchronized (chatPool) {
        /*
         * Just add this to solve concurrent modification on hashmap
         */
        Set<String> chatKeyToRemove = new HashSet<String>();

        for (String chatKey : chatPool.keySet()) {
          if (chatKey.contains(username)) {
            Chat chat = chatPool.get(chatKey);
            chat.close();
            chatKeyToRemove.add(chatKey);
          }
        }

        for (String key : chatKeyToRemove) {
          chatPool.remove(key);
        }

      }

    } else {
      result = true;
    }

    return result;
  }

  public String brocastMessage(String username, String to, String msg) throws Exception {
    String messageId = null;
    /*
     * It has already be processed
     */
    // username = this.retriveUsername(username);

    if (xmppSessionPool.containsKey(username) && null != xmppSessionPool.get(username)
        && xmppSessionPool.get(username).isConnected()) {
      // to = this.makeToUrl(to);
      messageId = this.SendPlainTextMessage(username, to, msg);
    } else {
      /*
       * Do not need to proceed just return empty
       */
    }

    return messageId;
  }


  private String SendPlainTextMessage(String username, String to, String msg) throws Exception {
    String messageId = null;
    AbstractXMPPConnection connection = xmppSessionPool.get(username);
    ChatManager chatmanager = ChatManager.getInstanceFor(connection);
    String chatKey = getChatKey(username, to);
    /*
     * Enable message receipt
     */
    Chat chat = null;
    try {

      if (chatPool.containsKey(chatKey)) {
        System.out.println("Find a reusable chat");
        chat = chatPool.get(chatKey);
      } else {
        chat = chatmanager.createChat(to, listener.getChatListener());
        // chat = chatmanager.createChat(to, chatListener);
        synchronized (chatPool) {
          chatPool.put(chatKey, chat);
        }
      }
      if (null != chat) {
        Message message = new Message();
        message.setBody(msg);
        message.setFrom(username);
        message.setTo(to);
        messageId = MessageDelivery(chat, message);
      } else {
        System.out.println("Message delivery failed");
      }
    } catch (NotConnectedException ne) {
      if (null != chat && chatPool.containsKey(chatKey)) {
        chat.close();
        chatPool.remove(chatKey);
        /*
         * Check session pool for two users to fix.
         */
        if (xmppSessionPool.containsKey(username)) {
          /*
           * Create a new chat and call myself again
           */
          miroserviceSendPlainTextMessage(username, to, msg);
        }
      }

      logger.error(String.format("Message deliver failure because of delivery failed:\n %s",
          ne.getMessage()));
    }
    return messageId;
  }

  private String MessageDelivery(Chat chat, Message msg) throws NotConnectedException {
    String deliveryReceiptId = DeliveryReceiptRequest.addTo(msg);
    chat.sendMessage(msg);

    System.out.println("sendMessage: deliveryReceiptId for this message is: " + deliveryReceiptId);
    return deliveryReceiptId;
  }

  /*
   * Make something more complicated in the future, like UUID Map
   */
  private String getChatKey(String username, String to) {
    System.out.println("Chat Key: " + (username + to));
    return username + to;
  }

  private XMPPTCPConnectionConfiguration notSecureConnectionBuild(String username, String password)
      throws KeyManagementException, NoSuchAlgorithmException {

    // System.out.println(String.format("Host: %s : port: %d",
    // defaultHostIp, defaultPort));
    logger.info(String.format("Host: %s : port: %d", configurationService.getDefaultHostIp(),
        configurationService.getPort()));
    System.out.println(String.format("Host: %s : port: %d", configurationService.getDefaultHostIp(),
        configurationService.getPort()));

    Builder builder;
    builder = XMPPTCPConnectionConfiguration.builder();
    builder.setUsernameAndPassword(username, password)
        .setServiceName(configurationService.getDefaultService())
        .setHost(configurationService.getDefaultHostIp())
        .setPort(configurationService.getDefaultPort()).build();

    TLSUtils.disableHostnameVerificationForTlsCertificicates(builder);
    TLSUtils.acceptAllCertificates(builder);

    return builder.build();
  }

  private String retriveUsername(String xmppId) {

    if (xmppId.contains("@")) {
      String[] parts = xmppId.split("@");
      xmppId = parts[0];

    }
    return xmppId;

  }



  private String makeToUrl(String to) {

    if (to.contains("@")) {
      String[] anotherparts = to.split("@");
      to = anotherparts[0] + "@" + configurationService.getXmppDomain();
    }

    System.out.println("ToURL: " + to);
    return to;
  }

}
