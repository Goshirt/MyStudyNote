# 服务提供者的简单配置provider.xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
        xsi:schemaLocation="http://www.springframework.org/schema/beans        
        http://www.springframework.org/schema/beans/spring-beans-4.3.xsd        
        http://dubbo.apache.org/schema/dubbo        
        http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

        <!-- 提供方应用信息，用于计算依赖关系 -->
        <dubbo:application name="hello-world-app"  />

        <!-- 使用multicast广播注册中心暴露服务地址 -->
        <dubbo:registry address="multicast://224.5.6.7:1234" />

        <!-- 用dubbo协议在20880端口暴露服务 -->
        <dubbo:protocol name="dubbo" port="20880" />

        <!-- 声明需要暴露的服务接口 -->
        <dubbo:service interface="org.apache.dubbo.demo.DemoService" ref="demoService" />

        <!-- 和本地bean一样实现服务 -->
        <bean id="demoService" class="org.apache.dubbo.demo.provider.DemoServiceImpl" />
    </beans>
    
# 服务消费者的简单配置consumer.xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:dubbo="http://dubbo.apache.org/schema/dubbo"
        xsi:schemaLocation="http://www.springframework.org/schema/beans        
        http://www.springframework.org/schema/beans/spring-beans-4.3.xsd       
        http://dubbo.apache.org/schema/dubbo        
        http://dubbo.apache.org/schema/dubbo/dubbo.xsd">

        <!-- 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样 -->
        <dubbo:application name="consumer-of-helloworld-app"  />

        <!-- 使用multicast广播注册中心暴露发现服务地址 -->
        <dubbo:registry address="multicast://224.5.6.7:1234" />

        <!-- 生成远程服务代理，id的值需要和provider.xml声明的服务bean id一致，可以和本地bean一样使用demoService -->
        <dubbo:reference id="demoService" interface="org.apache.dubbo.demo.DemoService" />
    </beans>
    
 # 启动时检查：
    Dubbo 缺省会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止 Spring 初始化完成，以便上线时，能及早发现问题，默认 check="true"
    关闭某个服务启动时时的检查（如果没有服务提供者报错）：
      <dubbo:reference interface="com.foo.BarService" check="false" />
    关闭所有服务启动时的检查（如果没有服务提供者报错）：
      <dubbo:consumer check="false" />
    关闭注册中心启动时的检查（订阅失败时报错）：
      <dubbo:registry check="false" />
      
      
# 集群容错：
 
  配置集群的方式：
  <dubbo:service cluster="failsafe" />
  或
  <dubbo:reference cluster="failsafe" />

  容错的模式
      Failover（失败自动切换，默认）：当出现失败，重试其它服务器 。通常用于读操作，但重试会带来更长延迟。可通过 retries="2" 
                                      来设置重试次数(不含第一次)
            retries的使用。例如：
                在provider.xml配置（选择这个最好）
                <dubbo:service retries="2" />
                或者
                在consumer.xml配置
                <dubbo:reference retries="2" />
                或者
                方法级别的配置
                <dubbo:reference>
                    <dubbo:method name="findFoo" retries="2" />
                </dubbo:reference>

      Failfast
      快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录。

      Failsafe 
      失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。

      Failback 
      失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。

      Forking 
      并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 forks="2" 来设置最大并行数。

      Broadcast 
      广播调用所有提供者，逐个调用，任意一台报错则报错 。通常用于通知所有提供者更新缓存或日志等本地资源信息。
          
          
  # 负载均衡
   
    配置负载均衡的方式：

      服务端服务级别
      <dubbo:service interface="..." loadbalance="roundrobin" />
      客户端服务级别
      <dubbo:reference interface="..." loadbalance="roundrobin" />
      服务端方法级别
      <dubbo:service interface="...">
          <dubbo:method name="..." loadbalance="roundrobin"/>
      </dubbo:service>
      客户端方法级别
      <dubbo:reference interface="...">
          <dubbo:method name="..." loadbalance="roundrobin"/>
      </dubbo:reference>

       负载均衡策略
           Random（默认）
              随机，按权重设置随机概率。
              在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。

       RoundRobin
          轮询，按公约后的权重设置轮询比率。
          存在慢的提供者累积请求的问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。

       LeastActive（当使用该策略时，该服务默认的并发控制会调用服务端或者消费端最小的并发数）
          最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。
          使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。

       ConsistentHash
          一致性 Hash，相同参数的请求总是发到同一提供者。
          当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。
          算法参见：http://en.wikipedia.org/wiki/Consistent_hashing
          缺省只对第一个参数 Hash，如果要修改，请配置 <dubbo:parameter key="hash.arguments" value="0,1" />
          缺省用 160 份虚拟节点，如果要修改，请配置 <dubbo:parameter key="hash.nodes" value="320" />

              
  # 并发控制：
      服务端通过executes属性控制
      并发执行数不能超过10
        <dubbo:service interface="com.foo.BarService" executes="10" />
      
      消费端通过actives属性控制
       <dubbo:service interface="com.foo.BarService" actives="10" />
