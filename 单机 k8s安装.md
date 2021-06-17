## 单机 k8s安装

1. 关闭centos防火墙

   ```
   systemctl disable firewalld
   systemctl stop firewalld
   ```

2. 安装etcd 以及 kubernetes (会自动安装docker),如果安装失败，可能是本机已有的docker版本冲突，可以卸载本机已有的docker

   ```
   yum install -y etcd kubernetes
   ```

3. 配置及Docker的配置文件

   -  /etc/sysconfig/docker 的OPTIONS内容设置为

     ```
     OPTIONS='--selinux-enabled=false --insecure-registry gcr.io'
     ```

   - /etc/kubernetes/apiserver 中 --admission_control 参数中的ServiceAccount删除

     ```
     KUBE_ADMISSION_CONTROL="--admission-control=NamespaceLifecycle,NamespaceExists,LimitRanger,SecurityContextDeny,ResourceQuota"
     ```

   - 

4. 按顺序启动所有的服务

   ```
   systemctl start etcd
   systemctl start docker
   systemctl start kube-apiserver
   systemctl start kube-controller-manager
   systemctl start kube-scheduler
   systemctl start kubelet
   systemctl start kube-proxy
   ```

5. 设置开机启动

   ```
   systemctl enable etcd
   systemctl enable docker
   systemctl enable kube-apiserver
   systemctl enable kube-controller-manager
   systemctl enable kube-scheduler
   systemctl enable kubelet
   systemctl enable kube-proxy
   ```
   
   
   
6. 通过定义一个yaml文件，启动一个rc ,新建一个mysql-rc.yaml 文件，写入一下内容

   ```yaml
   apiVersion: v1  #版本
   kind: ReplicationController  #类型为副本控制器
   metadata:
     name: mysql  # rc 的名称 全局唯一
   spec:  # rc 的定义
     replicas: 1  # pod 副本的数量
     template:    # 根据此模板创建pod的副本
       metadata:
         labels:
           app: mysql # pod 副本拥有的标签，对应 rc 的selector pod 的标签选择器
       spec:
         containers: # pod 容器定义的部分
           - name: mysql #容器的名称
             image: mysql # 容器镜像的名称
             ports:
               - containerPort: 3306 # 容器暴露的端口
             env:     # 注入到容器内的环境变量
               - name: MYSQL_ROOT_PASSWORD
                 value: "123456"
         restartPolicy: Always
     selector:  # rc 的 pod标签选择器 ，管理拥有这些标签的pod实例 确保集群上有且仅有  replicas 个 pod 在运行
       app: mysql   # 符合模板的pod拥有此标签
   
   ```

7. 执行该yaml 文件

   ```
   kubectl create -f mysql-rc.yaml
   ```

8. 查看当前rc的启动情况

   ```
   kubectl get rc
   ```

9. 查看当前pod

   ```
   kubectl get pods
   ```

10. 如果rc 的ready状态一直为0，且pod的status状态一直为ContainerCreating，可以通过日志查看原因。详细参考爬坑 1 

   ```
   journalctl -u kubelet -f
   或者
   kubectl describe pod {pod_name}
   ```




## 集群k8s 安装

1. 准备两个主机 master:192.168.51.129    node:192.168.51.130

2. 关闭maste 以及 node 的防火墙

   ```
   systemctl disable firewalld
   systemctl stop firewalld
   ```

3.  在master 以及node 安装kubernetes，安装时会自动安装docker

   ```
   yum install -y kubernetes
   ```

4. 在master 以及 node上设置docker 开机自启

   ```
   systemctl enable docker
   systemctl start docker
   ```

5. 在master主机安装etcd

   ```
   yum install -y etcd
   ```

6. 设置开机启动并且启动服务

   ```
   systemctl enable etcd
   systemctl start etcd
   ```

7. 检查是否启动成功 `etcdctl cluster-health`：

   ```
   member 8e9e05c52164694d is healthy: got healthy result from http://localhost:2379
   cluster is healthy
   ```

