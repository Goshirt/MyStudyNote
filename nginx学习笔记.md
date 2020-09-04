## 进程

启动nginx时可以启动一个master进程，多个worker进程，worker进程的个数根据配置文件配置

- master进程负责管理worker进程
- woker进程是工作进程
- cache manager 进程负责管理缓存 当nginx开启缓存功能时才会存在
- cache loader 进程负责加载缓存 当nginx开启缓存功能时才会存在

## 格式

```nginx

#配置错误日志的位置以及记录的级别为warn,有debug, info, notice, warn, error, crit 6种级别，默认crit
error_log  /var/log/nginx/error.log warn;
# 配置worker进程的个数为auto,可以是具体的个数，通常根据机器的cpu设定
worker_process atuo;
#使用用户nginx运行
user  nginx;
#配置工作模式以及连接数
events {
    #配置单个worker进程最大连接数为1024，nginx支持得总连接数=worker_processes * worker_connections
    worker_connections  1024;
}
...
http{
    #配置nginx支持哪些多媒体类型
    include       /etc/nginx/mime.types;
    #默认文件类型
    default_type  application/octet-stream; 
    #定义log 日志的格式，格式名为testFormat 格式为
	log_format testFormat '$remote_addr - $remote_user - "http_user_agent"';
	# 配置access_log的位置，并使用名为testFormat的格式记录
	access_log /home/logs/nginx/access.log testFormat;
    # 开启高效文件传输模式
    sendfile	on; 
    #tcp_nopush     on; #开启防止网络阻塞模式
    #gzip  on;  #开启gzip压缩输出
    #长连接超时时间，单位秒
    keepalive_timeout  65; 
    ...
    server{
        ...
        #charset koi8-r;  #配置字符集
        location ...{
        	...
            ...
        }    
    }
    server{
        # 服务监听的端口
        listen 80;
        # 服务名字
        server_name localhost;
        # 指定当前server默认的访问资源,当location中配置有index时，以location的为准
        index index.html;
        # 当访问的时localhost:80/时映射到该location中
        location /{
            # localhost:80/ 相当于访问nginx安装目录下的/test/index.htm
        	root /test;
            # 默认访问的资源为root指定目录下的index.html或index.htm
            index index.html index.htm;
        }
        #error_page  404              /404.html; #配置404页面
        error_page   500 502 503 504  /50x.html;  #配置50x错误页面
        location /bibili{
            # localhost:80/bibili 相当于访问nginx安装目录下的/test2/bibili/index.html
        	root /test2;
            # 默认访问的资源为root指定目录下的index.html或index.htm
            index index.html index.htm;
        }
        location /demo{
            # localhost:80/bibili 相当于访问nginx安装目录下的/test2/index.html
        	alias /test2;
            # 默认访问的资源为root指定目录下的index.html或index.htm
            index index.html index.htm;
        }
    }
}
```

一个http块可以有多个server块，一个server块可以有多个location块，server类似于一个服务，location相当于资源。



## 负载均衡

当多个主机提供同一服务时，通过使用upstream 解决负载均衡。

1. 首先在http节点下添加upstram节点

   ```nginx
   // test为命名,该upsteam会使用默认的轮询的方式进行三个ip的负载均衡
   upstream test {
       #指定的负载均衡算法为通过ip hash
       ip_hash; 
       # weight 可以设置权重
   	server 192.168.42.2:7071 weight=5;
   	server 192.168.42.3:7071 weight=10;
   	server 192.168.42.4:7071 weight=5;
   }
   ```

   

2. 将server节点下的location节点中的proxy_pass配置为：http://+upsteamName 名称即可

   ```nginx
    server{
           # 服务监听的端口
           listen 80;
           # 服务名字
           server_name localhost;
           # 指定当前server默认的访问资源,当location中配置有index时，以location的为准
           index index.html;
           # 当访问的时localhost:80/时反向代理到test中的其中一台主机的nginx安装目录下的/test/index.htm
           location /{
               # localhost:80/ 相当于访问nginx安装目录下的/test/index.htm
           	root /test;
               # 默认访问的资源为root指定目录下的index.html或index.htm
               index index.html index.htm;
           	proxy_pass http://test;
           }
       }
   ```

   

3. 常用的负载均衡算法

   - ip_hash : 通过ip进行hash，达到同一个ip请求转移到相同的服务机中，解决session问题
   - 轮询 : 默认
   - weight : 设置权重，权重高的机器，访问到的概率越高
   - fair(第三方) :   根据页面大小和加载时间长短智能地进行负载均衡，也就是根据后端服务器的响应时间 来分配请求，响应时间短的优先分配。Nginx本身不支持fair，如果需要这种调度算法，则必须安装upstream_fair模块 
   -  url_hash(第三方) ：  访问的URL的哈希结果来分配请求，使每个URL定向到一台后端服务器，可以进一步提高后端缓存服务器的效率。Nginx本身不支持url_hash，如果需要这种调度算法，则必须安装Nginx的hash软件包。 

## 常用的一些指令

- `nginx -v` 查看版本
- `nginx -c {path}`启动nginx时指定主配置文件
- `nginx -t` 测试nginx.conf文件是否有语法错误

## nginx 一些内置变量

- `$remote_addr` 客户端ip

- `$remote_user` 当nginx开启了用户认证功能，此变量记录客户端使用的哪个用户登录

- `$time_local` 当前日志的时间

- `$request` http请求的发，url 和版本协议

- `$status` http请求的响应状态码

- `$body_bytest_sent` nginx响应客户端时发送给客户端的字节数

- `$http_referer` 记录请求从哪个页面过来的

- `$http_user_agent` 请求客户端的软件信息

  如果变量没有值使用 `-` 占位符替代

## nginx实现反向代理

1. 使用docker 启动一个nginx容器,`/home/data/nginx/`下需要有nginx启动的配置文件

   ` docker run -it --name=myNginx -v /home/data/nginx:/etc/nginx  -p 80:80 nginx `

2. 启动一个tomcat

   ` docker run -d  -p 8080:8080 tomcat`

3. 配置nginx，当访问172.16.28.3:80/时反向代理到http://172.17.0.2:8080/index.html

   ```nginx
    server{
           # 服务监听的端口
           listen 80;
           # 配置为nginx容器的网络IP地址
           server_name 172.16.28.3;
           location /{
               # 反向代理到tomcat的地址
           	proxy_pass http://172.17.0.2:8080;
               # 默认访问的资源为root指定目录下的index.html或index.htm
               index index.html index.htm;
           }
   }
   ```

   