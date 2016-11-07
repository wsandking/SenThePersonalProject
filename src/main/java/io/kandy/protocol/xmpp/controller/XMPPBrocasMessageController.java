package io.kandy.protocol.xmpp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;
import io.kandy.protocol.xmpp.service.XMPPSessionManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@RequestMapping("/brocast")
public class XMPPBrocasMessageController {


  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private XMPPSessionManager xmppSessionManager;

  /*
   * Two types of requests that need to be handled, one is `
   */
  @ApiOperation(value = "Brocast Message", nickname = "Brocast message")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Message successfully found and brocast",
          response = IMMessageReceipt.class),
      @ApiResponse(code = 404, message = "Cannot find user in this host")})
  @RequestMapping(value = "/{xmppid}/message", method = RequestMethod.POST)
  public ResponseEntity<IMMessageReceipt> ForwardMessage(@PathVariable("xmppid") String username,
      @RequestBody IMMessage im) {
    logger.info(String.format("****************Message Broacst!!!: %s to %s :\n %s ", username,
        im.getImRequest().getToUrl(), im.getImRequest().getMessage()));

    System.out.println(String.format("Message send from %s to %s :\n %s ", username,
        im.getImRequest().getToUrl(), im.getImRequest().getMessage()));

    /*
     * Initiliaze as Not found
     */
    ResponseEntity<IMMessageReceipt> response;
    IMMessageReceipt receipt = new IMMessageReceipt();
    response = new ResponseEntity<IMMessageReceipt>(receipt, HttpStatus.NOT_FOUND);

    try {
      String messageId = xmppSessionManager.brocastMessage(username, im.getImRequest().getToUrl(),
          im.getImRequest().getMessage());
      if (null != messageId) {
        receipt.setMessageId(messageId);
        receipt.setStatusCode(0);
        response = new ResponseEntity<IMMessageReceipt>(receipt, HttpStatus.CREATED);
      }
    } catch (Exception e) {

    }
    return response;
  }
}