8. 在master上修改配置文件`/etc/kubernetes/apiserver`,增加一些启动参数

   ```
   KUBE_API_ARGS="--etcd_servers=http://127.0.0.1:2379
   --insecure-bind-address=0.0.0.0 --insecure-port=8080
   --service-cluster-ip-range=169.169.0.0/16 --service-node-port-range=1-65535
   --admission_control=NamespaceLifecycle,LimitRanger,SecurityContextDeny,ServiceAccount,ResourceQuota --logtostderr=false --log-dir=/var/log/kubernetes --v=2"
   ```

   启动参数说明

   - `--etcd_servers` : 指定etcd服务的url。
   - `--insecure-bind-address` : apiserver 绑定的主机的非安全IP地址，设为0.0.0.0 表示绑定所有ip地址
   - `--insecure-port` : apiserver 绑定主机的非安全端口号，默认8080 。
   - `--service-cluster-ip-range` ：Kubernetes 集群中service 的虚拟ip地址段范围，不能与物理机的真实ip段重合。
   - `--service-node-port-range` : kubernetes 集群中service 可映射的物理机端口号范围，默认30000~32767。
   - `admission_control` : kubernetes 集群准入控制设置。各控件模块以插件的形式依次生效。
   - `--logtostderr` : 设置为false表示将日志写入文件，不写入stderr。
   - `--log-dir` : 日志目录。
   - `--v` : 日志级别

9. 修改 `/usr/lib/systemd/system/kube-controller-manager.service`为

   ```
   Description=Kubernetes Controller Manager
   Documentation=https://github.com/GoogleCloudPlatform/kubernetes
   After=kube-apiserver.service
   Requires=kube-apiserver.service
   
   [Service]
   EnvironmentFile=-/etc/kubernetes/config
   EnvironmentFile=-/etc/kubernetes/controller-manager
   User=kube
   ExecStart=/usr/bin/kube-controller-manager \
               $KUBE_LOGTOSTDERR \
               $KUBE_LOG_LEVEL \
               $KUBE_MASTER \
               $KUBE_CONTROLLER_MANAGER_ARGS
   Restart=on-failure
   LimitNOFILE=65536
   
   [Install]
   WantedBy=multi-user.target
   
   ```

10. 修改`/etc/kubernetes/controller-manager` 增加启动参数

    ```
    KUBE_CONTROLLER_MANAGER_ARGS="--master=http://192.168.51.129:8080 --logtostderr=false --log-dir=/var/log/kubernetes --v=2"
    ```

    启动参数说明：

    - `--master` : 指定apiserver的url地址

    - `--logtostderr` : 设置为false表示将日志写入文件，不写入stderr。
    - `--log-dir` : 日志目录。
    - `--v` : 日志级别

11. 修改 `/usr/lib/systemd/system/kube-scheduler.service`为

    ```
    [Unit]
    Description=Kubernetes Scheduler Plugin
    Documentation=https://github.com/GoogleCloudPlatform/kubernetes
    After=kube-apiserver.service
    Requires=kube-apiserver.service
    
    [Service]
    EnvironmentFile=-/etc/kubernetes/config
    EnvironmentFile=-/etc/kubernetes/scheduler
    User=kube
    ExecStart=/usr/bin/kube-scheduler \
                $KUBE_LOGTOSTDERR \
                $KUBE_LOG_LEVEL \
                $KUBE_MASTER \
                $KUBE_SCHEDULER_ARGS
    Restart=on-failure
    LimitNOFILE=65536
    
    [Install]
    WantedBy=multi-user.target
    
    ```

    

12. 修改`/etc/kubernetes/scheduler` 增加启动参数

    ```
    KUBE_CONTROLLER_MANAGER_ARGS="--master=http://192.168.51.129:8080 --logtostderr=false --log-dir=/var/log/kubernetes --v=2"
    ```

    启动参数说明：

    - `--master` : 指定apiserver的url地址

    - `--logtostderr` : 设置为false表示将日志写入文件，不写入stderr。
    - `--log-dir` : 日志目录。
    - `--v` : 日志级别

13. 依次启动 kube-apiserver 、kube-controller-manager 、kube-scheduler 三个服务

    ```
    systemctl enable kube-apiserver
    systemctl start kube-apiserver
    systemctl enable kube-controller-manager
    systemctl start kube-controller-manager
    systemctl enable kube-scheduler
    systemctl start kube-scheduler
    ```

