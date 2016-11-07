package io.kandy.protocol.xmpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class XMPPApplication {

  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(XMPPApplication.class);

    ApplicationContext ctx = SpringApplication.run(XMPPApplication.class, args);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        if (ctx instanceof ConfigurableApplicationContext) {
          logger.info("Application closing");
          ((ConfigurableApplicationContext) ctx).close();
        }
      }
    });
  }

}
