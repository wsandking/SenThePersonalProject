package io.kandy.protocol.xmpp.message.listener;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.kandy.protocol.xmpp.component.IMMessageClient;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;

@Component
@Scope(value = "prototype")
public class GenericDeliveryReceiptReceivedListener implements ReceiptReceivedListener {

	@Autowired
	private IMMessageClient client;

	@Override
	public void onReceiptReceived(String fromJid, String toJid, String deliveryReceiptId, Stanza stanza) {
		// TODO Auto-generated method stub
		System.out.println("onReceiptReceived: from: " + fromJid + " to: " + toJid + " deliveryReceiptId: "
				+ deliveryReceiptId + " stanza: " + stanza);
		client = new IMMessageClient();
		IMMessageReceipt receipt = new IMMessageReceipt();

		receipt.setMessageId(deliveryReceiptId);
		receipt.setStatusCode(0);

		client.messageDelievered(receipt);
	}

}
