## centos安装docker及常用命令
- `yum install docker` 安装
- `service docker start` 启动docker服务
- `service docker stop` 关闭docker服务
- `service docker restart` 重启docker服务
- `sudo chkconfig docker on` 设置docker开机启动
- `docker images` 列出本地的docker镜像
- `docker rmi imgId`删除${imgId}的本地镜像，如果该镜像有被容器使用中，会删除失败，根据提示的容器id,先删除指定的容器 ，`-f` 强制删除
- `docker rm containerId` 删除${containerId}的容器 `-f`强制删除
- `docker ps` 正在运行的容器，`-a`列出所有容器的详细信息 `-q` 只列出容器Id
- `docker rm ${docker ps -a -q}`删除所有的容器
- `docker start containerId` 启动${containerName}容器
- `docker stop cotainerId` 停止${contianerName} 容器
- `docker stop ${docker ps -a -q}`停止所有的容器
- `systemctl daemon-reload` 重启守护进程
- `docker pull imgName：version` 在默认的仓库中拉取{imgName:version}的镜像到本地
- `docker pull url/imaName` 在url的服务器上拉取imgName的镜像到本地
- `docker inspect containerId` 查看${containerId}的详细信息


##配置docker镜像的阿里云加速地址
- 在`/etc/docker/daemon.json`(没有该文件时新建一个)添加下边的代码(url为自己在阿里云控制台容器镜像服务获取到的url)：
  -> `{
  "registry-mirrors": ["https://XXXX.mirror.aliyuncs.com"]
}`
