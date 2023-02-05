package com.mb.im.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mb.im.common.bean.ChatInfo;
import com.mb.im.common.bean.ServerInfo;
import com.mb.im.common.config.RouteConfig;
import com.mb.im.common.constant.BasicConstant;
import com.mb.im.common.constant.MessageConstant;
import com.mb.im.common.protocol.MessageProto;
import com.mb.im.config.UserConfig;
import com.mb.im.handle.IMClientHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;

/**
 * @author zhangjie
 */
@Component
@Slf4j
public class IMClientInit {

  private Channel channel;

  @Resource
  private UserConfig userConfig;
  @Resource
  private RouteConfig routeConfig;
  @Resource
  private OkHttpClient okHttpClient;

  private ServerInfo server;

  @PostConstruct
  public void start() {
    if (server != null) {
      log.warn("---已经连接了服务---");
      return;
    }
    //1.获取服务端ip+port
    server = getServerInfo();
    System.out.println("client get server :" + server);
    //2.启动客户端
    startClient(server);
    //3.登录到服务端
    registerToServer();
  }

  /**
   * 与服务端通信
   */
  private void registerToServer() {
    MessageProto.MessageProtocol login = MessageProto.MessageProtocol.newBuilder()
        .setUserId(userConfig.getId())
        .setContent(userConfig.getName())
        .setCommand(MessageConstant.LOGIN)
        .setTime(System.currentTimeMillis())
        .build();
    channel.writeAndFlush(login);
  }

  /**
   * 启动客户端，建立连接
   */
  private void startClient(ServerInfo server) {
    EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("im-client-work"));
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            // google Protobuf 编解码
            pipeline.addLast(new ProtobufVarint32FrameDecoder());
            pipeline
                .addLast(new ProtobufDecoder(MessageProto.MessageProtocol.getDefaultInstance()));
            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
            pipeline.addLast(new ProtobufEncoder());
            pipeline.addLast(new IMClientHandle());
          }
        });

    ChannelFuture future = null;
    try {
      future = bootstrap.connect(server.getIp(), server.getNettyPort()).sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
      log.info("连接失败");
    }
    if (future != null && future.isSuccess()) {
      log.info("---客户端启动成功[nettyPort:" + server.getNettyPort() + "]---");
      channel = future.channel();
    }

  }

  /**
   * 向路由服务器获取服务端IP与端口
   */
  private ServerInfo getServerInfo() {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("userId", userConfig.getId());
      jsonObject.put("userName", userConfig.getName());
      RequestBody requestBody = RequestBody.create(BasicConstant.MEDIA_TYPE, jsonObject.toString());

      //im.route.login.url=http://localhost:8880/login
      Request request = new Request.Builder()
          .url(routeConfig.getLoginUrl())
          .post(requestBody)
          .build();
      Response response = okHttpClient.newCall(request).execute();
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      ServerInfo service;
      ResponseBody body = response.body();
      try {
        String json = body.string();
        service = JSON.parseObject(json, ServerInfo.class);
      } finally {
        body.close();
      }
      return service;
    } catch (IOException e) {
      log.error("连接失败！");
    }
    return null;
  }


  public void sendMessage(ChatInfo chat) {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("command", chat.getCommand());
      jsonObject.put("time", chat.getTime());
      jsonObject.put("userId", chat.getUserId());
      jsonObject.put("content", chat.getContent());
      RequestBody requestBody = RequestBody.create(BasicConstant.MEDIA_TYPE, jsonObject.toString());
      //im.route.chat.url=http://localhost:8880/chat
      Request request = new Request.Builder()
          .url(routeConfig.getChatUrl())
          .post(requestBody)
          .build();
      Response response = okHttpClient.newCall(request).execute();
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Channel getChannel() {
    return this.channel;
  }

  /**
   * 处理登出指令
   */
  public void clear() {
    logoutRoute();
    //登出前发一条消息
    logoutServer();
    server = null;
    //关闭通道
//    ChannelFuture future = channel.close();
//    future.addListener((ChannelFutureListener) future1 -> {
//      if (future1.isSuccess()) {
//        log.info("连接顺利断开");
//      }
//    });
  }

  /**
   * 调用server处理登出
   */
  private void logoutServer() {
    MessageProto.MessageProtocol message = MessageProto.MessageProtocol.newBuilder()
        .setUserId(userConfig.getId())
        .setContent(userConfig.getName())
        .setCommand(MessageConstant.LOGOUT)
        .setTime(System.currentTimeMillis()).build();
    channel.writeAndFlush(message);
  }

  /**
   * 调用路由端处理redis
   */
  private void logoutRoute() {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("userId", userConfig.getId());
      RequestBody requestBody = RequestBody.create(BasicConstant.MEDIA_TYPE, jsonObject.toString());

      Request request = new Request.Builder()
          .url(routeConfig.getLogoutUrl())
          .post(requestBody)
          .build();
      Response response = okHttpClient.newCall(request).execute();
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 客户端重连
   */
  public void restart() {
    logoutRoute();
    server = null;
    start();
  }
}
