package io.kandy.protocol.xmpp.message.listener;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.springframework.http.HttpStatus;

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

    try {
      // client = new IMMessageClient();
      if (null != message.getBody()) {
        HttpStatus response = client.messageSendToIM(message);
        System.out.println("Message forwarded result : " + response.toString());

        /*
         * Message receipt information
         */
        if (DeliveryReceiptManager.hasDeliveryReceiptRequest(message)) {
          Message receipt = DeliveryReceiptManager.receiptMessageFor(message);
          if (null != receipt) {
            System.out.println("Message receipt payload: " + receipt);
            chat.sendMessage(receipt);
          } else {
            System.out.println("Message generation failure: ");
          }
        } else {
          System.out.println("Message do not require actions.");
        }
      } else {
        /*
         * Todo:
         * 
         * Create websocket or other lightweight message components
         * 
         */
        System.out.println("Not a message, a presense message");

      }
    } catch (Exception e) {
      System.out.println("Northbound message delivery failed");
      e.printStackTrace();
    }
  }

}
