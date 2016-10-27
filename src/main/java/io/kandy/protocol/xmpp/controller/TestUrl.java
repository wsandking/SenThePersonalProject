package io.kandy.protocol.xmpp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageDeliveredAck;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;
import io.kandy.protocol.xmpp.model.IMMessageReceivedResponse;

@RestController
@RequestMapping("/version/1/user")
public class TestUrl {

	@RequestMapping(value = "/app/xmpp/im/deliver", method = RequestMethod.POST)
	public ResponseEntity<IMMessageDeliveredAck> testPostChannel(@RequestBody IMMessageReceipt ir) {

		System.out.println(String.format("Message delivered for message id %s", ir.getMessageId()));
		IMMessageDeliveredAck res = new IMMessageDeliveredAck();
		res.setMessageId(ir.getMessageId());
		res.setStatusCode(0);
		ResponseEntity<IMMessageDeliveredAck> response = new ResponseEntity<IMMessageDeliveredAck>(res,
				HttpStatus.CREATED);

		return response;

	}

	@RequestMapping(value = "/{xmppid}/app/xmpp/im/receive", method = RequestMethod.POST)
	public ResponseEntity<IMMessageReceivedResponse> testPostReceivedChannel(@PathVariable("xmppid") String username,
			@RequestBody IMMessage im) {

		System.out.println(String.format("Message send from %s to %s :\n %s ", username, im.getImRequest().getToUrl(),
				im.getImRequest().getMessage()));
		IMMessageReceivedResponse res = new IMMessageReceivedResponse();
		res.setMessage(String.format("%s will receive message \"%s\" from %s", username, im.getImRequest().getToUrl(),
				im.getImRequest().getMessage()));
		res.setStatusCode(0);
		ResponseEntity<IMMessageReceivedResponse> response = new ResponseEntity<IMMessageReceivedResponse>(res,
				HttpStatus.CREATED);

		return response;
	}
}