14. 在node 节点修改 `/etc/kubernetes/kubelet`

    ```
    KUBELET_ARGS="--api-servers=192.168.51.129:8080 --hostname-override=192.168.51.130 --logtostderr=false --log-dir=/var/log/kubernetes --v=2"
    ```

    启动参数说明：

    - `--api-servers` : 指定apiserver的url地址
    - `--hostname-override` : 在集群中node街道显示的主机名

    - `--logtostderr` : 设置为false表示将日志写入文件，不写入stderr。
    - `--log-dir` : 日志目录。
    - `--v` : 日志级别

15. 在node节点

    ```
    [Unit]
    Description=Kubernetes Kube-Proxy Server
    Documentation=https://github.com/GoogleCloudPlatform/kubernetes
    After=network.target
    Requires=network.service
    [Service]
    EnvironmentFile=-/etc/kubernetes/config
    EnvironmentFile=-/etc/kubernetes/proxy
    ExecStart=/usr/bin/kube-proxy \
                $KUBE_LOGTOSTDERR \
                $KUBE_LOG_LEVEL \
                $KUBE_MASTER \
                $KUBE_PROXY_ARGS
    Restart=on-failure
    LimitNOFILE=65536
    
    [Install]
    WantedBy=multi-user.target
    
    ```

16. 修改`/etc/kubernetes/proxy `增加启动参数

    ```
    KUBE_CONTROLLER_MANAGER_ARGS="--master=http://192.168.51.129:8080 --logtostderr=false --log-dir=/var/log/kubernetes --v=2"
    ```

    启动参数说明：

    - `--master` : 指定apiserver的url地址

    - `--logtostderr` : 设置为false表示将日志写入文件，不写入stderr。
    - `--log-dir` : 日志目录。
    - `--v` : 日志级别

17. 依次启动kubelet.service 、 kube-proxy

    ```
    systemctl enable kubelet.service
    systemctl start kubelet.service
    systemctl enable kube-proxy
    systemctl start kube-proxy
    ```

18. 在master 节点安装kubeadm

    ```
    yum install -y kubeadm
    ```

19. 在master 安装证书

    

    

常用命令（显示的是默认的命名空间下的资源，可以通过 --namespace={name}来显示指定命名空间的资源

- `kubectl get pods` 查看所有的pod
- `kubectl get svc` 查看所有的service
- `kubectl get rc ` 查看所有rc
- `kubectl delete rc {rc_name}` 删除指定的rc
- `kubectl delete svc {svc_name}` 删除指定svc
- `kubectl describe pod {pod_name}` 查看指定名字pod的详细情况
- `kubectl scale rc myweb --replicas=3` rc 中pod的数量改为3





k8s 爬坑 

1.  发现 pod 一直处于ContainerCreating状态 。查看 ` journalctl -u kubelet -f`日志时一直报：

   ```
   Error syncing pod cd91b987-e443-11ea-9c9f-000c29f5efbc, skipping: failed to "StartContainer" for "POD" with ImagePullBackOff: "Back-off pulling image \"registry.access.redhat.com/rhel7/pod-infrastructure:latest\
   
   ```

   解决办法 ：

   - 做一个软连接

     ```shell
      ll /etc/docker/certs.d/registry.access.redhat.com/redhat-ca.crt
     ```

   - 安装 rhsm

     ```
     
     yum install -y *rhsm*
     ```

   - 配置

     ```
     wget http://mirror.centos.org/centos/7/os/x86_64/Packages/python-rhsm-certificates-1.19.10-1.el7_4.x86_64.rpm
     ```

     ```
     rpm2cpio python-rhsm-certificates-1.19.10-1.el7_4.x86_64.rpm | cpio -iv --to-stdout ./etc/rhsm/ca/redhat-uep.pem | tee /etc/rhsm/ca/redhat-uep.pem
     ```

     

   

2.  kubelet does not have ClusterDNS IP configured and cannot create Pod using "ClusterFirst" policy.