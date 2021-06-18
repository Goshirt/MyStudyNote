#### 安装

1. 拉取镜像

   ```
   docker pull jenkins:2.60.2
   ```

   

2. 启动

   ```
   docker run -u root -d --name jenkins-2 --restart=always -p 8081:8080 -p 50001:50000 -v /home/jenkins-volume-2/:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock jenkinsci/blueocean
   ```

   

3. 进入宿主机的jenkins 挂载地址后找到`hudson.model.UpdateCenter.xml`，然后 `vi hudson.model.UpdateCenter.xml` ,把镜像地址更改为华为云地址，或者其他的国内地址，保存退出并重启容器。例如：`http://mirror.esuni.jp/jenkins/updates/update-center.json` 也可以

   ![1601543818936](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1601543818936.png)

4. 访问 http://192.168.42.4:8081 ，根据提示获取管理员密码进行登录，在挂载到宿主机的目录下可以找到secrets 文件夹

   ![1601433570950](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1601433570950.png)

5. 插件安装选择推荐安装

   ![1601433751717](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1601433751717.png)

6. 安装一些插件，可以使用推荐安装，或者自定义安装。