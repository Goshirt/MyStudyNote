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
- 消息持久化落库，对消息状态进行打标，后台轮旬数据库中每没有完成的消息，重发，轮旬次数边界值的设定。
![消息持久化落库](https://github.com/Goshirt/MyStudyNote/blob/master/100one.png)
- 消息的延迟投递 ，做二次确认，回调检查

## 如何避免消费重复消费
