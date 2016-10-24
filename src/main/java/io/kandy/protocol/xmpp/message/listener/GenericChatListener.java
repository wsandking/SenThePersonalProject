package io.kandy.protocol.xmpp.message.listener;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.kandy.protocol.xmpp.component.IMMessageClient;
import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageReceivedResponse;

@Component
@Scope(value = "prototype")
public class GenericChatListener implements ChatMessageListener {

	@Autowired
	private IMMessageClient client;

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
			client = new IMMessageClient();
			IMMessageReceivedResponse response = client.messageReceived(msg);
			System.out.println("Message forwarded result : " + response.getMessage());
		} catch (Exception e) {
			System.out.println("Northbound message delivery failed");
			e.printStackTrace();
		}
	}

}
