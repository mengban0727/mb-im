package com.mb.im.common.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangjie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfo implements Serializable {

  private String command;
  private Long time;
  private String userId;
  private String content;
}
