package io.kandy.protocol.xmpp.service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jivesoftware.smack.util.TLSUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;

@Service
@Configuration
@Scope("singleton")
public class XMPPSessionManager {

	private volatile static Map<String, AbstractXMPPConnection> XMPPSessionPool;

	@Value("${xmpp.service}")
	private String defaultHostIp;

	@Value("${xmpp.server}")
	private String defaultService;

	@Value("${xmpp.port}")
	private int defaultPort;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

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

	public boolean login(String username, String passwd) throws Exception {
		boolean result = false;

		/*
		 * Check if user has already logged in and have a connection, create one
		 * otherwise.
		 */
		if (XMPPSessionPool.containsKey(username)) {
			result = true;
		} else {
			AbstractXMPPConnection connection = new XMPPTCPConnection(notSecureConnectionBuild(username, passwd));
			connection.connect();
			connection.login();

			/*
			 * Make sure connection pool operation thread safe, maybe use
			 * hashtable directly could be good
			 */
			synchronized (XMPPSessionPool) {
				XMPPSessionPool.put(username, connection);
			}
			result = true;
		}
		return result;
	}

	public boolean SendMessage(String username, Message msg) throws Exception {
		boolean result = false;

		/*
		 * Check if user has already logged in and have a connection, create one
		 * otherwise.
		 */
		if (XMPPSessionPool.containsKey(username)) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public boolean logout(String username) throws Exception {
		boolean result = false;

		/*
		 * Check if user has already logged in and have a connection, create one
		 * otherwise.
		 */
		if (XMPPSessionPool.containsKey(username)) {
			result = true;
			synchronized (XMPPSessionPool) {
				if (XMPPSessionPool.get(username) != null && XMPPSessionPool.get(username).isConnected())
					XMPPSessionPool.get(username).disconnect();
				XMPPSessionPool.remove(username);
			}
		} else {
			result = true;
		}

		return result;
	}

	private XMPPTCPConnectionConfiguration notSecureConnectionBuild(String username, String password)
			throws KeyManagementException, NoSuchAlgorithmException {

		System.out.println(String.format("Host: %s : port: %d", defaultHostIp, defaultPort));

		Builder builder;
		builder = XMPPTCPConnectionConfiguration.builder();
		builder.setUsernameAndPassword(username, password).setServiceName(defaultService).setHost(defaultHostIp)
				.setPort(defaultPort).build();

		TLSUtils.disableHostnameVerificationForTlsCertificicates(builder);
		TLSUtils.acceptAllCertificates(builder);

		return builder.build();
	}
}
