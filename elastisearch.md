## elastisearch 安装
- 启动redis不能使用root用户启动，使用`su helmetEs`切换另一个用户，`./elastisearch` 启动elastisearch服务 `-d`参数可以后台启动

### docker方式 安装
1. 下载镜像

   ```shell
   docker pull elasticsearch:7.7.0
   ```

2. 运行，9200为http端口，9300为tcp端口

   ```shell
   docker run --name elasticsearch -d -e ES_JAVA_OPTS="-Xms512m -Xmx512m" -e "discovery.type=single-node" -p 9200:9200 -p 9300:9300 elasticsearch:7.7.0
   ```

3. 进入容器修改配置配置文件，在elasticsearch.yml中添加以下内容：

   ```yml
   http.cors.enabled: true 
   http.cors.allow-origin: "*"	
   ```

4. 重启容器elasticsearch。

   ```shell
   docker restart elasticsearch
   ```

5. 访问http://ip:9200，可以得到界面信息。

6. 下载es 可视化管理工具

   ```shell
   docker pull mobz/elasticsearch-head:5
   ```
7. 启动可视化工具

   ```shell
   docker create --name elasticsearch-head -p 9100:9100 mobz/elasticsearch-head:5
   ```

8. 从容器elasticsearch-head中复制可视化工具的配置文件到宿主机

   ```
   docker cp d2795dc87a3d:/usr/src/app/_site /home/vendor.js
   ```

9. 修改宿主机的js文件,改动两处位置

   - 把`contentType: "application/x-www-form-urlencoded"` 修改为 `contentType: "application/json;charset=UTF-8"`
   - 把`var inspectData = s.contentType =="application/x-www-form-urlencoded" ` 修改为 `var inspectData = s.contentType =="application/json;charset=UTF-8" `

10. 把修改后的js文件重新复制回容器中

   ```
   docker cp /home/vendor.js d2795dc87a3d:/usr/src/app/_site
   ```

11. 重启容器elasticsearch-head

   ```
   docker restart elasticsearch-head
   ```

   

### 常用命令

- `curl 'localhost:9200/_cat/nodes?v'` 获取集群的所有结点信息
- `curl 'localhost:9200/_cat/indices?v'` 获取所有的索引
- 

