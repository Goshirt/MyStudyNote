## rabbitmq的安装
- `yum install erlang` 安装erlang环境
- `yum install rabbitmq-server`安装rabbitmq服务
- `chkconfig rabbitmq-server on` 设置开机启动
- `service rabbitmq-server start`启动rabbitmq服务
- `service rabbitmq-server restart` 重启rabbitmq服务
- `service rabbitmq-server stop`关闭rabbitmq服务
- `/usr/lib/rabbitmq`阿里云上rabbitmq的bin路径
- `rabbit.app` 该文件是rabbitmq的配置文件更改为：`loopback_users,[guest]` 
- 在bin目录下的`rabbitmq-plugins`为rabbitmq的插件，使用`rabbitmq-plugins list`可以查看到默认安装的插件
- `rabbitmq-plugins enable rabbitmq_management` 启动rabbitmq的管理控制台
- `http：//ip:15672`可以访问到rabbitmq的管理控制台，前提是开放15672端口，管理控制台的默认登录用户名密码为guest

## channel


## 如何保障消息100%投递成功
1. 生产端的可靠性投递
- 保证消息成功发出
- 保证MQ节点成功接收
- 保证发送端收到MQ节点的确认应答
- 完善的消息补偿机制
2. 解决方案
- 消息持久化落库，对消息状态进行打标，后台轮旬数据库中每没有完成的消息，重发，轮旬次数边界值的设定。由于第一步的两次数据库操作，在分布式高并发场景下，数据库性能瓶颈。
  ![消息持久化落库](H:/study_note/MyStudyNote/100one.png)
- 消息的延迟投递 ，做二次确认，回调检查。step1与step2发送的是同一条消息到不同的队列，并且step2发送的可以根据业务设置相应的延迟时间，step1发送到消息进过消费者消费之后，返回一个新的信息到mq新的队列，callback service 监听该队列，并将消息持久化到msg DB，当step2发送的延迟消息到达mq时，触发对应的监听器，callback service 去msg DB 查找，如果找不到，发起rpc给上游服务器，重新执行。适合分布式高并发场景
  ![消息持久化落库](H:/study_note/MyStudyNote/100two.png)
## 如何避免消费重复消费
1. 消费端实现幂等性，确保消息不会消费多次，即使收到多条一样的消息。

2. 解决方案
- 唯一ID + 指纹码 机制，利用数据库主键去重
  (1). select count(1) from t_order where Id = 唯一Id + 指纹码。 先查找数据，查不到数据insert ,查到就不insert。
- 利用redis的原子性
## Confirm mq消息确认流程  

## Return消息机制 

## 消费端限流

## 消费端ack与重回队列

## TTl队列/消息

## 死信队列