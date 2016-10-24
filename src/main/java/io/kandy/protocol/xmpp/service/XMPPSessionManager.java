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
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import io.kandy.protocol.xmpp.message.listener.SmackListener;

/*
 * Shouldn't have put all logic here, should make a new class that is not singleton retrieve connection from here
 * */
@Service
@Scope("singleton")
public class XMPPSessionManager {

	private volatile Map<String, AbstractXMPPConnection> xmppSessionPool;

	/*
	 * Do we need to close if chat is not ongoing for while
	 */
	private volatile Map<String, Chat> chatPool;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private SmackListener listener;

	@PostConstruct
	public void initIn() {
		/*
		 * Initialize the connection pool, at this point, initial XMPP
		 * Connection
		 */
		xmppSessionPool = new HashMap<String, AbstractXMPPConnection>();
		chatPool = new HashMap<String, Chat>();
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
		for (String connectionName : xmppSessionPool.keySet()) {
			AbstractXMPPConnection connection = xmppSessionPool.get(connectionName);
			connection.disconnect();
		}

	}

	public boolean login(String username, String passwd) throws Exception {
		boolean result = false;

		/*
		 * Check if user has already logged in and have a connection, create one
		 * otherwise.
		 */
		if (xmppSessionPool.containsKey(username)) {
			result = true;
		} else {

			AbstractXMPPConnection connection = new XMPPTCPConnection(notSecureConnectionBuild(username, passwd));
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
			 * Make sure connection pool operation thread safe, maybe use
			 * hashtable directly could be good
			 */
			synchronized (xmppSessionPool) {
				xmppSessionPool.put(username, connection);
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
		if (xmppSessionPool.containsKey(username)) {

			if (null != xmppSessionPool.get(username) && xmppSessionPool.get(username).isConnected()) {
				AbstractXMPPConnection connection = xmppSessionPool.get(username);
				ChatManager chatmanager = ChatManager.getInstanceFor(connection);
				String chatKey = getChatKey(username, to);

				/*
				 * Enable message receipt
				 */

				Chat chat;
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
		if (xmppSessionPool.containsKey(username)) {
			result = true;
			synchronized (xmppSessionPool) {
				if (null != xmppSessionPool.get(username) && xmppSessionPool.get(username).isConnected())
					xmppSessionPool.get(username).disconnect();
				xmppSessionPool.remove(username);
			}
		} else {
			result = true;
		}

		return result;
	}

	private XMPPTCPConnectionConfiguration notSecureConnectionBuild(String username, String password)
			throws KeyManagementException, NoSuchAlgorithmException {

		// System.out.println(String.format("Host: %s : port: %d",
		// defaultHostIp, defaultPort));
		System.out.println(String.format("Host: %s : port: %d", configurationService.getDefaultHostIp(),
				configurationService.getPort()));

		Builder builder;
		builder = XMPPTCPConnectionConfiguration.builder();
		builder.setUsernameAndPassword(username, password).setServiceName(configurationService.getDefaultService())
				.setHost(configurationService.getDefaultHostIp()).setPort(configurationService.getDefaultPort())
				.build();

		TLSUtils.disableHostnameVerificationForTlsCertificicates(builder);
		TLSUtils.acceptAllCertificates(builder);

		return builder.build();
	}

}
