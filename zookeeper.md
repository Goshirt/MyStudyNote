### 安装
1. 进入指定的文件夹，下载zookeeper安装包
- `wget http://mirror.bit.edu.cn/apache/zookeeper/zookeeper-3.4.14/zookeeper-3.4.14.tar.gz`
2. 解压
- `tar -zxvf zookeeper-3.4.14.tar.gz` 
3. 进入zookeeper的解压文件夹的bin目录，启动zookeeper
- `./zkServer.sh start`
4. 查看zookeeper状态
- `./zkServer.sh status`
5. 使用客户端连接zookeeper
- `./zllkCli.sh`

### zookeeper常用命令
打开zkCli.sh 默认连接的是本机的zookeeper服务，zookeeper的watch是一次性的，触发一次之后立即实效。在集群模式下时，watch只会在建立watch的服务器上生效，例如：在server1 的节点`/test`中建立'get /test watch' 当其他server修改`set /test {newData}`触发时，只在server1中有watch信息
- `conntect {ip}:{port}` 连接指定ip好端口的zookeeper服务
- `close` 关闭连接
- `create {path} {value}` （永久节点） 创建节点path,path为绝对路径/开头 值为value
- `create -e {path} {value}` (临时节点，当连接断开之后，节点消失)
- `ceate -s {path} {value}`创建顺序节点，会在自动在节点名字后添加一串数字，再次使用`create -s`时，该串数字会+1
- `set {path} [version]` 设置节点path的值
- `get {path} [watch]` 查看节path点数据和节点path状态信息，添加watch时，当path节点的值被修改时触发。
- `stat {path} [watch]` 查看节点path的状态信息,添加watchs时，当节点path删除、新增子节点、删除字节点或者修改path的value值时触发watch。
- `ls {path} [watch]`  显示节点路径path下的节点，添加watch 时，当节点path删除，新增子节点，删除字节点时触发watch。
- `ls2 {path} [watch]` 显示节点路径path下的节点以及节点path状态信息，添加watch 时，当节点path删除，新增子节点，删除字节点时触发watch。
- `history` 显示历史使用命令
- `redo cmdno` 重新执行历史命令
- `delete {path} [version]` 删除节点path,该节点必须没有子节点
- `clsoe` 关闭当前与服务端的连接
- `quit`断开当且zk服务
- 

### 集群
1. 集群的搭建
   - 分别在每台服务器的zookeeper的zoo.cfg配置文件中添加
    > server.1=ip:2888:3888
    server.2=ip:2888:3888
    server.3=ip:2888:3888
    server:固定的写法
    1|2|3：节点的id值
    ip：对应服务器的IP地址
    2888：zookeeper的默认心跳端口
    3888：zookeeper的默认数据端口
   - 在每个zoo.cfg中添加事务日志log的保存路劲，如果不添加，默认把事务日志保存在dataDir对应的目录下，日志文件是二进制文件，不能使用vim编辑器查看
    `dataLogDir={path}` 
    查看zookeeper的日志`java -cp /home/data/zookeeper-3.4.14/zookeeper-3.4.14.jar:/home/data/zookeeper-3.4.14/lib/slf4j-api-1.7.25.jar org.apache.zookeeper.server.LogFormatter /home/data/log/zookeeper/version-2/log.100000001` 前面的jar包在zookeeper的安装路径，可以通过修该路径查看指定的日志文件
    - 在每一个服务器的zoo.cfg中dataDir指定的目录下创建一个文件，名为`myid`,然后在新建的文件中写入zoo.cfg中与对应服务器ip地址的节点id值，也就是`server.`拼接的的数字
    - 分别启动服务器的zookeeper服务 `./zkServer.sh start`
集群中的三种角色： leader / follower / observer

### zookeeper在分布式架构中解决的问题


### 启动zookeeper报错
- `org.apache.zookeeper.server.persistence.FileTxnSnapLog$SnapDirContentCheckException:  Snapshot directory has log files. Check if dataLogDir and dataDir configuration is correct.`
- 删除config配置文件中dataDir以及dataLogDir对应目录下的version文件夹

