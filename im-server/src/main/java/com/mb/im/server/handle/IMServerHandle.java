package com.mb.im.server.handle;


import com.mb.im.common.constant.MessageConstant;
import com.mb.im.common.protocol.MessageProto;
import com.mb.im.common.utils.SpringBeanFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangjie
 */
@Slf4j
public class IMServerHandle extends ChannelInboundHandlerAdapter {


  private final AttributeKey<String> userIdKey = AttributeKey.valueOf("userId");

  private final ClientProcessor clientProcessor;

  public IMServerHandle() {
    clientProcessor = SpringBeanFactory.getBean(ClientProcessor.class);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    MessageProto.MessageProtocol message = (MessageProto.MessageProtocol) msg;

    if (MessageConstant.LOGIN.equals(message.getCommand())) {
      // 登录， 保存channel
      // 直接在channel设置了userId属性值
      ctx.channel().attr(userIdKey).set(message.getUserId());
      ChannelMap.newInstance().putClient(message.getUserId(), ctx.channel());
      log.info("----客户端登录成功----， userId:{}", message.getUserId());
    } else if (MessageConstant.LOGOUT.equals(message.getCommand())) {
      // 登出
      ChannelMap.newInstance().getChannelMap().remove(message.getUserId());
      log.info("----客户端下线了----， userId:{}", message.getUserId());
    }
    log.info("----服务端收到消息----[{}]", message);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    String userId = ctx.channel().attr(userIdKey).get();
    //服务端ChannelMap删掉
    ChannelMap.newInstance().getChannelMap().remove(userId);
    // 调用route 删除redis数据
    clientProcessor.down(userId);
    log.info("----客户端强制下线了----,userId:{}", userId);
  }
}
