package com.mb.im.server.controller;

import com.mb.im.common.bean.ChatInfo;
import com.mb.im.common.constant.MessageConstant;
import com.mb.im.common.protocol.MessageProto;
import com.mb.im.server.handle.ChannelMap;
import io.netty.channel.Channel;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangjie
 */
@RestController
public class IMServerController {

  private final static Logger LOGGER = LoggerFactory.getLogger(IMServerController.class);

  @PostMapping("/pushMessage")
  public void pushMessage(@RequestBody ChatInfo chat) {
    // 1.获取消息对象
    MessageProto.MessageProtocol message = MessageProto.MessageProtocol.newBuilder()
        .setCommand(chat.getCommand())
        .setTime(chat.getTime())
        .setUserId(chat.getUserId())
        .setContent(chat.getContent()).build();

    // 2.发给指定的client（私聊、群聊）
    if (MessageConstant.CHAT.equals(message.getCommand())) {
      for (Map.Entry<String, Channel> entry : ChannelMap.newInstance().getChannelMap().entrySet()) {
        // 过滤掉当前发送消息的用户本身
        if (!entry.getKey().equals(message.getUserId())) {
          LOGGER.info("---服务端向{}用户发送了消息，来自于userId:{}", entry.getKey(), chat.getUserId());
          entry.getValue().writeAndFlush(message);
        }
      }
    }
  }
}
