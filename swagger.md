## spring boot 整合swagger

### 添加依赖

```xml
<!-- Swagger API文档 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
            <exclusions>
                <exclusion>
                    <groupId>io.swagger</groupId>
                    <artifactId>swagger-annotations</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>io.swagger</groupId>
                    <artifactId>swagger-models</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>swagger-bootstrap-ui</artifactId>
            <version>1.9.6</version>
        </dependency>
        <!-- # 增加两个配置解决 NumberFormatException -->
        <dependency>
            <groupId>io.swagger</groupId>
            <artifactId>swagger-annotations</artifactId>
            <version>1.5.22</version>
        </dependency>
        <dependency>
            <groupId>io.swagger</groupId>
            <artifactId>swagger-models</artifactId>
            <version>1.5.22</version>
        </dependency>
```



### 添加配置文件

```java
package com.helmet.esdemo.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author Chrisp
 */
@Slf4j
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class Swagger2Config implements WebMvcConfigurer {

  /**
   * 显示swagger-ui.html文档展示页，还必须注入swagger资源：
   *
   * @param registry
   */
  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  /**
   * swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
   *
   * @return Docket
   */
  @Bean
  public Docket createScRpsRestApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .groupName("esApi")
        .select()
        //此包路径下的类，才生成接口文档
        .apis(basePackage("com.helmet"))
        //加了ApiOperation注解的类，才生成接口文档
        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        //.apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
    //设置全局参数，身份校验
    //.globalOperationParameters(setHeaderToken());
  }

  /**
   * 扩展可以扫描多个包
   **/
  public static Predicate<RequestHandler> basePackage(final String basePackage) {
    return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
  }

  private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
    return input -> {
      // 循环判断匹配
      for (final String strPackage : basePackage.split(";")) {
        final boolean isMatch = input.getPackage().getName().startsWith(strPackage);
        if (isMatch) {
          return true;
        }
      }
      return false;
    };
  }

  private static Optional<? extends Class<?>> declaringClass(final RequestHandler input) {
    return Optional.fromNullable(input.declaringClass());
  }

  /**
   * api文档的详细信息函数,注意这里的注解引用的是哪个
   *
   * @return
   */
  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        // //大标题
        .title("es demo")
        // 版本号
        .version("1.0")
        // 描述
        .description("es demo 的接口文档")
        // 作者
        .license("helmet")
        .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
        .build();
  }


}

```

### 访问路径

```
http://ip:port/doc.html
```

