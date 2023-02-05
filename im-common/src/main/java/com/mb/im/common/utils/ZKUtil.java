package com.mb.im.common.utils;

import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

/**
 * @author zhangjie
 */
@Slf4j
public class ZKUtil {

  @Resource
  private ZkClient zkClient;

  List<String> allNode;

  /**
   * 创建父级节点
   */
  public void createRootNode(String root) {
    boolean exists = zkClient.exists(root);
    if (exists) {
      return;
    }
    //创建 root
    zkClient.createPersistent(root);
  }

  /**
   * 写入指定节点 临时目录
   */
  public void createNode(String path) {
    zkClient.createEphemeral(path);
  }

  /**
   * 获取所有注册节点
   */
  public List<String> getAllNode() {
    log.info("查询所有节点成功，节点数：" + allNode.size());
    return allNode;
  }

  /**
   * 更新server list
   */
  public void setAllNode(List<String> allNode) {
    log.info("server节点更新，节点数：" + allNode.size());
    this.allNode = allNode;
  }

}
