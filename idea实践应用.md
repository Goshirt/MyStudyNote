## 配置docker部署

### 安装

1. 通过plugin 搜索`Docker` 进行安装

2. 配置docker ,运行远程连接,修改[Service]下的ExecStart配置

   ```shell
   vi /lib/systemd/system/docker.service
   
   ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock -H fd:// --containerd=/run/containerd/containerd.sock
   ```

3. 重启docker 服务

   ```shell
   service docker restart
   ```

4. 查看网络防火墙是否关闭或者开启端口2375

5. 配置idea的docker 连接

   ![](D:\file\study_note\MyStudyNote\img\idea\0001.png)

6. 编写docker file

   ```dockerfile
   # 基础镜像
   FROM anapsix/alpine-java:8_server-jre_unlimited
   # 维护者信息
   MAINTAINER 13672875006@163.com
   # 定义镜像的时间格式及时区
   RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
   # 创建一个目录
   RUN mkdir -p /home/demo
   # 指定工作目录
   WORKDIR /home/demo
   # 目录就会在运行时自动挂载为匿名卷，任何向/home/demo/logs中写入的信息都不会记录进容器存储层，从而保证了容器存储层的无状态化，可通过docker run -d -v mydata:/home/demo/logs xxxx 映射到宿主机中
   VOLUME /home/demo/logs
   # 镜像暴露的端口
   EXPOSE 8081
   # 把文件添加到镜像中
   ADD ./target/helmet-module-demo-2.3.0.jar ./
   # 启动jar包
   CMD sleep 60;java -Djava.security.egd=file:/dev/./urandom -jar helmet-module-demo-2.3.0.jar
   ```

7. ![](D:\file\study_note\MyStudyNote\img\idea\0002.png)

