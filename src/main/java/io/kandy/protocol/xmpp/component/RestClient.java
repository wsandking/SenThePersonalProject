package io.kandy.protocol.xmpp.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient {

	@Value("${im.server.address}")
	private String imhost;

	@Value("${im.server.port}")
	private int port;

	@Value("${im.server.path}")
	private String impath;

	private RestTemplate client;

}
