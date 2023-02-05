package com.mb.route;


import com.mb.im.common.config.BeanConfiguration;
import com.mb.im.common.config.ZkClientConfig;
import com.mb.im.common.config.ZookeeperConfig;
import com.mb.im.common.utils.SpringBeanFactory;
import com.mb.im.common.utils.ZKUtil;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author zhangjie
 */
@SpringBootApplication
@Import({SpringBeanFactory.class, BeanConfiguration.class, ZKUtil.class,
    ZkClientConfig.class})
public class IMRouteApplication {


  public static void main(String[] args) {
    SpringApplication.run(IMRouteApplication.class, args);

    ZkClient zkClient = SpringBeanFactory.getBean(ZkClient.class);
    ZookeeperConfig zkConfig = SpringBeanFactory.getBean(ZookeeperConfig.class);
    ZKUtil zkUtil = SpringBeanFactory.getBean(ZKUtil.class);

    //监听/im节点下子节点的变化，实时更新server list
    zkClient.subscribeChildChanges(zkConfig.getRoot(),
        (parentPath, currentChilds) -> zkUtil.setAllNode(currentChilds));
  }
}
