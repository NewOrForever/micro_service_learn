此例子参照的是官方例子：https://github.com/alibaba/spring-cloud-alibaba/tree/2021.x/spring-cloud-alibaba-examples/integrated-example

------
通常在 spring cloud 项目中，我们在 `bootstrap.yml` 中配置如下内容：
- `spring.application.name` 和 `server.port` 来指定服务名称和端口号
- 服务注册中心和配置中心

原来的 `application.yaml` 中的配置内容我们会放到**配置中心**中**动态加载**


**spring-boot 2.4.x 版本之后，`bootstrap.yml` 默认不会加载了，那么如何才能继续使用原先的 `bootstrap.yaml` 功能呢？**

**方案一：设置 `spring.cloud.bootstrap.enabled=true`**
- @see org.springframework.cloud.bootstrap.BootstrapApplicationListener.onApplicationEvent
- @see org.springframework.cloud.util.PropertyUtils.bootstrapEnabled

如果没有 `application.yaml` 的话，那就在启动参数中添加 `-Dspring.cloud.bootstrap.enabled=true` 来加载

**方案二：在父 pom 中添加如下配置引入 Bootstrap 启动器**
``` xml
<!-- bootstrap 启动器 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

**方案三：不用 `bootstrap.yaml`**

spring-boot 2.4.x 版本之后推荐在 `application.yaml` 中配置 `spring.config.import` 来导入对应的配置源

如 nacos 可以配置如下：`spring.config.import=option:nacos:service-config-id.yaml`
``` yaml
# @see org.springframework.cloud.commons.ConfigDataMissingEnvironmentPostProcessor.postProcessEnvironment
# @see org.springframework.cloud.commons.ConfigDataMissingEnvironmentPostProcessor.shouldProcessEnvironment
# @see com.alibaba.cloud.nacos.configdata.NacosConfigDataMissingEnvironmentPostProcessor.shouldProcessEnvironment
#   - 如果想要希望禁用此检查，可以设置：     spring.cloud.nacos.config.import-check.enabled=false 或 spring.cloud.nacos.config.enabled=false
spring
 config:
   import: optional:nacos:integrated-gateway.yaml
```

官方推荐的方案三暂时还是不太明白新版做了哪些处理，等有时间再研究一下，暂时先用**方案二引入 Bootstrap 启动器来实现原来的功能**