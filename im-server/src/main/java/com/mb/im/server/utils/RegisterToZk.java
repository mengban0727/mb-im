package com.mb.im.server.utils;

import com.mb.im.common.config.ZookeeperConfig;
import com.mb.im.common.utils.SpringBeanFactory;
import com.mb.im.common.utils.ZKUtil;
import com.mb.im.server.config.AddressConfig;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangjie
 */
public class RegisterToZk implements Runnable {

  private final static Logger LOGGER = LoggerFactory.getLogger(RegisterToZk.class);

  private final ZKUtil zk;
  private final AddressConfig conf;
  private final ZookeeperConfig zkConfig;

  public RegisterToZk() {
    zk = SpringBeanFactory.getBean(ZKUtil.class);
    conf = SpringBeanFactory.getBean(AddressConfig.class);
    zkConfig = SpringBeanFactory.getBean(ZookeeperConfig.class);
  }

  @Override
  public void run() {
    try {
      String ip = InetAddress.getLocalHost().getHostAddress();
      String rootPath = zkConfig.getRoot();
      zk.createRootNode(rootPath);
      String path =
          rootPath + "/" + ip + ":" + conf.getNettyPort() + ":" + conf.getHttpPort();
      try {
        zk.createNode(path);
      } catch (Exception e) {
        LOGGER.error("---zk注册失败------," + e.getMessage());
      }
      LOGGER.info("------服务注册在ZK------");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
