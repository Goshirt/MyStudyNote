## spring两种ioc容器实现
- BeanFactory
  
  > 
- ApplicationContext  
ApplicationContext是BeanFactory的子接口（推荐使用）。有三个具体的实现类：
    1. ClassPathXmlApplicationContext：从calsspath中装入XML配置文件。

   ```
   ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
   MyBean mybean = context.getBean("Mybean");
  ````
    2. FileSystemXmlApplicationCotext:从文件系统或者URL装载XML配置文件
    3. XmlWebApplicationContext和XmlPortletApplicationContext:仅用于web和入口应用程序。

## bean属性的注入方式
- 属性注入

  ![1586693773957](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1586693773957.png)

- 构造函数注入

  ![1586693812596](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1586693812596.png)

- 工厂方法注入（静态工厂，非静态工厂）

  ![1586693829666](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1586693829666.png)

- 泛型依赖注入

## aop底层的两种实现方式
- jdk动态代理
  > JDK动态代理主要涉及java.lang.reflect包下边的两个类：Proxy和InvocationHandler,只能为接口创建代理实例，而对于没有通过接口定义业务方法的类。
  通过实现InvocationHandler接口创建自己的调用处理器；
 1. 通过为Proxy类指定ClassLoader对象和一组interface来创建动态代理；
1. 通过反射机制获取动态代理类的构造函数，其唯一参数类型就是调用处理器接口类型；
2. 通过构造函数创建动态代理类实例，构造时调用处理器对象作为参数参入。
   
- CGlib
  
  > 采用底层的字节码技术，全称是：Code Generation Library，CGLib可以为一个类创建一个子类，在子类中采用方法拦截的技术拦截所有父类方法的调用并顺势织入横切逻辑。

## Sping事务
###### 实现方式
1. 编程式事务管理，通过beginTransaction(),commit(),rollback()方法管理事务
2. 基于 TransactionProxyFactoryBean的声明式事务管理，主要是通过在xml中配置
    ```
    <bean id="serviceProxy" class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
            <property name="transactionManager" ref="myTracnsactionManager"></property>
            <property name="target" ref="buyStockService"></property>
            <property name="transactionAttributes">
                <props>
                    <!-- 主要 key 是方法   
                        ISOLATION_DEFAULT  事务的隔离级别
                        PROPAGATION_REQUIRED  传播行为
                    -->
                    <prop key="add*">ISOLATION_DEFAULT,PROPAGATION_REQUIRED</prop>
                    <!-- -Exception 表示发生指定异常回滚，+Exception 表示发生指定异常提交 -->
                    <prop key="buyStock">ISOLATION_DEFAULT,PROPAGATION_REQUIRED,-BuyStockException</prop>
                </props>
            </property>
            
        </bean>
    ```
3. 基于注解@Transactional
4. 基于Aspectj AOP配置事务
    ```
    <!-- 事务管理器 -->
	<bean id="myTracnsactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource"></property>
	</bean>
	
	<tx:advice id="txAdvice" transaction-manager="myTracnsactionManager">
		<tx:attributes>
			<!-- 为连接点指定事务属性 -->
			<tx:method name="add*" isolation="DEFAULT" propagation="REQUIRED"/>
			<tx:method name="buyStock" isolation="DEFAULT" propagation="REQUIRED" rollback-for="BuyStockException"/>
		</tx:attributes>
	</tx:advice>
	
	<aop:config>
		<!-- 切入点配置 -->
		<aop:pointcut expression="execution(* *..service.*.*(..))" id="point"/>
		<aop:advisor advice-ref="txAdvice" pointcut-ref="point"/>
	</aop:config>
   ```
###### 事务的传播性 @Transactionl 的propagation属性可以设置
- `propagation_requierd`：如果当前没有事务，就新建一个事务，如果已存在一个事务中，加入到这个事务中，这是Spring默认的选择。
- `propagation_supports`：支持当前事务，如果没有当前事务，就以非事务方法执行。
- `propagation_mandatory`：使用当前事务，如果没有当前事务，就抛出异常。
- `propagation_required_new`：无论当前事务是否存在，都会创建薪的事务运行。
- `propagation_not_supported`：以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
- `propagation_never`：以非事务方式执行操作，如果当前事务存在则抛出异常。
- `propagation_nested`：在当前方法调用子方法时，如果也设置了事务且子方法发生异常，只回滚子方法执行过的sql,而不回滚当前方法的事务

###### 事务的隔离级别 @Transactionl 的isolation属性可以设置
- 未提交读
- 读已提交
- 可重复读
- 串行化

###### @Transactional事务的失效
1. 是否用于public方法上，因为@Transactional的作用范围是public
2. 是否抛出了checked异常，checked异常不回滚，通过`@Transactional(rollbackFor=Exception.class) `可以解决checked异常不回滚的问题
3. 数据库的引擎是否支持事务
4. 是否开启了对注解的解析。`<tx:annotation-driven transaction-manager="transactionManager" proxy-target-class="true"/>`

###### 枚举类在pojo中的使用
1.通过定义一个类继承使用BaseTypeHandler
