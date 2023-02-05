package com.mb.im.server.handle;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangjie
 */
public class ChannelMap {

  private static ChannelMap instance;
  /**
   * userId,channel
   */
  private final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

  private ChannelMap() {
  }

  /**
   * 调用时初始化，延迟加载
   */
  public static ChannelMap newInstance() {
    if (instance == null) {
      instance = new ChannelMap();
    }
    return instance;
  }

  public Map<String, Channel> getChannelMap() {
    return CHANNEL_MAP;
  }

  public void putClient(String userId, Channel channel) {
    CHANNEL_MAP.put(userId, channel);
  }

  public Channel getClient(String userId) {
    return CHANNEL_MAP.get(userId);
  }
}
