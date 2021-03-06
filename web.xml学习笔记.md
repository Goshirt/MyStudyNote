## 加载顺序 ： 
    ServletContext -> context-param -> listener -> filter -> servlet
## 标签
### display-name
  > Web应用名称,用于标记这个特定的Web应用的名称
### discription
  > Web应用描述
### context-param
  > 上下文参数，用于向Servlet+Context提供键值对，即应用程序上下文信息。后续的listener，filter在初始化时会用到这些上下文信息。在servlet里面可以通过getServletContext().getInitParameter("context/param")获取。
### filter
  > 过滤器,Filter接口中有一个doFilter方法，当开发人员编写好Filter，并配置对哪个web资源进行拦截后，WEB服务器每次在调用web资源的service方法之前,都会先调用一下filter的doFilter方法，因此，在该方法内编写代码可达到如下目的：
 1. 调用目标资源之前，让一段代码执行。
 2. 是否调用目标资源（即是否让用户访问web资源）。
 > web服务器在调用doFilter方法时，会传递一个filterChain对象进来，filterChain对象是filter接口中最重要的一个对象，它也提供了一个doFilter方法，开发人员可以根据需求决定是否调用此方法，调用该方法，则web服务器就会调用web资源的service方法，即web资源就会被访问，否则web资源不会被访问。
 使用filter步骤：
 1. 编写java类实现Filter接口，并实现其doFilter方法。
 2. 在 web.xml 文件中使用<filter>和<filter-mapping>元素对编写的filter类进行注册，并设置它所能拦截的资源    
* <fliter>配置详解：
    
        <filter-name>     用于为过滤器指定一个名字，该元素的内容不能为空。 
        <filter-class>    元素用于指定过滤器的完整的限定类名。 
        <init-param>      元素用于为过滤器指定初始化参数，它的子元素<param-name>指定参数的名字，<param-value>指定参数的值。在过滤器中，可以使用FilterConfig接口对象来访问初始化参数。
        <filter-mapping>  元素用于设置一个 Filter 所负责拦截的资源。一个Filter拦截的资源可通过两种方式来指定：Servlet 名称和资源访问的请求路径 
        <filter-name>     子元素用于设置filter的注册名称。该值必须是在<filter>元素中声明过的过滤器的名字 
        <url-pattern>     设置 filter 所拦截的请求路径(过滤器关联的URL样式) 
        <servlet-name>    指定过滤器所拦截的Servlet名称。 
        <dispatcher>      指定过滤器所拦截的资源被 Servlet 容器调用的方式，可以是REQUEST,INCLUDE,FORWARD和ERROR之一，默认REQUEST。用户可以设置多个<dispatcher> 子元素用来指定 Filter 对资源的多种调用方式进行拦截。 
        <dispatcher>      子元素可以设置的值及其意义： 
                    REQUEST：当用户直接访问页面时，Web容器将会调用过滤器。如果目标资源是通过RequestDispatcher的include()或forward()方法访问时，那么该过滤器就不会被调用。 
                    INCLUDE：如果目标资源是通过RequestDispatcher的include()方法访问时，那么该过滤器将被调用。除此之外，该过滤器不会被调用。 
                    FORWARD：如果目标资源是通过RequestDispatcher的forward()方法访问时，那么该过滤器将被调用，除此之外，该过滤器不会被调用。 
                    ERROR：如果目标资源是通过声明式异常处理机制调用时，那么该过滤器将被调用。除此之外，过滤器不会被调用。
 * Filter链：
 > 在一个web应用中，可以开发编写多个Filter，这些Filter组合起来称之为一个Filter链。web服务器根据Filter在web.xml文件中的注册顺序，决定先调用哪个Filter，当第一个Filter的doFilter方法被调用时，web服务器会创建一个代表Filter链的FilterChain对象传递给该方法。在doFilter方法中，开发人员如果调用了FilterChain对象的doFilter方法，则web服务器会检查FilterChain对象中是否还有filter，如果有，则调用第2个filter，如果没有，则调用目标资源。
 * Filter的生命周期：
 > Filter的创建和销毁由WEB服务器负责。 web 应用程序启动时，web 服务器将创建Filter 的实例对象，并调用其init方法，读取web.xml配置，完成对象的初始化功能，从而为后续的用户请求作好拦截的准备工作（filter对象只会创建一次，init方法也只会执行一次）。通过init方法的参数，可获得代表当前filter配置信息的FilterConfig对象。
          
       public void init(FilterConfig filterConfig) throws ServletException;//初始化
       public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws  IOException,ServletException;//拦截请求
       public void destroy();//销毁

       通过FilterConfig对象可以获得以下信息：
        String getFilterName();//得到filter的名称。 
        String getInitParameter(String name);//返回在部署描述中指定名称的初始化参数的值。如果不存在返回null. 
        Enumeration getInitParameterNames();//返回过滤器的所有初始化参数的名字的枚举集合。 
        public ServletContext getServletContext();//返回Servlet上下文对象的引用。

