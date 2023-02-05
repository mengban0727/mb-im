package com.mb.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhangjie
 */
@ConfigurationProperties(prefix = "im.route")
@Data
public class RouteConfig {

  private String loginUrl;
  private String chatUrl;
  private String logoutUrl;

}
