package com.mb.im.common.config;

import javax.annotation.Resource;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.context.annotation.Bean;

/**
 * @author zhangjie
 */

public class ZkClientConfig {

  @Resource
  private ZookeeperConfig zkConfig;

  @Bean(name = "zkClient")
  public ZkClient zkClient() {
    return new ZkClient(zkConfig.getAddr());
  }
}
