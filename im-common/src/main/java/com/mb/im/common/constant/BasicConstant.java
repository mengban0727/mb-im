package com.mb.im.common.constant;

import okhttp3.MediaType;

/**
 * @author zhangjie
 */
public class BasicConstant {

  /**
   * redis中客户端服务端映射前缀
   **/
  public static final String ROUTE_PREFIX = "im-route:";

  /**
   * 响应格式
   **/
  public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
}
