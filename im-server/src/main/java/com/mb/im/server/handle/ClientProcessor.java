package com.mb.im.server.handle;


import com.alibaba.fastjson.JSONObject;
import com.mb.im.common.config.RouteConfig;
import com.mb.im.common.constant.BasicConstant;
import java.io.IOException;
import javax.annotation.Resource;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

/**
 * @author zhangjie
 */
@Component
public class ClientProcessor {

  @Resource
  private OkHttpClient okHttpClient;

  @Resource
  private RouteConfig routeConfig;

  public void down(String userId) {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("userId", userId);
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
}
