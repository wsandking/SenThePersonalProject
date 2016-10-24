package io.kandy.protocol.xmpp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageDeliveredAck;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;
import io.kandy.protocol.xmpp.model.IMMessageReceivedResponse;
import io.kandy.protocol.xmpp.model.RegisterRequest;
import io.kandy.protocol.xmpp.service.XMPPSessionManager;

@RestController
@RequestMapping("/protocol/xmpp")
public class XMPPController {

	@Autowired
	private XMPPSessionManager xmppSessionManager;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<String> Login(@RequestBody RegisterRequest request) {

		System.out.println(
				String.format("Username: %s --------- Password: %s", request.getUsername(), request.getPassword()));
		ResponseEntity<String> response;
		try {
			xmppSessionManager.login(request.getUsername(), request.getPassword());
			response = new ResponseEntity<String>("Login Successful", HttpStatus.CREATED);
		} catch (Exception e) {
			response = new ResponseEntity<String>("Login Failure", HttpStatus.UNAUTHORIZED);
			e.printStackTrace();
		}
		return response;
	}

	@RequestMapping(value = "/register", method = RequestMethod.DELETE)
	public ResponseEntity<String> Logout(@RequestParam String username) {

		System.out.println(String.format("Username: %s about to logout", username));
		ResponseEntity<String> response;

		try {
			xmppSessionManager.logout(username);
			response = new ResponseEntity<String>("Logout Successful", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			response = new ResponseEntity<String>("Logout Failure", HttpStatus.BAD_REQUEST);
			e.printStackTrace();
		}
		return response;
	}

	@RequestMapping(value = "/sendim", method = RequestMethod.POST)
	public ResponseEntity<IMMessageReceipt> SendMessage(@RequestBody IMMessage im) {

		System.out.println(
				String.format("Message send from %s to %s :\n %s ", im.getFrom(), im.getTo(), im.getPlainMessage()));
		ResponseEntity<IMMessageReceipt> response;
		IMMessageReceipt receipt = new IMMessageReceipt();

		try {
			String messageId = xmppSessionManager.SendPlainTextMessage(im.getFrom(), im.getTo(), im.getPlainMessage());

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

	@RequestMapping(value = "/message/test", method = RequestMethod.POST)
	public ResponseEntity<IMMessageDeliveredAck> testPostChannel(@RequestBody IMMessageReceipt ir) {

		System.out.println(String.format("Message delivered for message id %s", ir.getMessageId()));
		IMMessageDeliveredAck res = new IMMessageDeliveredAck();
		res.setMessageId(ir.getMessageId());
		res.setStatusCode(0);
		ResponseEntity<IMMessageDeliveredAck> response = new ResponseEntity<IMMessageDeliveredAck>(res,
				HttpStatus.CREATED);

		return response;

	}

	@RequestMapping(value = "/message/received", method = RequestMethod.POST)
	public ResponseEntity<IMMessageReceivedResponse> testPostReceivedChannel(@RequestBody IMMessage im) {

		System.out.println(
				String.format("Message send from %s to %s :\n %s ", im.getFrom(), im.getTo(), im.getPlainMessage()));
		IMMessageReceivedResponse res = new IMMessageReceivedResponse();
		res.setMessage(
				String.format("%s will receive message \"%s\" from %s", im.getTo(), im.getPlainMessage(), im.getTo()));
		res.setStatusCode(0);
		ResponseEntity<IMMessageReceivedResponse> response = new ResponseEntity<IMMessageReceivedResponse>(res,
				HttpStatus.CREATED);

		return response;
	}

}
