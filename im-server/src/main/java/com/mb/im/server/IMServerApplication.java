package com.mb.im.server;


import com.mb.im.common.config.BeanConfiguration;
import com.mb.im.common.config.ZkClientConfig;
import com.mb.im.common.config.ZookeeperConfig;
import com.mb.im.common.utils.SpringBeanFactory;
import com.mb.im.common.utils.ZKUtil;
import com.mb.im.server.utils.RegisterToZk;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author zhangjie
 */
@SpringBootApplication
@Import({SpringBeanFactory.class, BeanConfiguration.class, ZKUtil.class,
    ZkClientConfig.class})
public class IMServerApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(IMServerApplication.class, args);
  }

  @Override
  public void run(String... args) {
    Thread thread = new Thread(new RegisterToZk());
    thread.setName("im-server-register-thread");
    thread.start();
  }
}
