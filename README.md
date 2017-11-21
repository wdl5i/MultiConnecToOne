# 多连接转单连接服务

## 功能描述

需求是这样的：

这个服务会监停一个端口，允许外部多个连接的接入，并可以把这个连接上的数据包汇总发到后端的一个连接上。简单的说，就是一个 N 对 1 的数据处理器。把 N 个 TCP 数据流合成一个数据流。

一个服务的处理上限是 64K 的连接，使用 2 字节的 id 号区分不同的外部连接。我定义了简单的协议，每个数据片段有 3 字节的数据头。分别是数据长度一字节和 2 字节的连接 id 号。

这个服务仅仅做数据流的合并，而不规定数据逻辑上的分包。对内的数据管道上看起来的数据流就是这样的：

```len id_lo id_hi content ... len id_lo id_hi content ...  len id_lo id_hi content ...``` 
处理合并起来的数据流非常简单，只需要通过一个 IO 管道 ，方便后端的程序不再考虑多连接的问题。

后端服务需要可以控制连接服务器。最基本的功能就是可以强制断开某个外部连接。并且可以获得新的外部连接接入或离开的信号。

更进一步，应该由后端服务器来控制连接服务器对外监听端口的开启与关闭，以及外部连接的上限等。

## 实现
1. 由于允许多客户端(64000)连接，故client和connection之间采用tcp连接，没有采用分布式的event bus
2. connection和logic都是服务器，数量有限，且ip固定，采用更方便的分布式event bus
3. 在logic逻辑服务程序中，本打算采用vertx unit(单元测试)实现控制client连接，由于细节问题，暂未实现


## 编译


```mvn clean install```


## 部署

1. Copy client/target/client-1.0-SNAPSHOT-fat.jar to A machine

2. Copy connection/target/client-1.0-SNAPSHOT-fat.jar to B machine 

3. Copy connection/target/client-1.0-SNAPSHOT-fat.jar to C machine

## 运行

1. Client:

```java -jar client-1.0-SNAPSHOT-fat.jar```


2. Connection:

```java -jar connection-1.0-SNAPSHOT-fat.jar -cluster -cluster-host XXX.XXX.XXX.XX```

2. Logic:

```java -jar logic-1.0-SNAPSHOT-fat.jar -cluster -cluster-host XXX.XXX.XXX.XX```



