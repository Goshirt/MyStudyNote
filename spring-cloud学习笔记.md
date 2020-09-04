## Eureka

eureka作为服务的注册中心，包含服务端，客户端，客户端可以细分为服务提供者，服务消费者

#### 1. 服务端

在启动类上使用`@EnableEurekaServer`注解标明这是作为eureka的服务端，`application.yml`配置信息如下：

```xml
server:
  # 声明注册中心服务端的端口
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    # 作为服务端，不向注册中心注册自己
    registerWithEureka: false
    # 表示不从eureka服务器获取注册信息
    fetchRegistry: false
    serviceUrl:
      # 声明注册中心服务端的地址，服务提供者需要响这个地址注册服务，服务消费者从该服务地址消费
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

spring:
  application:
    # 声明服务端应用名
    name: eurka-server
```

通过 http://localhost:8761/  可以访问到注册中心页面

#### 2. 客户端

###### 服务提供者

在启动类上使用`@EnableEurekaClient` 注解标明这是一个eureka的客户端，`application.yml`的配置信息如下：

```
server:
  port: 8762

spring:
  application:
    # 设置提供服务的名称，消费者通过服务名称调用
    name: service-hi

eureka:
  client:
    serviceUrl:
      # 配置eureka server的服务端地址
      defaultZone: http://localhost:8761/eureka/
```

编写controller 提供接口

```java
@RestController
public class HiController {

    @Value("${server.port}")
    String port;

    @RequestMapping("/hi")
    public String home(@RequestParam(value = "name", defaultValue = "helmet") String name) {
        return "hi " + name + " ,i am from port:" + port;
    }
}
```

通过idea ,修改yml配置文件的端口为8673,启动多一个服务提供者，访问页面可以看到同一个接口现在有俩个实例

![1595948918522](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1595948918522.png)



###### 服务消费者(使用`RestTemplate` 进行远程调用)

在启动类上使用`@EnableEurekaClient` 注解标明这是一个eureka的客户端，同时使用`@EnableDiscoveryClient ` 标明向服务注册中心注册 

```java
/**
 * EnableEurekaClient 注解标明这是一个EurekaClient客户端
 * EnableDiscoveryClient 向服务中心注册
 */
@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication
public class Comsumer8674RibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(Comsumer8674RibbonApplication.class, args);
    }


    /**
     * @Author Helmet
     * @Date 22:28 2020/7/31
     * @Param []
     * @return org.springframework.web.client.RestTemplate
     *
     * 注册一个bean RestTemplate,并通过@LoadBalanced开启负载均衡,
    */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

`application.yml`的配置信息如下：

```xml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8764

spring:
  application:
    name: service-comsumer-ribbon
```

`service` 层调用

```java
@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    public String hiServie(String name){
        return restTemplate.getForObject("http://SERVICE-HI/hi?name="+name,String.class);
    }
}
```

###### 服务消费者(使用`Feign` 进行远程调用)

在启动类中要使用`@EnableFeignClients`开启 feign 功能

```java
/**
 * @Author Helmet
 * @Date 12:22 2020/8/1
 * @Param
 * @return
 *
*/
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class Comsumer8675FeginRiboonApplication {

    public static void main(String[] args) {
        SpringApplication.run(Comsumer8675FeginRiboonApplication.class, args);
    }

}
```

`service` 层调用时

```java
/**
 * @ClassName HelloService
 * @Author Helmet
 * @date 2020/8/1 12:14
 * FeignClien 指定调用哪个服务，
 **/
@FeignClient(value = "SERVICE-HI")
public interface HelloService {

    /**
     * @Author Helmet
     * @Date 12:18 2020/8/1
     * @Param [name]
     * @return java.lang.String
     * 调用service-hi 服务的 '/hi'接口
    */
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHi(@RequestParam(value = "name")String name);
}
```

`application.yml`的配置信息如下：

```java
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/

spring:
  application:
    name: service-comsumer-fegin-ribbon
```



#### Hystrix 断路由的使用

当一个服务调用另一个服务不可用达到一个阀值（Hystric 是5秒20次） ，可以执行指定的方法

###### 服务消费者(使用`RestTemplate` 进行远程调用)时的断路由使用

1. 首先添加依赖

   ```
   <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
     </dependency>
   ```

2. 启动类添加注解`@EnableHystrix`开启断路由

3. 在调用服务的接口方法上使用注解`@HystrixCommand`并指定出错时执行的方法名

   ```java
   @Service
   public class HelloService {
   
       @Autowired
       RestTemplate restTemplate;
   
       @HystrixCommand(fallbackMethod = "hiError")
       public String hiService(String name) {
           return restTemplate.getForObject("http://SERVICE-HI/hi?name="+name,String.class);
       }
   
       public String hiError(String name) {
           return "hi,"+name+",sorry,error!";
       }
   
   }
   ```

   

###### 服务消费者(使用`Feign` 进行远程调用)时断路由的使用

1. 由于Fegin 自带断路器，可以直接在配置文件`application.yml`中添加配置，开启断路器

```
feign:
  hystrix:
    enabled: true
```

2. 在调用服务的接口类上

   ```java
   /**
    * @ClassName HelloService
    * @Author Helmet
    * @date 2020/8/1 12:14
    * FeignClien  value:指定调用哪个服务， fallback:指定出错执行的类，该类需要继承当前类，当出错时会调用重写的方法
    **/
   @FeignClient(value = "SERVICE-HI",fallback = HiServiceError.class)
   public interface HelloService {
   
       /**
        * @Author Helmet
        * @Date 12:18 2020/8/1
        * @Param [name]
        * @return java.lang.String
        * 调用service-hi 服务的 '/hi'接口
       */
       @RequestMapping(value = "/hi",method = RequestMethod.GET)
       String sayHi(@RequestParam(value = "name")String name);
   }
   ```

3. 新建`HiServiceError`类并实现`sayHi`方法

   ```java
   /**
    * @ClassName HiServiceError
    * @Author Helmet
    * @date 2020/8/1 21:37
    **/
   @Component
   public class HiServiceError implements HelloService {
       @Override
       public String sayHi(String name) {
           return "hi: " + name +"sorry,error";
       }
   }
   ```

   