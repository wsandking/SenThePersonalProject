package io.kandy.protocol.xmpp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class XMPPApplication {
  public static void main(String[] args) {
    SpringApplication.run(XMPPApplication.class, args);
  }

}
