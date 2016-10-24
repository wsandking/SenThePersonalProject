package io.kandy.protocol.xmpp.component;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.kandy.protocol.xmpp.model.IMMessage;
import io.kandy.protocol.xmpp.model.IMMessageDeliveredAck;
import io.kandy.protocol.xmpp.model.IMMessageReceipt;
import io.kandy.protocol.xmpp.model.IMMessageReceivedResponse;

@Component
@Scope(value = "prototype")
public class IMMessageClient {

	@Value("${im.server.address}")
	private String imhost = "127.0.0.1";

	@Value("${im.server.port}")
	private int port = 9000;

	@Value("${im.server.path}")
	private String impath = "/protocol/xmpp/";

	public IMMessageReceivedResponse messageReceived(IMMessage message) {
		RestTemplate client = new RestTemplate();

		IMMessageReceivedResponse ack = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML));
		HttpEntity<IMMessage> entity = new HttpEntity<IMMessage>(message, headers);

		String url = String.format("http://%s:%d/%s%s", imhost, port, impath, "/message/received");
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

		String url = String.format("http://%s:%d/%s%s", imhost, port, impath, "/message/test");
		System.out.println("Message forwarded to service url: " + url);
		ResponseEntity<IMMessageDeliveredAck> response = client.postForEntity(url, entity, IMMessageDeliveredAck.class);

		ack = response.getBody();
		return ack;

	}

}
