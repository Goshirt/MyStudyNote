## rabbitmq的安装
- `yum install erlang` 安装erlang环境
- `yum install rabbitmq-server`安装rabbitmq服务
- `chkconfig rabbitmq-server on` 设置开机启动
- `service rabbitmq-server start`启动rabbitmq服务
- `service rabbitmq-server restart` 重启rabbitmq服务
- `service rabbitmq-server stop`关闭rabbitmq服务
- `/usr/lib/rabbitmq`阿里云上rabbitmq的bin路径
- `rabbit.app` 该文件是rabbitmq的配置文件更改为：`loopback_users,[guest]` 
- `/var/log/rabbitmq/rabbit\@Helmet_aliyun.log` 日志文件的位置,可以通过该日志查看链接情况
- 在bin目录下的`rabbitmq-plugins`为rabbitmq的插件，使用`rabbitmq-plugins list`可以查看到默认安装的插件
- `rabbitmq-plugins enable rabbitmq_management` 启动rabbitmq的管理控制台
- `http：//ip:15672`可以访问到rabbitmq的管理控制台，前提是开放15672端口，管理控制台的默认登录用户名密码为guest

## rabbitmq的基本使用
1. 账号的管理
   - `rabbitmqclt add_user <userName> <password>` 新建一个用户及密码
   - `rabbitmqclt list_user` 查看现有的用户列表
   - `rabbitmqclt delete <userName> ` 删除用户
   - `rabbitmqclt change_user <userName> <newpassword>`更改密码
2. 角色管理，rabbitmq有五种角色
- `none`不能进入Web Management,只能发送和接收消息,一般生产者和消费者使用这个角色
- `management`可以通过AMQP做的任何事，列出自己可以通过AMQP登入的virtual hosts，查看自己的virtual hosts中的queues, exchanges 和 bindings，查看和关闭自己的channels 和 connections，查看有关自己的virtual hosts的“全局”的统计信息，包含其他用户在这些virtual hosts中的活动
- `policymaker `management可以做的任何事外，还可以查看、创建和删除自己的virtual hosts所属的policies和parameters
- `monitoring `management可以做的任何事外，列出所有virtual hosts，包括他们不能登录的virtual hosts，查看其他用户的connections和channels，查看节点级别的数据如clustering和memory使用情况，查看真正的关于所有virtual hosts的全局的统计信息
- `administrator `超级管理员,拥有最高的权限，可以做任何事,guest默认就是超级管理员，并且从3.5版本开始不能使用guest远程登录
   - `rabbitmqctl set_user_tags <username> <tag>`给用户分配角色，默认新建的用户角色为none
   - `rabbitmqctl list_user_permissions <username>` 查看用户权限
   - `rabbitmqctl clear_permissions[-p <vhostpath>] <username>` 清楚用户权限
   - `rabbitmqctl set_permissions [-p <vhostpath>] <user> <conf> <write> <read>`设置用户可访问的虚拟路径的权限，ps:后三个参数可以使用通配符，例如给helmet用户设置访问虚拟主机/，并具有可配置，可读可写的权限 `rabbitmqctl set_permissions -p / helmet ".*" ".*" ".*"`
# exchange 类型
- direct 由生产者与消费指定一样的routingkey
- topic
- fanout
- header
- 
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

## RabbitTemplate 使用

## rabbitmq 整合spring

## rabbitmq 整合springboot

## rabbitmq 整合springcloud

## rabbitmq 集群架构模式（第五章）
1. 主备模式（并发数据量不大的时候使用），一主多备，主节点提供读写，只有当主节点挂掉之后，备份节点才升为主节点
2. 远程模式
  
3. 镜像模式
  - 准备rabbitmq三个节点,按正常启动后，停止三个节点的服务`rabbitmqctl stop`
  - 把其中准备作为Master节点的cookie文件复制到另外两个节点中 `scp /var/lib/rabbitmq/.erlang.cookie ip:/var/lib/rabbitmq/`
  - 确保已经修改每个节点的主机名 `vi /etc/hostname` 每个节点所在的主机名可以自定义
  - `rabbitmq-server -detached` 启动每一个节点
  - 另外连个节点相应执行以下命令 ，加入到主节点中`rabbitmqctl stop_app`  `rabbitmqctl join_cluster rabbit@{master 的host name}` `rabbitmqctl start_app`
  - 执行镜像队列复制 `rabbitmqctl set_policy ha-all "^" ' {"ha-mode":"all"}`
  - 修改集群的名字（任意一个节点） `rabbitmqctl set_cluster_name {name}`
  - 查看集群状态(任意一个节点) `rabbitmqctl cluster_status`
  - 
  - 使用HAProxy 进行负载均衡
  - 用新的两个节点 安装HAProxy
  - 使用KeepAlived 实现高可用，解决HAProxy单点故障
  - 在HAProxy节点中安装KeepAlived
  - 延迟插件的使用，指定延迟时间
4. 多活模式

## set（单元化架构）（第六章）