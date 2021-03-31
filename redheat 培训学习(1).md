## 数据库自动化 

1. 参考文档

   中文： https://www.cwiki.us/pages/viewpage.action?pageId=47842520 

   英文： https://flywaydb.org/documentation/ 

2. [Flyway与Liquibase对比](https://reflectoring.io/database-refactoring-flyway-vs-liquibase/) 

3. 实现

   - 添加maven依赖

     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-jdbc</artifactId>
     </dependency>
     <dependency>
         <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
         <scope>runtime</scope>
    </dependency>
   	 	<dependency>
           <groupId>org.flywaydb</groupId>
           <artifactId>flyway-core</artifactId>
           <version>5.2.4</version>
    </dependency>  

     ```

​     

   - 添加配置

     ```properties
     spring.flyway.locations=classpath:db/migration/h2
     # 开启flyway
     spring.flyway.enabled=true
     # 禁止flyway执行清理，如果执行清理的会导致每次数据丢失
     spring.flyway.clean-disabled=true
     #如果我们并不是在项目初就加flyway的话，则在有历史数据的情况下，加入flyway后，将会出现：set baselineOnMigrate to true to initialize the schema history table.
     spring.flyway.baseline-on-migrate=true
     #设置基础版本号起始值为0，默认为1
     spring.flyway.baseline-version=0
     ```

     

 - flyway其他配置项：
       
     ```properties
     flyway.baseline-description= # 执行基线时标记已有Schema的描述
     flyway.baseline-version=1 # 基线版本默认开始序号 默认为 1. 
     flyway.baseline-on-migrate=false # 针对非空数据库是否默认调用基线版本 ， 这也是我们上面版本号从 2 开始的原因
     flyway.check-location=false # 是否开启脚本检查 检查脚本是否存在 默认false
     flyway.clean-on-validation-error=false # 验证错误时 是否自动清除数据库 高危操作！！！
     flyway.enabled=true # 是否启用 flyway.
     flyway.encoding=UTF-8 # 脚本编码.
     flyway.ignore-failed-future-migration=true # 在读元数据表时，是否忽略失败的后续迁移.
     flyway.init-sqls= # S获取连接后立即执行初始化的SQL语句
     flyway.locations=classpath:db/migration # 脚本位置， 默认为classpath: db/migration.
     flyway.out-of-order=false # 是否允许乱序（out of order）迁移
     flyway.placeholder-prefix={  # 设置每个占位符的前缀。 默认值： ${ 。 
     flyway.placeholder-replacement=true # 是否要替换占位符。 默认值： true 。 
     flyway.placeholder-suffix=} # 设置占位符的后缀。 默认值： } 。 
     flyway.placeholders.*= # 设置占位符的值。
     flyway.schemas= # Flyway管理的Schema列表，区分大小写。默认连接对应的默认Schema。
    flyway.sql-migration-prefix=V # 迁移脚本的文件名前缀。 默认值： V 。 
     flyway.sql-migration-separator=__ # 迁移脚本的分割符 默认双下划线
    flyway.sql-migration-suffix=.sql # 迁移脚本的后缀 默认 .sql
     flyway.table=schema_version # Flyway使用的Schema元数据表名称 默认schema_version
    flyway.url= # 待迁移的数据库的JDBC URL。如果没有设置，就使用配置的主数据源。
     flyway.user= # 待迁移数据库的登录用户。
    flyway.password= # 待迁移数据库的登录用户密码。
     flyway.validate-on-migrate=true # 在运行迁移时是否要自动验证。 默认值： true 。
    ```

   - sql 文件的命名规则 ：V{version}__{name}.sql
   
   - 在`spring.flyway.locations`指定的目录下添加每个版本的sql文件。**注意：每一个运行过的版本sql文件不能再次修改。**
   
   - 项目运行之后，会在数据库中生成一个名为`flyway_schema_history`的表，用来记录每个版本sql的执行结果。如果success的标记为0，证明对应版本的sql执行失败。
   
     ![1598839388597](C:\Users\Helmet\AppData\Roaming\Typora\typora-user-images\1598839388597.png)
   
- 当使用druid 数据源是，需要解决druid防火墙于flyway利用脚本创建数据库的冲突，添加配置

  ```yml
  spring:
    datasource:
      druid:
        wall:
          config:
            variantCheck: false
            noneBaseStatementAllow: true
            commentAllow: true
            multiStatementAllow: true
  ```

  




## 测试驱动开发--TDD

 1. 参考文档：

     https://testerhome.com/topics/7060 

 2. 测试驱动开发，同过编写测试类，检查接口的健壮性，检查预期与实际是否相等。

 3. **使用技术**：`junit5`、`rest-assured` ，添加maven依赖

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <scope>test</scope>
        <version>3.3.0</version>
    </dependency>
    ```

 4. 编写测试类

    ```java
    package com.csg.cms.example.acceptance;
    
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
    import org.springframework.boot.web.server.LocalServerPort;
    import org.springframework.test.context.ActiveProfiles;
    
    import io.restassured.http.ContentType;
    import lombok.extern.slf4j.Slf4j;
    
    import static io.restassured.RestAssured.*;
    import static org.hamcrest.Matchers.*;
    import static org.junit.jupiter.api.Assertions.assertTrue;
    
    import java.io.IOException;
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    
    @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("dev")
    @Slf4j
    class CMSApplicationTests {
    
    	@LocalServerPort
    	int port;
    
    	@Test
    	@DisplayName("访问/api/greeting 可以返回200")
    	void greetingIsUp() {
    		given()
    			.port(port)
    		.when()
    			.get("/api/greeting")
    		.then()
    			.log().all()
    			.statusCode(200)
    			//使用"$", 返回对象
    			.body("name", equalTo("Zhang"));
    	}
    
    	@Test
    	@DisplayName("访问/api/people, 可以保存数据到数据库")
    	void saveDatatoDB(){
    		given().contentType(ContentType.JSON).body("{\"title\": \"Mr.\",\"name\": \"Ryan\"}").port(port)
    		.when()
    			.post("/api/people")
    		.then()
    			.statusCode(200)
    			.body("name", equalTo("Ryan"))
    			.body("id", greaterThanOrEqualTo(1));
    	}
    
    
    	@Test
    	@DisplayName("访问/api/people,可以从数据库获取数据")
    	void fetchDataFromDB(){
    		given().contentType(ContentType.JSON).body("{\"title\": \"Mr.\",\"name\": \"Ryan\"}").port(port)
    			.when()
    				.post("/api/people")
    			.then()
    				.statusCode(200);
    		given().port(port)
    			.when()
    				.get("/api/people")
    			.then()	
    				.log().all()
    				.statusCode(200)
    				.body("name", hasItem("Ryan"))
    				.body("title", hasItem("Mr."));
    		given().port(port)
    				.when()
    					.get("/api/people/1")
    				.then()
    					.log().all()
    					.statusCode(200)
    					.body("name", equalTo("Ryan"))
    					.body("title", equalTo("Mr."));
    	}
    
    	@Value("${logging.file}")
    	String log_file_path;
    	@Test
    	@DisplayName("可以保存并且能够输出应用日志到文件")
    	void applicationLogTraceable() throws IOException{
    		String mark =  "red to read, green to great, perfectly safe!";
    		log.info(mark);
    		assertTrue(
    			new String(Files.readAllBytes(Paths.get(log_file_path)), 
    				Charset.defaultCharset()).contains(mark), "检测日志文件包含指定字符串失败!"
    		);
    	}
    
    }
    
    ```

 5. 编写接口

    ```java
    package com.csg.cms.example.controller;
    
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;
    import java.util.stream.StreamSupport;
    
    import com.csg.cms.example.domain.People;
    import com.csg.cms.example.repository.PeopleRepository;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import java.util.HashMap;
    
    import com.csg.cms.common.errorhandle.BusinessException;
    import com.csg.cms.common.errorhandle.ResultStatus;
    import com.csg.cms.common.vostruct.Result;
    
    
    import lombok.RequiredArgsConstructor;
    import springfox.documentation.annotations.ApiIgnore;
    
    @RestController
    @RequiredArgsConstructor
    @ApiIgnore
    @RequestMapping("/api")
    public class PeopleController {
    	final PeopleRepository repository;
    
    	@GetMapping("/people")
    	public List<People> listPeople() {
    		return StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
    	}
    
    	@GetMapping("/people/{id}")
    	public Optional<People> getPeopleById(@PathVariable Long id) {
    		Optional<People> result = repository.findById(id);
    		return result;
    	}
    
    	@PostMapping("/people")
    	public People savePeople(@RequestBody final People people) {
    		repository.save(people);
    		return people;
    
    	}
    	@GetMapping("/greeting")
    	public HashMap<String, String> greeting() {
    		return new HashMap<String, String>() {
    						{
    							put("title", "Mr.");
    							put("name", "Zhang");
    						}
    					};
    
    	}
    }
    ```

 6. 使用`mvn clean package` ，当运行测试出错时，修改接口，直到测试通过。





## CDC--契约测试



1. 官方文档地址：

   中文： https://www.springcloud.cc/spring-cloud-contract.html 

   英文： https://cloud.spring.io/spring-cloud-contract/reference/html/getting-started.html#getting-started 

   


## OpenFegin 服务间调用

1. 参考文档

   中文： https://www.jianshu.com/p/b6a47b06d3dc 

   英文： https://spring.io/projects/spring-cloud-openfeign 

2. 添加maven依赖

   ```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
   ```

3. 编写服务调用层service

   ```java
   package com.sc.demo.example.service;
   
   import com.sc.demo.example.service.impl.HiServiceErrorImpl;
   import org.springframework.cloud.openfeign.FeignClient;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RequestMethod;
   import org.springframework.web.bind.annotation.RequestParam;
   
   /**
    * FeignClient
    * url:指定调用服务的url，
    * fallback(用于服务降级):指定出错执行的类，该类需要继承当前类，当出错时会调用重写的方法,需要加入hystrix依赖
    **/
   @FeignClient(name = "hiService",url = "http://www.baidu.com",fallback = HiServiceErrorImpl.class)
   public interface HiService {
   
       /**
        * 调用service-hi 服务的 '/hi'接口
       */
       @RequestMapping(value = "/hi",method = RequestMethod.GET)
       String sayHi(@RequestParam(value = "name") String name);
   
   }
   
   ```

4. 编写服务降级类

   ```java
   package com.sc.demo.example.service.impl;
   
   import com.sc.demo.example.service.HiService;
   import org.springframework.stereotype.Service;
   
   /**
    * fallback 时服务降级的处理
   */
   @Service("hiServiceError")
   public class HiServiceErrorImpl implements HiService {
       
       /**
        *在重写方法中处理服务降级的业务代码
       */
       @Override
       public String sayHi(String name) {
           return "hi: " + name +"sorry,error";
       }
   }
   
   ```

5. 编写控制层controller

   ```java
   package com.sc.demo.example.controller;
   
   import com.sc.demo.example.service.HiService;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RequestParam;
   import org.springframework.web.bind.annotation.RestController;
   
   @RestController
   public class HiController {
   
       @Autowired
       HiService hiService;
   
       @GetMapping(value = "/hi")
       public String sayHi(@RequestParam String name){
           System.out.println("llllll 8675");
           return hiService.sayHi(name);
       }
   
   
   }
   
   ```