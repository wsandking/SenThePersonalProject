package io.kandy.protocol.xmpp.message.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.kandy.protocol.xmpp.service.IMMessageClient;

@Configuration
@Service
public class SmackListener {

	@Autowired
	private IMMessageClient client;

	@Bean
	@Scope("prototype")
	public GenericChatListener getChatListener() {

		GenericChatListener listener = new GenericChatListener();
		listener.setClient(client);

		return listener;
	}

	@Bean
	@Scope("prototype")
	public GenericChatManagerListner getChatManagerListener() {

		if (null == client)
			System.out.println("Auto wired is failed!!!!!!!!!!!!!!!!!!");

		GenericChatManagerListner chatManager = new GenericChatManagerListner();
		chatManager.setChatlistener(this.getChatListener());

		return chatManager;
	}

	@Bean
	@Scope("prototype")
	public GenericDeliveryReceiptReceivedListener getDeliveryReceiptReceivedListener() {
		GenericDeliveryReceiptReceivedListener listener = new GenericDeliveryReceiptReceivedListener();
		listener.setClient(client);

		return listener;
	}

}
