package io.kandy.protocol.xmpp.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class XMPPSessionManager {

	private volatile static Map<String, AbstractXMPPConnection> XMPPSessionPool;

	@PostConstruct
	public void initIn() {
		/*
		 * Initialize the connection pool, at this point, initial XMPP
		 * Connection
		 */
		XMPPSessionPool = new HashMap<String, AbstractXMPPConnection>();
	}

	@PreDestroy
	public void cleanUp() throws Exception {
		/*
		 * Make sure all the connection inside has been closed properly
		 */
		for (String connectionName : XMPPSessionPool.keySet()) {
			AbstractXMPPConnection connection = XMPPSessionPool.get(connectionName);
			connection.disconnect();
		}

	}

	public boolean login(String username, String passwd) {
		boolean result = false;
		
		

		return result;
	}

}
