package io.kandy.protocol.xmpp.message.listener;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

public class GenericChatListener implements ChatMessageListener {
	
	@Override
	public void processMessage(Chat chat, Message message) {
		// TODO Auto-generated method stub
		System.out.println("Message type: " + message.getType());
		System.out.println("Message received: " + message);

	}

}
