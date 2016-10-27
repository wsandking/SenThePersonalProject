package io.kandy.protocol.xmpp.service;

import java.util.Arrays;

import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageDeliveredAck;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;

@Service
@Scope("singleton")
public class IMMessageClient {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ConfigurationService configurationService;

	public HttpStatus messageSendToIM(Message message) {
		RestTemplate client = new RestTemplate();

		String receiver = this.makeUsername(message.getTo());

		IMMessage im = new IMMessage(receiver, message.getBody());

		HttpStatus ack = HttpStatus.NO_CONTENT;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML));
		HttpEntity<IMMessage> entity = new HttpEntity<IMMessage>(im, headers);

		/*
		 * senwangtest1@127.0.0.1/Spark
		 */

		String sender = this.makeUsername(message.getFrom());

		/*
		 * Comment it out for now
		 */
		// String url = String.format("http://%s:%d/%s/%s",
		// configurationService.getImhost(),
		// configurationService.getPort(), configurationService.getImpath(),
		// sender + "/app/xmpp/im/receive");

		String url = String.format("http://%s:%d/%s/%s", configurationService.getImhost(),
				configurationService.getPort(), configurationService.getImpath(), sender + "/app/xmpp/im/send");
		logger.info("Message forwarded to service url: " + url);
		System.out.println("**************Message forwarded to service url: " + url);
		ResponseEntity<?> response = client.exchange(url, HttpMethod.POST, entity, String.class);

		ack = response.getStatusCode();
		return ack;
	}

	/*
	 * For future handling
	 */
	// public IMMessageReceivedResponse messageSendToIM(Message message) {
	// RestTemplate client = new RestTemplate();
	//
	// String receiver = this.makeUsername(message.getTo());
	//
	// IMMessage im = new IMMessage(receiver, message.getBody());
	//
	// IMMessageReceivedResponse ack = null;
	// HttpHeaders headers = new HttpHeaders();
	// headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON,
	// MediaType.APPLICATION_ATOM_XML));
	// HttpEntity<IMMessage> entity = new HttpEntity<IMMessage>(im, headers);
	//
	// /*
	// * senwangtest1@127.0.0.1/Spark
	// */
	//
	// String sender = this.makeUsername(message.getFrom());
	//
	// /*
	// * Comment it out for now
	// */
	// // String url = String.format("http://%s:%d/%s/%s",
	// // configurationService.getImhost(),
	// // configurationService.getPort(), configurationService.getImpath(),
	// // sender + "/app/xmpp/im/receive");
	//
	// String url = String.format("http://%s:%d/%s/%s",
	// configurationService.getImhost(),
	// configurationService.getPort(), configurationService.getImpath(), sender
	// + "/app/xmpp/im/send");
	//
	// System.out.println("Message forwarded to service url: " + url);
	// ResponseEntity<IMMessageReceivedResponse> response =
	// client.postForEntity(url, entity,
	// IMMessageReceivedResponse.class);
	//
	// ack = response.getBody();
	// return ack;
	// }

	public IMMessageDeliveredAck messageDelievered(IMMessageReceipt message) {
		RestTemplate client = new RestTemplate();

		IMMessageDeliveredAck ack = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML));
		HttpEntity<IMMessageReceipt> entity = new HttpEntity<IMMessageReceipt>(message, headers);

		/*
		 * Waiting for message delivered API finished
		 */
		String url = String.format("http://%s:%d/%s%s", "127.0.0.1", configurationService.getPort(),
				configurationService.getImpath(), "/app/xmpp/im/deliver");
		System.out.println("Message forwarded to service url: " + url);
		ResponseEntity<IMMessageDeliveredAck> response = client.postForEntity(url, entity, IMMessageDeliveredAck.class);

		ack = response.getBody();
		return ack;

	}

	private String makeUsername(String username) {

		if (username.contains("/")) {
			String[] parts = username.split("/");
			username = parts[0];

		}

		if (username.contains("@")) {
			String[] anotherparts = username.split("@");
			username = anotherparts[0] + "@" + configurationService.getDomainName();
		}

		return username;
	}
}
