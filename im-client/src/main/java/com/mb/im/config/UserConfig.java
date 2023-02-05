package com.mb.im.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangjie
 */
@Component
@Data
@ConfigurationProperties(prefix = "im.user")
public class UserConfig {

  private String id;
  private String name;
}
