package com.mb.im;

import com.mb.im.common.config.BeanConfiguration;
import com.mb.im.common.utils.SpringBeanFactory;
import com.mb.im.handle.ScannerHandle;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author zhangjie
 */
@SpringBootApplication
@Import({SpringBeanFactory.class, BeanConfiguration.class})
public class IMClientApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(IMClientApplication.class, args);
  }

  @Override
  public void run(String... args) {
    try {
      Thread thread = new Thread(new ScannerHandle());
      thread.setName("client-scanner-thread");
      thread.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
