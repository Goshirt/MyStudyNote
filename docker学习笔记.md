

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
- `docker rm $(docker ps -a -q)`删除所有的容器
- `docker rm $(docker ps -aq -f status=exited)`删除所有已经退出的容器
- `docker start containerId` 启动${containerName}容器
- `docker stop cotainerId` 停止${contianerName} 容器
- `docker stop $(docker ps -a -q)`停止所有的容器
- `docker pause containerId`暂停容器，让出cpu,直到遇见`unpause`
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
## 镜像 image
#### 运行镜像
镜像的运行是在原有的镜像基础上添加一层容器层，对镜像的增删改查操作都只是记录在容器层中，这样就保证多个镜像共享基础镜像而互不干扰，修改的时候使用的是copy-on-write的特性，先把修改的文件从上往下找，找到第一个直接复制到容器层进行修改
- `docker run -it iamgeid`通过命令行交互的方式运行镜像 `-m`设置容器使用的内存，`--memory-swap`设置内存+swap的和使用限额，如果不指定`--memory-swap`则默认为`-m`的两倍，`-vm 1`启动一个内存工作现程，`--vm-bytes 280M`每一个线程分配280M内存，如果`--vm-bytes`指定的内存超过`--memory-swap`指定的内存，则启动失败，`-c或者--cpu-shares`可以指定cpu分配的数量，`-p`可以指定端口的映射，`--ip`可以指定容器的静态ip,但是必须是使用`--subnet`创建的网络才可以指定容器的静态ip
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
##### RUN CMD ENTRYPOINT三个命令的区别
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
## 容器的网络
`docker network ls`可以查看到docker的网络情况，默认安装了三种网络,在创建容器是可以通过`--network={type}`指定使用的网络，此外还可以自己创建user-defind网络,同一网络下的容器可以相互通信，不同网络下的容器通信时需要额外的设置，例如`docker network connect {networkName} {containerId}`将现有容器加入到指定的网络中
1. none:什么网络也没有，挂在该网络下的容器除了lo,不存在任何网卡，这样封闭的网络环境使用于随机密码生成
2. host：共享宿主机的网络栈，网络配置与host完全一样，最大的好处就是性能好，但灵活性不好，docker host 上已经使用的端口不能再使用。
3. bridge：docker安装的时候会创建一个命名为docker0的linux bridge,如果不指定 `--network`创建的容器默认会挂载到docker0上，创建出来的容器的ip从`172.17.0.0/16`中自动分配,并且网关为`172.17.0.1`,可以通过`docker network inspect bridge` 查看。
4. user-defined:提供三种种网络驱动：bridge/overlay/macvlan,overlay和maclan用于创建跨主机的网络。
   - bridge驱动： `docker network create --driver bridge my_net`可以创建一个类似默认的bridge类型的网络，然后通过`brctl show`可以查看到宿主机多了一个新创建的bridge,可以在创建的时候加上`--subnet 172.22.16.0/24` 和 `--gateway 172.22.16.1`指定ip网段和网关

##### docker容器与外部环境网络访问
1. docker容器默认就可以访问外部的网络
2. 外部访问docker容器时，容器的宿主机默认会创建一个docker-proxy进程处理外部的访问，docker-proxy会监听容器绑定到宿主机的的端口，当外部访问宿主机的该端口时，docker-proxy就会把访问转发给容器。

## 容器的数据保存
1. 保存在镜像中,例如在容器安装的软件，应用
2. 保存在data volume，volume其实是宿主机文件系统的一部分，容量取决于当前宿主机未使用的空间，有两种类型的volume:`bind mount` 和 `docker managed`。例如容器运行产生的数据
     - `bind mount`:将宿主机中已存在的目录或者文件mount到容器中 可以在run启动容器是使用`-v {hosPath}:{containerPath}:ro`(`eg: -v /home/data/python:/home`)把宿主机的目录挂载到容器中，当容器删除时，宿主机的目录依旧存在,`:ro`指定为只读，默认为可读可写，只读时容器就无权更改数据，提高安全性。当hostPath 在宿主机不存在时，会作为一个新目录挂载在容器中。
     - `docker managed`:不需要指定`hostPath` 只需要在启动时通过`-v {containerPath}`就可以，启动容器后可以通过`docker inspect`查看data volume的位置，在Mounts中指出
     - 两者的区别：
        |   不同点 |   bind mount  |   docker managed  |
        |:------- |:----------|:----------- |
        |volume的位置 | 可任意指定| /var/lib/docker/volumes/..|
        |对已有mount point 的影响 | 隐藏并替换volume | 原有数据复制到volumes
        |对单文件的指出 | 支持 | 不支持|
        |权限控制 | 可设置可读，默认为可读可写 | 无法控制，均为可读可写|
        |移植性 | 移植性弱，与host path 绑定 | 移植性强，无须指定host目录|

### 配置docker镜像加速地址

 1. 在`/etc/docker/daemon.json`(没有该文件时新建一个)添加下边的代码(url为自己在阿里云控制台容器镜像服务获取到的url)：
      `{
    "registry-mirrors": ["https://XXXX.mirror.aliyuncs.com"]
}`
 2. 配置daocloud镜像加速地址（在daocloud.io注册一个账号，获取地址），执行命令
