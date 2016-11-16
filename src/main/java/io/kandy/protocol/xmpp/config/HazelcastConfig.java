//package io.kandy.protocol.xmpp.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.hazelcast.client.config.ClientConfig;
//import com.hazelcast.config.Config;
//import com.hazelcast.client.HazelcastClient;
//import com.hazelcast.core.Hazelcast;
//import com.hazelcast.core.HazelcastInstance;
//
//
//@Configuration
//public class HazelcastConfig {
//
//  @Bean
//  public HazelcastInstance initServer() {
//    Config cfg = new Config();
//    HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
//    return instance;
//  }
//
//  /*
//   * Client Object
//   */
//  @Bean
//  public HazelcastInstance initClient() {
//    ClientConfig clientConfig = new ClientConfig();
//    clientConfig.getGroupConfig().setName("dev").setPassword("dev-pass");
//    clientConfig.getNetworkConfig().addAddress("172.28.19.60:5701");
//    HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
//    return client;
//  }
//
//
//
//}
