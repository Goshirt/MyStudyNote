
/Linux下的根目录是:/
# CentOS命令

## 文件具体信息显示：
  >  第一小块的第1位符号：-代表文件 d代表的是目录 l代表的是连接
  > 第2到4代表的是所拥有者的权限，r是可读，w是可写，x是可执行，-是无
  第5到7代表的是所属组的权限,r是可读，w是可写，x是可执行，-是无
  往后显示的依次是所属者的名字，所属组的名字，大小，创建时间，文件或者目录或者连接名

## 命令

- `pwd`    	显示当前目录
- `cd` 	 	进入root的目录：~
- `cd ../` 	进入上级目录
- `cd ../etc` 进入指定目录
- `clear` 清屏
- `ip addr` 	查看CentOS 的ip，用于shell连接``



### 显示
- `grep` 查找字符串
- `tar -zcvf {name}.tar.gz file`	把file文件打包成name.tar.gz压缩包
- `tar -zxvf fileName`	解压缩.tar.gz/.tgz后缀的压缩包
- `ps`查看进程
- `ps-ef`  查看当前正在进行的进程
- `ps -aux`	查看所有进程
- `ps -ef|grep java`查看当前正在运行的进程名中含有java的进程
- `kill -9`	强行终止进程
- `ls`	显示当前目录下一级的所有目录
- `ls -a` 显示当前目录下一级的所有目录，包括隐藏文件
- `ls /etc/` 显示etc目录下的下一级所有目录
- `ls -l`显示当前目录下的具体信息
- `ls -l /etc/`	显示指定目录下的具体信息
- `ls -l /root/.cshrc` 显示指定目录下指定文件的具体信息
- `ls -lh` 可以人性化显示具体信息
- `ls -li`也是显示具体信息，不过会在头部多一个类似于id好的唯一属性值

### 文件的处理  增删改查
- `mkdir name`在当前目录下创建name目录
- `mkdir -p name/name`在当前目录下级联创建目录
- `rmdir name`删除name目录
- `touch name`在当前目录下创建name文件
- `rm name`	删除当前目录下的name文件（会有提示y/n）
- `rm -f name`	强制删除当前目录下的name文件
- `rm -r name`	删除目录及目录下的文件/删除文件
- `rm -rf name`	强制删除name目录及目录下的文件/强制删除文件
- `cp name /name2/`	复制name文件到name2目录下，如果name是目录会跳过,如果不加文件名，新文件名称不变
- `cp name /name2/name3`复制name文件到name2目录下并重命名为name3
- `cp -r` 复制目录
- `cp -p` 连带文件属性复制
- `cp -d` 若文件是链接文件，则复制链接属性
- `cp -a` 相当于-pdr 剪切/重命名
- `mv  name name2`   源文件目录到目标文件目录，如果name和name2在同一个目录下则是重命名
- `find / -name fileName` 在路径`/`下查找fileName文件所在的路径

### 防火墙常用命令
- `firewall-cmd --zone=pulbic --add-port=3306/tcp --permanent` 开发3306端口
- `systemctl status firewalld` 查看防火墙状态
- `firewall-cmd --list-ports` 查看防火墙开放的端口
- `systemctl start firewalld.service`开启防火墙
- ` firewall-cmd --reload` 配置立即生效
- `systemctl stop firewalld.service` 关闭

### centos中Tomcat输出控制台
```
#进入tomcat安装目录下的logs文件夹 动态打印日志信息
tail -f catalina.out 
```

### vim编辑器的常用命令
- `/` 查找命令
- `i`输入命令
-  `n`查找模式下的 下一个
-  `m`查找模式下的 上一个
-  `wq!`强制保存并退出
-  `:`末行输入模式
-  `s/s1/s2/g`在末行模式下，全局将字符串s1替换为s2

### 查找机器信息常用命令
- `free -m` `free -h` `free ` 显示内存使用
- `cat /proc/meninfo` 读出内核的信息并显示
- `cat /proc/cpuinfo` 读出cpu的信息并显示
- `df -h` 显示硬盘的使用信息
- `du -sh /*` 显示每个目录使用磁盘容量
- `top` 查看内存占用情况 在top命令运行的情况下，可以按f键选择其他需要显示的信息，方向盘的上下键进行选择，空格键选中或者取消，s键确定排序的字段，q可以退出
- ` netstat -tunlp|grep 端口` 查看端口占用 ，也可以安装 lsof  `yum install lsof`,然后使用共`lsof -i:端口`
 ### buff/cache 缓存过高时可以手动清楚缓存 
