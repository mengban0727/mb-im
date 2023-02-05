package com.mb.im.handle;

import com.mb.im.common.protocol.MessageProto;
import com.mb.im.common.utils.SpringBeanFactory;
import com.mb.im.init.IMClientInit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangjie
 */
public class IMClientHandle extends ChannelInboundHandlerAdapter {

  private final static Logger LOGGER = LoggerFactory.getLogger(IMClientHandle.class);

  @Override
  public void channelActive(ChannelHandlerContext ctx) {

  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    MessageProto.MessageProtocol message = (MessageProto.MessageProtocol) msg;
    LOGGER.info("----客户端收到消息----： {}", message.getContent());

  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    LOGGER.info("-----当前连接的服务断开连接，重新连接其他服务----");
    try {
      IMClientInit client = SpringBeanFactory.getBean(IMClientInit.class);
      client.restart();
      LOGGER.info("----连接成功----");
    } catch (Exception e) {
      LOGGER.warn("--连接失败，" + e.getMessage());
    }
  }
}
