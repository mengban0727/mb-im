package com.mb.route.controller;

import com.mb.im.common.bean.ChatInfo;
import com.mb.im.common.bean.ServerInfo;
import com.mb.im.common.bean.UserInfo;
import com.mb.im.common.constant.BasicConstant;
import com.mb.im.common.utils.ZKUtil;
import com.mb.route.service.RouteService;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangjie
 */
@RestController
@RequestMapping("/")
@Slf4j
public class IMRouteController {

  private final AtomicLong index = new AtomicLong();

  @Resource
  private ZKUtil zk;
  @Resource
  private RedisTemplate<String, String> redisTemplate;
  @Resource
  private RouteService routeService;

  /**
   * 客户端登录，发现可用服务端: 1、获取所有zk上的节点； 2、轮询法得到一个节点
   **/
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ServerInfo login(@RequestBody UserInfo userInfo) {
    String server = "";
    try {
      List<String> all = zk.getAllNode();
      if (all.size() <= 0) {
        log.info("no server start...");
        return null;
      }
      long position = index.incrementAndGet() % all.size();
      if (position < 0) {
        position = 0L;
      }
      server = all.get((int) position);
      //在route端，绑定了client与server的关系
      redisTemplate.opsForValue().set(BasicConstant.ROUTE_PREFIX + userInfo.getUserId(), server);
      log.info("get server info :" + server);
    } catch (Exception e) {
      e.printStackTrace();
    }
    String[] serv = server.split(":");
    return new ServerInfo(serv[0], Integer.parseInt(serv[1]),
        Integer.parseInt(serv[2]));
  }


  /**
   * 分发消息
   **/
  @RequestMapping(value = "/chat", method = RequestMethod.POST)
  public void chat(@RequestBody ChatInfo chat) {
    //判断userId是否登录——从缓存取数据 ...
    String islogin = redisTemplate.opsForValue().get(BasicConstant.ROUTE_PREFIX + chat.getUserId());
    if (StringUtils.isEmpty(islogin)) {
      log.info("该用户并未登录[" + chat.getUserId() + "]");
      return;
    }
    try {
      //从ZK拿到所有节点，分发消息
      List<String> all = zk.getAllNode();
      for (String server : all) {
        String[] serv = server.split(":");
        String ip = serv[0];
        int httpPort = Integer.parseInt(serv[2]);
        String url = "http://" + ip + ":" + httpPort + "/pushMessage";
        routeService.sendMessage(url, chat);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 客户端下线，从缓存中删除客户端与服务端映射关系
   **/
  @RequestMapping(value = "/logout", method = RequestMethod.POST)
  public void logout(@RequestBody UserInfo userInfo) {
    redisTemplate.opsForValue().getOperations()
        .delete(BasicConstant.ROUTE_PREFIX + userInfo.getUserId());
    log.info("路由端处理了用户下线逻辑：" + userInfo.getUserId());
  }
}
