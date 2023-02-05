package com.mb.route.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mb.im.common.bean.ChatInfo;
import com.mb.route.service.RouteService;
import java.io.IOException;
import javax.annotation.Resource;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

/**
 * @author zhangjie
 */
@Service
public class RouteServiceImpl implements RouteService {

  private MediaType mediaType = MediaType.parse("application/json");
  @Resource
  private OkHttpClient okHttpClient;

  @Override
  public void sendMessage(String url, ChatInfo chat) throws IOException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("command", chat.getCommand());
    jsonObject.put("time", chat.getTime());
    jsonObject.put("userId", chat.getUserId());
    jsonObject.put("content", chat.getContent());

    RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

    Request request = new Request.Builder()
        .url(url)
        .post(requestBody)
        .build();

    Response response = okHttpClient.newCall(request).execute();
    if (!response.isSuccessful()) {
      throw new IOException("Unexpected code " + response);
    }
  }
}
