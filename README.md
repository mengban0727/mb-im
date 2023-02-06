# mb-im聊天室
2020年学习netty后开发的一个聊天工具，使用Zookeeper+Redis实现服务端分布式部署。

## 说明
-  `服务端`启动后，向`Zookeeper`注册
-  `客户端`与`服务端`的连接，通过`路由端`选择可用`服务端`节点，redis中保存客户端与服务端的连接关系
-  `客户端`发送消息通过`路由端`，选择对应的`服务端`进行推送消息

## TODO LIST
* [x] 服务端注册
* [x] 路由端获取可用服务端节点
* [x] 客户端连接服务端
* [x] 客户端处理用户输入消息
* [x] Protobuf序列化
* [x] 消息群发功能
* [x] 服务端断线，客户端重连

## 运行
1.启动redis与Zookeeper

```java
docker run -d -p 2181:2181 --name=zookeeper zookeeper
```

```java
docker run -d -p 6379:6379 --name=redis redis:latest
```

2.编译、打包

```java
mvn -Dmaven.test.skip=true clean package
```
3.启动路由
```java
java -jar im-route\target\im-route-1.0-SNAPSHOT.jar
```
4.启动服务端
```
java -jar im-server\target\im-server-1.0-SNAPSHOT.jar --spring.profiles.active=server1
java -jar im-server\target\im-server-1.0-SNAPSHOT.jar --spring.profiles.active=server2
```
5.启动客户端
```
java -jar im-client\target\im-client-1.0-SNAPSHOT.jar --spring.profiles.active=user1
java -jar im-client\target\im-client-1.0-SNAPSHOT.jar --spring.profiles.active=user2
```
