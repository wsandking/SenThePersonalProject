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
@RequestMapping("/version/1/user/")
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
  @RequestMapping(value = "/{xmppid}/brocast/message", method = RequestMethod.POST)
  public ResponseEntity<String> brocastMessage(@PathVariable("xmppid") String username,
      @RequestBody IMMessage im) {
    logger.info(String.format("****************Message Broacst!!!: %s to %s :\n %s ", username,
        im.getImRequest().getToUrl(), im.getImRequest().getMessage()));

    System.out.println(String.format("Message send from %s to %s :\n %s ", username,
        im.getImRequest().getToUrl(), im.getImRequest().getMessage()));

    /*
     * Initiliaze as Not found
     */
    ResponseEntity<String> response;
    response = new ResponseEntity<String>("", HttpStatus.NOT_FOUND);

    try {
      String messageId = xmppSessionManager.brocastMessage(username, im.getImRequest().getToUrl(),
          im.getImRequest().getMessage());
      if (null != messageId) {
        response = new ResponseEntity<String>(messageId, HttpStatus.CREATED);
      }
    } catch (Exception e) {
    }
    return response;
  }

  @ApiOperation(value = "Brocast logout", nickname = "Brocast logout")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Logout Successful"),
      @ApiResponse(code = 400, message = "Logout Operation Failure")})
  @RequestMapping(value = "/{xmppid}/brocast/message", method = RequestMethod.DELETE)
  public ResponseEntity<String> brocastLogout(@PathVariable("xmppid") String username,
      @RequestBody IMMessage im) {
    logger.info(String.format("Brocast Received: Username: %s logout", username));
    /*
     * Initiliaze as Not found
     */
    ResponseEntity<String> response;
    response = new ResponseEntity<String>("", HttpStatus.NOT_FOUND);

    try {
      String messageId = xmppSessionManager.brocastMessage(username, im.getImRequest().getToUrl(),
          im.getImRequest().getMessage());
      if (null != messageId) {
        response = new ResponseEntity<String>(messageId, HttpStatus.CREATED);
      }
    } catch (Exception e) {
    }
    return response;
  }
}