- `echo 1 > /proc/sys/vm/drop_caches`  清除pagecache
- `echo 2 > /proc/sys/vm/drop_caches` 清除回收slab分配器中的对象（包括目录项缓存和inode缓存）
- `echo 3 > /proc/sys/vm/drop_caches`清除pagecache和slab分配器中的缓存对象
###  传输命令
- `scp [-r]|[-P] /home/data/test.txt 192.168.2.2:/home/data/` 把本机、home/data/test.txt 文件传输到192.168.2.2 的/home/data/ 目录下，如果传输的目录，则需要加上`-r` ，要指定端口需要加上`-P port`
### 环境变量
- `source /etc/profile` 使修改过的环境变量生效
### 用户操作
- `su userName` 切换到userName用户
- `useradd userName` 添加userName 用户
- `passwd userName` 添加userName密码	
- `cat /etc/passwd | grep -v /sbin/nologin | cut -d : -f 1` 查看当前可以登录的用户
- `/etc/hostname `在该文件下可以修改主机名

## 常见问题解决  

1.  centos 文件夹，文件 名称乱码解决办法
```
#安装convmv    
yum install convmv
#批量递归修改当前目录下的文件名格式    
convmv -f GBK -t UTF-8 -r --notest ./*
```

2. 安装mysql后登录mysql 无法登录时
```
vi /etc/my.cnf/ 
#在文件末尾添加  
skip-grant-tables 
#保存退出
#使用root用户登录  
mysql -u root 
#进入mysql
use mysql
update user set authentication_string = password("123456") where user="root";
flush privileges;
exit
#修改my.文件 
vi /etc/my.cnf/  
#删除前面添加的
skip-grant-tables 
保存退出
#利用刚刚设置的密码登录
mysql -u root -p 后输入密码
#修改密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'xxx';
#允许远程登录,BY后面的密码改为自己root用户的实际密码
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'xxx' WITH GRANT OPTION; 
#开放3306端口
firewall-cmd --zone=public --add-port=3306/tcp --permanent 
#如果报错FirewallD is not running 检查防火墙状态以及开放的端口
#修改mysql默认编码，进入配置文件 
vi /etc/my.cnf/
#在Remove leading  前面添加两行内容：
character_set_server=utf8
init_connect='SET NAMES utf8' 
#保存并退出
#重启mysql   
systemctl restart mysqld
#然后进入mysql 检查一下编码，检查编码语句
show variables like '%character%'; 
#全显示为utf-8成功
```

## centos中Tomcat输出控制台
	进入tomcat安装目录下的logs文件夹 动态打印日志信息
	tail -f catalina.out 
	启动Tomcat
	安装目录/bin/startup.sh

 ## buff/cache 缓存过高时可以手动清楚缓存 
- `echo 1 > /proc/sys/vm/drop_caches`  清除pagecache
- `echo 2 > /proc/sys/vm/drop_caches` 清除回收slab分配器中的对象（包括目录项缓存和inode缓存）
- `echo 3 > /proc/sys/vm/drop_caches`清除pagecache和slab分配器中的缓存对象



### 克隆虚拟机出现的问题

- ` systemctl restart network` 时出现 `failed to start lsb: bring up/down networking`的错误解决办法

  ```
  # vim /etc/sysconfig/network-scripts/ifcfg-enoxxxx      这里的ifcfg-enoxxxx后面的xxxx根据不同机器配置不一样
  在网络配置文件中加入Mac地址和IP地址：
  BOOTPROTO=static   # 这个的值改为static
  HWADDR=00:0C:29:90:83:04  # 这里填写虚拟机的真实Mac地址
  IPADDR=192.168.134.128  #这里填写虚拟机的可用IP地址
  NETMASK=255.255.255.0
  GATEWAY=192.168.134.0
  
  重启机器 reboot
  
  重启后执行如下命令，不用管是干啥用的，简单粗暴：
  systemctl enable NetworkManager-wait-online.service
  systemctl restart network
  systemctl stop NetworkManager
  systemctl disable NetworkManager
  systemctl restart network 
  
  ```

  

## lrzsz 工具

 - `yum install -y lrzsz`安装
 - `sz filename` 可以从服务端下载指定文件名的文件到本地
 - `rz`选择指定的文件上传到服务器