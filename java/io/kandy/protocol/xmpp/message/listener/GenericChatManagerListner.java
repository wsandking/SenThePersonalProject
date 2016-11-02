package io.kandy.protocol.xmpp.message.listener;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;

public class GenericChatManagerListner implements ChatManagerListener {

  private GenericChatListener chatlistener;

  public GenericChatListener getChatlistener() {
    return chatlistener;
  }

  public void setChatlistener(GenericChatListener chatlistener) {
    this.chatlistener = chatlistener;
  }

  @Override
  public void chatCreated(Chat chat, boolean createdLocally) {
    // TODO Auto-generated method stub
    if (!createdLocally)
      // chat.addMessageListener(new GenericChatListener());
      chat.addMessageListener(chatlistener);

  }

}
