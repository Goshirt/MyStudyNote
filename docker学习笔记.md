

## centos安装docker及常用命令
#### 安装步骤
1. `yum install -y yum-utils device-mapper-persistent-data lvm2` 首先安装依赖环境
2. `yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo` 设置稳定的存储库
3. `yum install docker-ce docker-ce-cli containerd.io` 安装
#### 常用命令
- `yum list docker-ce --showduplicates | sort -r` 列出存储库中可用的docker版本，从高到底
- `service docker start` 启动docker服务
- `service docker stop` 关闭docker服务
- `service docker restart` 重启docker服务
- `sudo chkconfig docker on` 设置docker开机启动
- `docker run --name containerName -d imageName|imageId [command]` 指定新的容器的名字并且以后台运行的方式运行指定镜像名或者镜像id的镜像,`-it`以交互的方式运行
- `docker images` 列出本地的docker镜像
- `docker rmi imgId`删除${imgId}的本地镜像，如果该镜像有被容器使用中，会删除失败，根据提示的容器id,先删除指定的容器 ，`-f` 强制删除
- `docker rm containerId` 删除${containerId}的容器 `-f`强制删除
- `docker ps` 正在运行的容器，`-a`列出所有容器的详细信息 `-q` 只列出容器Id
- `docker rm ${docker ps -a -q}`删除所有的容器
- `docker rm ${docker ps -aq -f status=exited}`删除所有已经退出的容器
- `docker start containerId` 启动${containerName}容器
- `docker stop cotainerId` 停止${contianerName} 容器
- `docker stop ${docker ps -a -q}`停止所有的容器
- `docker pause containerId`暂停容器，让出cpu,知道遇见`unpause`
- `docker unpause containerId`回复运行容器
- `systemctl daemon-reload` 重启守护进程
- `docker pull imgName：version` 在默认的仓库中拉取{imgName:version}的镜像到本地
- `docker pull url/imaName` 在url的服务器上拉取imgName的镜像到本地
- `docker inspect containerId` 查看${containerId}的详细信息
- `docker history imageName|imageId` 查看指定镜像名或者镜像id的构建历史（dockerfile的执行过程）
- `docker attach containerId `进入指定容器id的容器中，`ctrl+p+q`退出容器终端 
- `docker exec -it containerId bash|sh` 以交互的模式一个bash终端进入指定的容器中
- `docker run -d --restart=always containerId` 启动指定的容器，并且无论容器因何种原因退出，都立即自动重启，
- `docker run -d --restart=on-failure:3`启动指定的容器，如果容器是非正常退出，则重启容器，最多重启3次
## docker的配置文件
##### 配置docker可以远程访问
## 镜像 image
#### 运行镜像
镜像的运行是在原有的镜像基础上添加一层容器层，对镜像的增删改查操作都只是记录在容器层中，这样就保证多个镜像共享基础镜像而互不干扰，修改的时候使用的是copy-on-write的特性，先把修改的文件从上往下找，找到第一个直接复制到容器层进行修改
- `docker run -it iamgeid`通过命令行交互的方式运行镜像 `-m`设置容器使用的内存，`--memory-swap`设置内存+swap的和使用限额，如果不指定`--memory-swap`则默认为`-m`的两倍，`-vm 1`启动一个内存工作现程，`--vm-bytes 280M`每一个线程分配280M内存，如果`--vm-bytes`指定的内存超过`--memory-swap`指定的内存，则启动失败，`-c或者--cpu-shares`可以指定cpu分配的数量
- `docker run -m 200M --memory-swap=300M -it iamgeid`最多运行使用200M的内存和100M的swap。
- `docker run -it -m 200M --memory-swap=300M progrium/stress --vm 1 --vm-bytes 280M`启动一个内存工作线程，每个线程分配280M内存，过程为分陪280M，释放280M,分配280M，释放280M,循环下去，可用于容器 压测。
- `docker run -it --name centos_ioa --blkio-weight 600 imageid` 启动容器的block io 权重为600，more为500。
- `docker run -it --device-write-bps /dev/sda:30MB imageId` 限制容器io每秒写的速率为30M,`/dev/sda`为容器文件系统在host宿主机的位置，`--device-read-bps`每秒读速率，` --device-write-bps`每秒写速率，`--device-read-iops`每秒读的io次数，`--device-write-iops`每秒写的io次数，可以使用`time dd`进行测试。eg:`time dd if=/dev/zero of=test.out bs=1M count=800 oflag=direct`
#### 构建镜像
1. 通过`docker commit containername newImageName` 将当前容器构建成一个指定命名的新镜像
2. `docker build -t newImageName .` 通过当前目录下的名为Dockerfile的文件构建一个指定命名的新镜像 `-t`:指定新镜像的名字    `.`: 指定Dockerfile的文件位置为当前目录   `-f`:指定Dockerfile 的文件位置 
#### 镜像的缓存特性
docker会缓存已有的镜像层，构建新镜像时，如果某镜像已经存在，就直接使用，在`docker build`命令后加上`--no-cache`参数可以使构建镜像的时候不使用缓存
## dockerfile
#### dockerfile执行过程
dockerfile中的每一条指令都会创建一个镜像层，上层依赖下层，只要某一层发生改变，其上面所有层缓存都会失效，也就是只要改变Dockerfile的执行顺序或者修改添加指令，也会使得镜像缓存失效。eg:
 
