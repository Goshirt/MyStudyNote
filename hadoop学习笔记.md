# hadoop 
## hdfs 
#### 组成
- 一个hdfs系统由一台运行namenode(负责管理记录块信息的角色)的服务器和多台运行datanode（存储块信息的角色）的服务器组成
#### namenode 工作原理
namenode的存储的数据叫做元数据，包含hdfs的目录结构以及每一个文件块的信息（块的id,块的副本数量，块的存储位置）
**checkpoint过程**：

1. namenode会把实时的完整数据存储在内存当中，同时在磁盘上存储内存元数据在某个时间节点的fsimage镜像文件。磁盘的存储路径在配置文件hdfs-site.xml的`dfs.namenode.name.dir`可以进行配置。
2. namenode会把每一次引起元数据变化的操作记录在edits日志文件中。
3. 如果启动了secondarynamenode，那么secondarynamenode会在第一次checkpoint的时候从namenode上下载fsimage镜像和定期的从namenode下载新生成的edits日志，然后把下载好的fsimage镜像加载到内存中，并解析edits日志，对内存中的元素数据进行整合，整合完成后生成一个新的fsimages镜像，最后把这个镜像上传到namenode中。

#### 启动
- `hadoop-daemon.sh start namenode` 在部署好环境的集群中任意一台机器上运行该命令，运行此命令的机器将作为namenode节点启动。可通过`jps`命令检查是否启动成功

- `hadoop-daemon.sh start datanode`在部署好环境的集群中任意多个机器上运行该命令，运行此命令的机器将作为datanode节点启动（namenode可以与datanode共存一个机器上）。

- 批量启动hdfs：

  1. 在namenode所在机器上配置该机器到集群所有机器的ssh免密登录。

  2. 执行一次`ssh 0.0.0.0`。

  3. 修改hadoop按照目录中的/etc/hadoop/slaves,把需要的启动的datanode节点添加进去

     > hdfs-test1
     >
     > hdfs-test2
     >
     > hdfs-test3
     >
     > hdfs-test4
  4. 在namenode所在节点执行`start-dfs.sh`启动整个集群的namenode以及datanode，`stop-dfs.sh`关闭整个集群。 
#### 配置文件hdfs-site.xml
- `dfs.blocksize ` 设置切片的大小，默认128M.
- `dfs.replication` 设置副本的数量
- `dfs.namenode.name.dir` hdfs中nade node数据存储的位置
- `dfs.datanode.data.dir` hdfs中data node数据存储的位置
- `dfs.namenode.secondary.http-address` secondary namenode启动的位置
- `dfs.namenode.checkpoint.dir` secondary namenode保存数据的目录
#### 文件写入的流程

![文件写入hdfs流程](D:\file\study_note\MyStudyNote\img\hadoop\hdfs写入流程.jpg)

#### 读取数据流程

![hdfs读取流程](D:\file\study_note\MyStudyNote\img\hadoop\hdfs读取流程.jpg)

#### 常用命令

- `hadoop fs -ls path`显示hdfs中的指定路径的文件信息，根路径为`/`
- `hadoop fs -put localpath fspath`把本地路径下的文件复制一分到hdfs中，等价于`hadoop fs -copyFromLocal`
- `hadoop fs -moveFromLocal localpath fspath` 把本地的文件剪切到hdfs中
- `hadoop fs -get fspath localpath`下载hdfs中指定路径的文件到本地，等价于`hadoop fs -copyToLocal`
- `hadoop fs -moveToLocal fspath localpath` 截切hdfs中指定路径的文件到本地
- `hadoop fs -mkdir -p path` 在hdfs中创建目录
- `hadoop fs -mv sourcepath destpath` 移动文件
- `hadoop fs -rm -r path`删除文件
- `hadoop fs -appendToFile  localpath hdfspath` 把本地的文件追加到hdfs文件中
- `hadoop fs -cat hdfspath` 显示hdfs文件的内容



## MapReduce

#### MapReduce 的流程

- map task 的数量由文件的 文件数、文件大小、块大小、以及split大小有关 ，一个map task处理一个切块，默认切开大小为128M。

- reduce task 的运行数量由partitions分区决定，当在客户端定义的reduce task数量大于partitions分区的数量时，实际参与运行的reduce task数量为partitions分区的数量。

  

1. 首先job划分输入切片。job客户端会根据输入目录进行扫描，遍历每个文件的大小，然后根据配置文件的块大小进行划分。
2. 根据划分的切片产生对应数量的map task。
3. 每个map task读取自己的split切片。 通过TextInputFormat 生成一个LineRecordReader，反复调用next() ，一行一行读取文件内容，每一行产生一对kv，k是行的起始偏移量，v是行的内容。
4. WcMapper的map(k,v,context)把kv键值对输出到MapOutputCollector(环形缓冲区，100M)。
5. Spiller读取环形缓冲区的kv对，首先按照Partitioner的getPartition（）进行分区，再按照key的compareTo进行排序输出到本地文件中
6. 如果需要可以自定义个Combiner进行局部聚合，形成新的文件
7. reduce task 通过http下载不同map task产生的同一个区号的文件进行合并排序，然后通过LineRecordWriter的wirter方法写入到HDFS中。



## Hive

   Hive通过sql从hdfs中获取文件，转化为mapreduce执行查询，只能执行查询操作，速度偏慢，不适用于实时查询。

## Hbase

   NoSql数据库，持久化存在HDFS中，支持增删改查，分布式的数据库系统。

#### 工作原理



   1. region server 中有一个内存区域存放热数据，所有改变表结构的操作都会同时在hdfs中记录log日志，即使region server 挂掉也可以通过日志恢复。