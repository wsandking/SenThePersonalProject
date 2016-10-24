package io.kandy.protocol.xmpp.message.listener;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import io.kandy.protocol.xmpp.model.IMMessageReceipt;
import io.kandy.protocol.xmpp.service.IMMessageClient;

public class GenericDeliveryReceiptReceivedListener implements ReceiptReceivedListener {

	private IMMessageClient client;

	public IMMessageClient getClient() {
		return client;
	}

	public void setClient(IMMessageClient client) {
		this.client = client;
	}

	@Override
	public void onReceiptReceived(String fromJid, String toJid, String deliveryReceiptId, Stanza stanza) {
		// TODO Auto-generated method stub
		System.out.println("onReceiptReceived: from: " + fromJid + " to: " + toJid + " deliveryReceiptId: "
				+ deliveryReceiptId + " stanza: " + stanza);
		// client = new IMMessageClient();
		IMMessageReceipt receipt = new IMMessageReceipt();

		receipt.setMessageId(deliveryReceiptId);
		receipt.setStatusCode(0);

		client.messageDelievered(receipt);
	}

}
