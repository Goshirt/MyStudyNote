## centos安装docker及常用命令
- `sudo yum install docker` 安装
- `sudo service docker start` 启动docker服务
- `sudo chkconfig docker on` 设置docker开机启动
- `docker images` 列出本地的docker镜像
- `docker rmi imgId`删除指定的imgId的本地镜像，如果该镜像有被容器使用中，会删除失败，根据提示的容器id,先删除指定的容器
- `docker rm containerId` 删除指定containerId的容器
- `docker pull imgName` 在默认的仓库中拉取imgName的镜像到本地
- `docker pull url/imaName` 在url的服务器上拉取imgName的镜像到本地
 