Dockerfile one
> FROM centos
    RUN yum install -y wget

Dockerfile two
  > FROM centos
    RUN yum install -y wget
    COPY testfile /

Dockerfile three 
  > FROM centos
    copy testfile /
    RUN yum install -y wget
    
在执行Dockerfile one 的时候会存在一个执行完`RUN yum install -y wget`的镜像层，当执行Dockerfile two的时候，会使用Dockerfile one时的缓存镜像层，只是在该缓存层中添加多一层`COPY testfile /` 的镜像层，但是当执行Dockerfile three 时，由于顺序的变化，Dockerfile one 的缓存将失效，将会重新的在centos镜像层中一层一层的添加。
#### dockerfile常用指令
- `FROM {imageName | imageid}` 指定base镜像
- `MAINTAINER` 指定镜像的作者，可以是任意字符串
- `COPY` 将文件从build context 复制到镜像中，build context: 运行`docker build`时指定Dockerfile的路径
- `ADD` 与`COPY`相似
- `ENV` 设置环境变量，环境变量可以被后面的指令使用
- `EXPOSE` 指定容器进程监听的端口
- `VOLUME` 将文件或目录声明为volume
- `WORKDIR` 为后面RUN,CMD,ENTRYPOINT,ADD,COPY 指定镜像中的当前工作目录
- `RUN`在容器中运行指定的命令
- `CMD` 容器启动时指定的命令，可以有多个`CMD`命令，但是只有最后一个会生效
- `ENTRYPOINT` 设置容器启动时运行的命令
#### RUN CMD ENTRYPOINT三个命令的区别
 1. RUN：通常用于安装应用和软件包，在当前镜像的顶部执行命令，并创建新的镜像层，一个Dockerfile中可以包含多个RUN指令，有两种书写格式：
      - Shell格式： RUN    eg: `RUN yum install -y httpd`
      - Exec格式： RUN["executable","param1","param2"]
 2. CMD:允许用户指定容器的默认执行的命令，此命令只有当容器启动且docker run 没有指定其他命令运行时运行，当docker run 指定了其他命令，CMD指定的指令将被忽略，如果Dockerfile 中有多个CMD指令，只有最后一个生效。有三种书写格式：
      - Exec格式：CMD["executable","param1","param2"] (推荐格式)
      - CMD["param1","param2"] 为ENTRYPOINT提供额外的参数，此时ENTRYPOINT必须使用Exec格式
      - Shell格式： CMD command param1 param2
  3. ENTRYPOINT: 让容器以应用程序或者服务的形式运行，与CMD很相似，但是ENTRYPOINT不会被忽略，一定会执行，即使运行docker run 时指定了其他命令，而且当使用Exec格式时，还可以通过CMD命令提供额外的参数，有两种格式：
      - Exec格式：ENTRYPOINT ["executable","param1","param2"] (推荐格式) eg: `ENTRYPOINT ["/bin/echo","hello"] CMD ["world"]` 当通过docker run -it 启动容器时会输出: hello word
      - Shell格式：ENTRYPOINT command param1 param2
## 容器底层技术 cgroup 和 namespace
##配置docker镜像加速地址
1. 在`/etc/docker/daemon.json`(没有该文件时新建一个)添加下边的代码(url为自己在阿里云控制台容器镜像服务获取到的url)：
   `{
  "registry-mirrors": ["https://XXXX.mirror.aliyuncs.com"]
}`
2.配置daocloud镜像加速地址（在daocloud.io注册一个账号，获取地址），执行命令
`curl -sSL https://get.daocloud.io/daotools/set_mirror.sh | sh -s http://f1361db2.m.daocloud.io`
1. 然后重启daemon `systemctl daemon-reload`
