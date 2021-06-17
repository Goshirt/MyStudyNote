## elastisearch 安装
- 启动redis不能使用root用户启动，使用`su helmetEs`切换另一个用户，`./elastisearch` 启动elastisearch服务 `-d`参数可以后台启动



### 常用命令

- `curl 'localhost:9200/_cat/nodes?v'` 获取集群的所有结点信息
- `curl 'localhost:9200/_cat/indices?v'` 获取所有的索引
- ``