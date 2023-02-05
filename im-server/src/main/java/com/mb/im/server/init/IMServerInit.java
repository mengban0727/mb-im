package com.mb.im.server.init;

import com.mb.im.common.protocol.MessageProto;
import com.mb.im.server.config.AddressConfig;
import com.mb.im.server.handle.IMServerHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangjie
 */
@Component
public class IMServerInit {

  private final static Logger LOGGER = LoggerFactory.getLogger(IMServerInit.class);

  private final EventLoopGroup acceptorGroup = new NioEventLoopGroup();
  private final EventLoopGroup workerGroup = new NioEventLoopGroup();

  @Resource
  private AddressConfig addressConfig;

  /**
   * 启动netty服务
   */
  @PostConstruct
  public void init() throws InterruptedException {
    ServerBootstrap sb = new ServerBootstrap();
    sb.group(acceptorGroup, workerGroup)
        //设置创建的Channel为NioServerSocketChannel类型
        .channel(NioServerSocketChannel.class)
        //保持长连接
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        //设置绑定IO事件的处理类
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            // google Protobuf 编解码
            pipeline.addLast(new ProtobufVarint32FrameDecoder());
            pipeline
                .addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
            pipeline.addLast(new ProtobufEncoder());
            pipeline.addLast(new IMServerHandle());
          }
        });
    ChannelFuture conn = sb.bind(addressConfig.getNettyPort()).sync();
    if (conn.isSuccess()) {
      LOGGER.info("---Netty服务端启动成功，端口[" + addressConfig.getNettyPort() + "]");
    }
  }
}
