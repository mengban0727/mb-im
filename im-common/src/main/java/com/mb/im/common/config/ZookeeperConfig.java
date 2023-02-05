package com.mb.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhangjie
 */
@ConfigurationProperties(prefix = "im.zk")
@Data
public class ZookeeperConfig {

  private String root;
  private String addr;
}
