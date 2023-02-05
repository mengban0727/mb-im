package com.mb.im.handle;

import com.mb.im.common.bean.ChatInfo;
import com.mb.im.common.constant.MessageConstant;
import com.mb.im.common.utils.SpringBeanFactory;
import com.mb.im.config.UserConfig;
import com.mb.im.init.IMClientInit;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author zhangjie
 */
@Slf4j
public class ScannerHandle implements Runnable {

  private final IMClientInit client;
  private final UserConfig userConfig;

  public ScannerHandle() {
    client = SpringBeanFactory.getBean(IMClientInit.class);
    userConfig = SpringBeanFactory.getBean(UserConfig.class);
  }

  @Override
  public void run() {
    try {
      Scanner scanner = new Scanner(System.in);
      while (true) {
        String msg = scanner.nextLine();
        if (StringUtils.isEmpty(msg)) {
          log.info("---不允许发送空消息---");
          continue;
        }

        if (MessageConstant.LOGOUT.equals(msg)) {
          // 登出指令
          client.clear();
          log.info("---客户端主动下线，如需重新进入，请输入LOGIN---");
          continue;
        } else if (MessageConstant.LOGIN.equals(msg)) {
          client.start();
          log.info("--重新登录");
          continue;
        }

        ChatInfo chat = new ChatInfo(MessageConstant.CHAT, System.currentTimeMillis(),
            userConfig.getId(), msg);
        client.sendMessage(chat);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
