## kafka 安装及基本使用我
1. 通过wget命令安装
`wget http://archive.apache.org/dist/kafka/1.0.0/kafka_2.11-1.0.0.tgz`
2. 解压文件
`tar -xzcf kafka_2.11-1.0.0.tgz`
3. kafka 使用的是zookeeper,所以先要启动一个zookeeper,直接在解压文件中可以启动单节点的zookeeper
`bin/zookeeper-server-start.sh config/zookeeper.properties`
4. 启动kafka服务
`bin/Kafka-server-start.sh config/server.properties`
