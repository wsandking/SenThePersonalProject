package io.kandy.protocol.xmpp.message.listener;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageReceivedResponse;
import io.kandy.protocol.xmpp.service.IMMessageClient;

public class GenericChatListener implements ChatMessageListener {

	private IMMessageClient client;

	public IMMessageClient getClient() {
		return client;
	}

	public void setClient(IMMessageClient client) {
		this.client = client;
	}

	/*
	 * Should invoke a RestTemplate Client and make a call to north bound.
	 */
	@Override
	public void processMessage(Chat chat, Message message) {
		// TODO Auto-generated method stub
		System.out.println("Message type: " + message.getType());
		System.out.println("Message received: " + message);

		IMMessage msg = new IMMessage(message.getFrom(), message.getTo(), message.getBody());
		try {
			// client = new IMMessageClient();
			IMMessageReceivedResponse response = client.messageReceived(msg);
			System.out.println("Message forwarded result : " + response.getMessage());
		} catch (Exception e) {
			System.out.println("Northbound message delivery failed");
			e.printStackTrace();
		}
	}

}