### listerner
  > 监听器Listener就是在application,session,request三个对象创建、销毁或者往其中添加修改删除属性时自动执行代码的功能组件
主要有三种：
    * ServletContext监听
        ServletContextListener：用于对Servlet整个上下文进行监听（创建、销毁），方法如下：
                public void contextInitialized(ServletContextEvent sce);//上下文初始化
                public void contextDestroyed(ServletContextEvent sce);//上下文销毁
                public ServletContext getServletContext();//ServletContextEvent事件：取得一个ServletContext（application）对象

        ServletContextAttributeListener：对Servlet上下文属性的监听（增删改属性），方法如下：
                public void attributeAdded(ServletContextAttributeEvent scab);//增加属性
                public void attributeRemoved(ServletContextAttributeEvent scab);//属性删除
                public void attributeRepalced(ServletContextAttributeEvent scab);//属性替换（第二次设置同一属性）
                //ServletContextAttributeEvent事件：能取得设置属性的名称与内容
                public String getName();//得到属性名称
                public Object getValue();//取得属性的值
   * Session监听
   
         HttpSessionListener接口：对Session的整体状态的监听，方法如下
                public void sessionCreated(HttpSessionEvent se);//session创建
                public void sessionDestroyed(HttpSessionEvent se);//session销毁
                //HttpSessionEvent事件：
                public HttpSession getSession();//取得当前操作的session

          HttpSessionAttributeListener接口：对session的属性监听，方法如下：
                public void attributeAdded(HttpSessionBindingEvent se);//增加属性
                public void attributeRemoved(HttpSessionBindingEvent se);//删除属性
                public void attributeReplaced(HttpSessionBindingEvent se);//替换属性
                //HttpSessionBindingEvent事件：
                public String getName();//取得属性的名称
                public Object getValue();//取得属性的值
                public HttpSession getSession();//取得当前的session

   * Request监听

           ServletRequestListener：用于对Request请求进行监听（创建、销毁），方法如下：
                public void requestInitialized(ServletRequestEvent sre);//request初始化
                public void requestDestroyed(ServletRequestEvent sre);//request销毁
                //ServletRequestEvent事件：
                public ServletRequest getServletRequest();//取得一个ServletRequest对象
                public ServletContext getServletContext();//取得一个ServletContext（application）对象

            ServletRequestAttributeListener：对Request属性的监听（增删改属性），方法如下：
                public void attributeAdded(ServletRequestAttributeEvent srae);//增加属性
                public void attributeRemoved(ServletRequestAttributeEvent srae);//属性删除
                public void attributeReplaced(ServletRequestAttributeEvent srae);//属性替换（第二次设置同一属性）
                //ServletRequestAttributeEvent事件：能取得设置属性的名称与内容
                public String getName();//得到属性名称
                public Object getValue();//取得属性的值

### servlet
> 运行在服务器端的小程序
### session-config
> 会话超时配置，单位min

### welcome-file-list
> 欢迎首页
