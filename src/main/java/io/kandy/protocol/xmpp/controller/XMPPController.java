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
	public ResponseEntity<String> SendMessage(@RequestBody IMMessage im) {

		System.out.println(
				String.format("Message deliver from %s to %s :\n %s ", im.getFrom(), im.getTo(), im.getPlainMessage()));
		ResponseEntity<String> response;

		try {
			xmppSessionManager.SendPlainTextMessage(im.getFrom(), im.getTo(), im.getPlainMessage());
			response = new ResponseEntity<String>("Message successfully sent to XMPP Server", HttpStatus.CREATED);
		} catch (Exception e) {
			response = new ResponseEntity<String>("Message deliver failure", HttpStatus.UNAUTHORIZED);
			e.printStackTrace();
		}
		return response;
	}

}
