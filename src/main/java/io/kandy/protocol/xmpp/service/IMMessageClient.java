package io.kandy.protocol.xmpp.service;

import java.util.Arrays;

import org.jivesoftware.smack.packet.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageDeliveredAck;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;
import io.kandy.protocol.xmpp.model.IMMessageReceivedResponse;

@Service
@Scope("singleton")
public class IMMessageClient {

	@Autowired
	private ConfigurationService configurationService;

	public IMMessageReceivedResponse messageReceived(Message message) {
		RestTemplate client = new RestTemplate();

		IMMessage im = new IMMessage(message.getTo(), message.getBody());

		IMMessageReceivedResponse ack = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML));
		HttpEntity<IMMessage> entity = new HttpEntity<IMMessage>(im, headers);

		String url = String.format("http://%s:%d/%s/%s", configurationService.getImhost(),
				configurationService.getPort(), configurationService.getImpath(),
				message.getFrom() + "/protocol/xmpp/message/received");
		System.out.println("Message forwarded to service url: " + url);
		ResponseEntity<IMMessageReceivedResponse> response = client.postForEntity(url, entity,
				IMMessageReceivedResponse.class);

		ack = response.getBody();
		return ack;
	}

	public IMMessageDeliveredAck messageDelievered(IMMessageReceipt message) {
		RestTemplate client = new RestTemplate();

		IMMessageDeliveredAck ack = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML));
		HttpEntity<IMMessageReceipt> entity = new HttpEntity<IMMessageReceipt>(message, headers);

		String url = String.format("http://%s:%d/%s%s", configurationService.getImhost(),
				configurationService.getPort(), configurationService.getImpath(), "/message/test");
		System.out.println("Message forwarded to service url: " + url);
		ResponseEntity<IMMessageDeliveredAck> response = client.postForEntity(url, entity, IMMessageDeliveredAck.class);

		ack = response.getBody();
		return ack;

	}

}
