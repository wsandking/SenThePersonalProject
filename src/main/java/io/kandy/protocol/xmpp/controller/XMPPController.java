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
import io.kandy.protocol.xmpp.model.RegisterRequest;
import io.kandy.protocol.xmpp.model.RegisterResponse;
import io.kandy.protocol.xmpp.service.XMPPSessionManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/version/1/user/")
public class XMPPController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private XMPPSessionManager xmppSessionManager;

  /**
   * 
   * @param xmppid
   * @param request
   * @return
   */
  @ApiOperation(value = "Login", nickname = "Login")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Login Successful", response = RegisterResponse.class),
      @ApiResponse(code = 401, message = "Incorrect credential")})
  @RequestMapping(value = "/{xmppid}/protocol/xmpp/register", method = RequestMethod.POST)
  public ResponseEntity<RegisterResponse> Login(@PathVariable("xmppid") String xmppid,
      @RequestBody RegisterRequest request) {

    logger.info(String.format("System %s ----- username: %s --------- login request",
        xmppSessionManager.instanceInfoStamp().get(0), xmppid));

    ResponseEntity<RegisterResponse> response;
    try {
      String streamId = xmppSessionManager.login(request.getXmppId(), request.getPassword());
      RegisterResponse responseBody = new RegisterResponse(xmppid, streamId);

      response = new ResponseEntity<RegisterResponse>(responseBody, HttpStatus.CREATED);
    } catch (Exception e) {
      RegisterResponse responseBody = null;
      response = new ResponseEntity<RegisterResponse>(responseBody, HttpStatus.UNAUTHORIZED);
      e.printStackTrace();
    }

    return response;
  }

  /**
   * 
   * @param username
   * @return
   */
  @ApiOperation(value = "Logout", nickname = "Logout")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Logout Successful"),
      @ApiResponse(code = 400, message = "Logout Operation Failure")})
  @RequestMapping(value = "/{xmppid}/protocol/xmpp/register", method = RequestMethod.DELETE)
  public ResponseEntity<String> Logout(@PathVariable("xmppid") String username) {
    logger.info(String.format("System %s ----- user: %s logging out",
        xmppSessionManager.instanceInfoStamp().get(0), username));
    System.out.println(String.format("Username: %s about to logout", username));
    ResponseEntity<String> response;

    try {
      xmppSessionManager.logout(username);
      response = new ResponseEntity<String>("Logout Successful", HttpStatus.OK);
    } catch (Exception e) {
      response = new ResponseEntity<String>("Logout Failure", HttpStatus.BAD_REQUEST);
      e.printStackTrace();
    }
    return response;
  }

  /**
   * 
   * @param username
   * @param im
   * @return
   */
  @ApiOperation(value = "Message", nickname = "message")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Message successfully delivered to XMPP Server",
          response = IMMessageReceipt.class),
      @ApiResponse(code = 401, message = "Message send failure. Check if user has log on")})
  @RequestMapping(value = "/{xmppid}/protocol/xmpp/message", method = RequestMethod.POST)
  public ResponseEntity<IMMessageReceipt> SendMessage(@PathVariable("xmppid") String username,
      @RequestBody IMMessage im) {
    logger.info(String.format("System %s \n Message sending from %s to %s :\n %s ",
        xmppSessionManager.instanceInfoStamp().get(0), username, im.getImRequest().getToUrl(),
        im.getImRequest().getMessage()));

    System.out.println(String.format("Message send from %s to %s :\n %s ", username,
        im.getImRequest().getToUrl(), im.getImRequest().getMessage()));
    ResponseEntity<IMMessageReceipt> response;
    IMMessageReceipt receipt = new IMMessageReceipt();

    try {
      String messageId = xmppSessionManager.MiroserviceSendPlainTextMessage(username,
          im.getImRequest().getToUrl(), im.getImRequest().getMessage());

      receipt.setMessageId(messageId);
      receipt.setStatusCode(0);

      response = new ResponseEntity<IMMessageReceipt>(receipt, HttpStatus.CREATED);

    } catch (Exception e) {
      receipt.setMessageId(null);
      receipt.setStatusCode(1);

      response = new ResponseEntity<IMMessageReceipt>(receipt, HttpStatus.UNAUTHORIZED);
      e.printStackTrace();
    }
    return response;
  }



}
