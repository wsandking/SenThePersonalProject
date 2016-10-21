package io.kandy.protocol.xmpp.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.kandy.protocol.xmpp.model.RegisterRequest;
import io.kandy.protocol.xmpp.model.RegisterResponse;

@RestController
@RequestMapping("/protocol/xmpp")
public class XMPPController {

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public RegisterResponse greeting(@RequestBody RegisterRequest request) {
		System.out.println("Username: " + request.getUsername());
		System.out.println("Password: " + request.getPassword());

		return new RegisterResponse(100, true);
	}

}
