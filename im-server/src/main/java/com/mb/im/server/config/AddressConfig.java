package com.mb.im.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhangjie
 */
@Component
@Data
public class AddressConfig {

  @Value("${server.port}")
  private int httpPort;

  @Value("${im.server.port}")
  private int nettyPort;
}
