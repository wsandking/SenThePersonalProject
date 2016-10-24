package io.kandy.protocol.xmpp.service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;

import io.kandy.protocol.xmpp.message.listener.GenericChatListener;
import io.kandy.protocol.xmpp.message.listener.GenericChatManagerListner;
import io.kandy.protocol.xmpp.message.listener.GenericDeliveryReceiptReceivedListener;

/*
 * Shouldn't have put all logic here, should make a new class that is not singleton retrieve connection from here
 * */
@Service
@Configuration
@Scope("singleton")
public class XMPPSessionManager {

	private volatile static Map<String, AbstractXMPPConnection> XMPP_SESSION_POOL;

	/*
	 * Do we need to close if chat is not ongoing for while
	 */
	private volatile static Map<String, Chat> CHAT_POOL;

	@Value("${xmpp.service}")
	private String defaultHostIp;

	@Value("${xmpp.server}")
	private String defaultService;

	@Value("${xmpp.port}")
	private int defaultPort;

	@Value("${im.server.address}")
	private String imhost;

	@Value("${im.server.port}")
	private int port;

	@Value("${im.server.path}")
	private String impath;

	@Value("${xmpp.deliver.receipt.enable}")
	private boolean defaultEnableMessageDeliverReceipt;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@PostConstruct
	public void initIn() {
		/*
		 * Initialize the connection pool, at this point, initial XMPP
		 * Connection
		 */
		XMPP_SESSION_POOL = new HashMap<String, AbstractXMPPConnection>();
		CHAT_POOL = new HashMap<String, Chat>();
		ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE,
				new DeliveryReceipt.Provider());
		ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT,
				new DeliveryReceiptRequest().getNamespace(), new DeliveryReceiptRequest.Provider());

		DeliveryReceiptManager.setDefaultAutoReceiptMode(AutoReceiptMode.always);
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		/*
		 * Make sure all the connection inside has been closed properly
		 */
		for (String connectionName : XMPP_SESSION_POOL.keySet()) {
			AbstractXMPPConnection connection = XMPP_SESSION_POOL.get(connectionName);
			connection.disconnect();
		}

	}

	public boolean login(String username, String passwd) throws Exception {
		boolean result = false;

		/*
		 * Check if user has already logged in and have a connection, create one
		 * otherwise.
		 */
		if (XMPP_SESSION_POOL.containsKey(username)) {
			result = true;
		} else {

			AbstractXMPPConnection connection = new XMPPTCPConnection(notSecureConnectionBuild(username, passwd));
			connection.connect();
			connection.login();

			/*
			 * Add a chat listener here see if there is incoming message.
			 */
			ChatManager chatManager = ChatManager.getInstanceFor(connection);
			chatManager.addChatListener(new GenericChatManagerListner());
			// chatManager.addChatListener(chatListenerManager);

			DeliveryReceiptManager.getInstanceFor(connection)
					.addReceiptReceivedListener(new GenericDeliveryReceiptReceivedListener());

			// DeliveryReceiptManager.getInstanceFor(connection).addReceiptReceivedListener(receiptListener);

			/*
			 * Make sure connection pool operation thread safe, maybe use
			 * hashtable directly could be good
			 */
			synchronized (XMPP_SESSION_POOL) {
				XMPP_SESSION_POOL.put(username, connection);
			}
			result = true;
		}
		return result;
	}

	public String SendPlainTextMessage(String username, String to, String msg) throws Exception {
		String messageId = null;

		/*
		 * Check if user has already logged in and have a connection, create one
		 * otherwise.
		 */
		if (XMPP_SESSION_POOL.containsKey(username)) {

			if (null != XMPP_SESSION_POOL.get(username) && XMPP_SESSION_POOL.get(username).isConnected()) {
				AbstractXMPPConnection connection = XMPP_SESSION_POOL.get(username);
				ChatManager chatmanager = ChatManager.getInstanceFor(connection);
				String chatKey = getChatKey(username, to);

				/*
				 * Enable message receipt
				 */

				Chat chat;
				if (CHAT_POOL.containsKey(chatKey)) {
					System.out.println("Find a reusable chat");
					chat = CHAT_POOL.get(chatKey);
				} else {
					chat = chatmanager.createChat(to, new GenericChatListener());
					// chat = chatmanager.createChat(to, chatListener);

					synchronized (CHAT_POOL) {
						CHAT_POOL.put(chatKey, chat);
					}
				}
				if (null != chat) {
					Message message = new Message();
					message.setBody(msg);
					messageId = MessageDelivery(chat, message);
				} else {
					System.out.println("Message delivery failed");
				}
			}

		} else {
			throw new Exception("Message deliver failure");
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

	public boolean logout(String username) throws Exception {
		boolean result = false;

		/*
		 * Check if user has already logged in and have a connection, create one
		 * otherwise.
		 */
		if (XMPP_SESSION_POOL.containsKey(username)) {
			result = true;
			synchronized (XMPP_SESSION_POOL) {
				if (null != XMPP_SESSION_POOL.get(username) && XMPP_SESSION_POOL.get(username).isConnected())
					XMPP_SESSION_POOL.get(username).disconnect();
				XMPP_SESSION_POOL.remove(username);
			}
		} else {
			result = true;
		}

		return result;
	}

	private XMPPTCPConnectionConfiguration notSecureConnectionBuild(String username, String password)
			throws KeyManagementException, NoSuchAlgorithmException {

		System.out.println(String.format("Host: %s : port: %d", defaultHostIp, defaultPort));

		Builder builder;
		builder = XMPPTCPConnectionConfiguration.builder();
		builder.setUsernameAndPassword(username, password).setServiceName(defaultService).setHost(defaultHostIp)
				.setPort(defaultPort).build();

		TLSUtils.disableHostnameVerificationForTlsCertificicates(builder);
		TLSUtils.acceptAllCertificates(builder);

		return builder.build();
	}
}
