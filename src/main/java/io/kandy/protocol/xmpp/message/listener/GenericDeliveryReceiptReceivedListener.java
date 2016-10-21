package io.kandy.protocol.xmpp.message.listener;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

public class GenericDeliveryReceiptReceivedListener implements ReceiptReceivedListener {

	@Override
	public void onReceiptReceived(String arg0, String arg1, String arg2, Stanza arg3) {
		// TODO Auto-generated method stub
		System.out.println("Message Receipt-------------------\nMessage delivered successfully. ");
	}

}