`curl -sSL https://get.daocloud.io/daotools/set_mirror.sh | sh -s http://f1361db2.m.daocloud.io`

 3. 然后重启daemon `systemctl daemon-reload`

### 安装vi命令

 1. `apt-get update`

 2. `apt-get install vim`

### docker实战redis
##### 主从配置

1. 下载一个redis镜像

    `docker pull redis`

2. 在home目录下准备三个文件夹 one,two,three 用来挂载三个redis容器的目录，并且在每一个文件夹中都存放一个redis.conf配置文件。

3. 在每一个redis.conf中做以下改变

   -  Qno` 改为`daemonize yes`

   - 注释掉`bind 127.0.0.1`
   
   - `protected-mode yes` 改为`protected-mode no`
   
   - 取消`requiredpass foobared` 的注释，并将`foobared` 改成自己的密码口令
   
   - 修改log的文件路径    
   
4. 使用下载好的redis镜像，启动三个redis容器,三个redis分别映射宿主机的6379，6380，6381三个端口。redis-one 将作为maste，redis-two和reids-three作为slave
   
    - `docker run --name reids-one -p 6379:6379 -v /home/one：/data -v /home/one/reids.conf:/data/redis.conf {imageId}`
    
    - `docker run --name reids-two -p 6380:6379 -v /home/two：/data -v /home/two/reids.conf:/data/redis.conf {imageId}`
    
    - `docker run --name reids-three-p 6381:6379 -v /home/three：/data -v /home/three/reids.conf:/data/redis.conf {imageId}`ip

5. 使用`docker ps` 检查三个redis容器是否启动成功 。

6. 使用`docker inspect redis-one` 查看redis-one在容器网络中的主机

7. 在代表从库的/home/two,/home/three文件夹下的配置文件redis.conf中加以下配置
   
    - `masterauth {password}` password为主master（redis-one的配置文件中设置的密码）的登录密码。（代表主库master的redis.conf也需要加上,否则加入哨兵机制，当master意外挂掉，然后重启变为slave的时候，会由于没有新master的连接密码导致无法从新的master中同步数据）
    
    - `slaveof {ip} {port}` {ip}为主master（也就是第五步查看到的redis-one在容器网络中的ip）的主机ip ,port为6379。
    
8. 从起reids-two 和 redis-threee 容器 `docker restart redis-two`,`docker restart redis three

9. 用`docker exec -it reids-one bash`进入redis-one容器中，然后输入`redis-cli`启动redis客户端，通过`info replication`可以查看到slave的个数。

##### 哨兵模式（在上边完成一主二从的基础上增加三个哨兵）
1. 在宿主机/home/redis-volume/ 下新建三个目录作为哨兵容器的挂载目录
   - `mkdir /home/redis-volume/sentinel-1`
   - `mkdir /home/redis-volume/sentinel-2`
   - `mkdir /home/redis-volume/sentinel-3` 
2. 在redis的安装目录下找到sentinel.conf ,做以下修改
   - `daemonize no`改为 `daemonize yes`
   - `logfile "/data/sentinel.log"` 该目录为容器中的目录，所以需要启动哨兵容器之后在哨兵容器中对应的目录下新建该sentinel.log。
   - `sentinel monitor mymaster 172.17.0.3 6379 2` ip为前边一主二从时master的容器网络中的ip。
   - `sentinel auth-pass mymaster {password}`password为一主二从中配置的master密码。
3. 把该文件分别复制到第一步新建的三个目录中。
4. 启动三个容器作为哨兵，分别映射到主机的23679，26380，26381三个端口，把第一步新建的三个目录挂载到容器中。
   - `docker run --name sentinel-1 -p 26379:26379 -v /home/redis-volume/sentinel-2:/data -d redis`
   - `docker run --name sentinel-2 -p 263880:26379 -v /home/redis-volume/sentinel-2:/data -d redis`
   - `docker run --name sentinel-3 -p 26381:26379 -v /home/redis-volume/sentinel-3:/data -d redis`
5. 进去其中一个容器sentinel-1
  - `docker exec -it sentinel-1 bash`
6. 默认进入的`data`目录，在该目录下新建一个文件`sentinel.log`。
  - `touch sentinel.log`
7. 启动哨兵
  - `redis-sentinel sentinel.conf`由于在第三步的时候把宿主机的目录挂载到容器中了，所以在容器的`data` 目录下会有`sentinel.conf`文件。
8. 通过查看sentinel.log，可以知道哨兵成功的加入集群中。
9. 重复4-8步骤，启动另外两个哨兵。
10. 验证，关闭前面的maste主机，发现其中的一个slave成功的转换为master。

### docker 实战mysql

#### 安装mysql

1. 拉取自己需要的mysql 版本镜像
	`docker pull mysql:5.7`
	
2. 使用镜像启动应给mysql容器 , 并指定mysql的端口映射以及root的初始密码
	`docker run -p 3306:3306 --name mysql-test -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7`
	
3. 进入mysql容器中
    `docker exec -it mysql-test bash`
    
4. 使用第二步指定的root用户的密码进入容器中的mysql 客户端。
   `mysql -u root -p 123456`
   
5. 修改root用户的远程访问权限
   `GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;`
   
   `flush privileges;`
   
6. 重启mysql容器
   `docker start mysql-test`

### docker 实战zookeeper
1. 拉取镜像
   `docker pull zookeeper`
2. 