package com.mb.route.service;

import com.mb.im.common.bean.ChatInfo;
import java.io.IOException;

/**
 * @author zhangjie
 */
public interface RouteService {


  void sendMessage(String url, ChatInfo chat) throws IOException;
}
