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
- 构造函数注入

## aop底层的两种实现方式
- jdk动态代理
  > JDK动态代理主要涉及java.lang.reflect包下边的两个类：Proxy和InvocationHandler,只能为接口创建代理实例，而对于没有通过接口定义业务方法的类。
  通过实现InvocationHandler接口创建自己的调用处理器；
 1. 通过为Proxy类指定ClassLoader对象和一组interface来创建动态代理；
1. 通过反射机制获取动态代理类的构造函数，其唯一参数类型就是调用处理器接口类型；
2. 通过构造函数创建动态代理类实例，构造时调用处理器对象作为参数参入。
   
- CGlib
  > 采用底层的字节码技术，全称是：Code Generation Library，CGLib可以为一个类创建一个子类，在子类中采用方法拦截的技术拦截所有父类方法的调用并顺势织入横切逻辑。