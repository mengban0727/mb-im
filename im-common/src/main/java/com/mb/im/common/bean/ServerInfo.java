package com.mb.im.common.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangjie
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfo implements Serializable {

  private String ip;
  private Integer nettyPort;
  private Integer httpPort;
}
