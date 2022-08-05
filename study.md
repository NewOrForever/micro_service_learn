# 微服务学习
## springboot
### springboot自定义starter
步骤（**命名要规范**）：
1. 创建一个module，custom-spring-boot-starter。
   ```
   启动器（starter）是一个空的jar文件， 仅仅提供辅助性依赖管理， 这些依赖需要自动装配或其他类库。
   ```
   pom文件引入autoconfigurer
    ```
   <dependency>
        <groupId>org.example</groupId>
        <artifactId>custom-spring-boot-starter-autoconfigurer</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ```
2. 创建cutom-spring-boot-starter-autoconfigurer这个module。
    ```
    自动装配的类就写在这个工程中。
    ```
    - HelloAutoConfiguration，配置类，在这里去 **@Bean**，下面的这些 **@Conditional的扩展** 如果不匹配的话该AutoConfiguration不会导入进来
    ```
   @Configuration(proxyBeanMethods = false)
    // HelloProperties会注册为bean
    // 这是和配置文件绑定的javabean
    @EnableConfigurationProperties(HelloProperties.class)
    // 配置文件要有该配置否则该AutoConfiguration排除不会装配进来
    @ConditionalOnProperty(value = "custom.hello.name")
    // 要有StrUtil类
    @ConditionalOnClass(StrUtil.class)
   ```
   - HelloProperties类创建，这个类是用于和配置文件的配置绑定的一个javabean
     - 这个 **@EnableConfigurationProperties(HelloProperties.class)** 实际可以不用，该注解作用就是去绑定配置文件和javabean
     - **@ConfigurationProperties("custom.hello")** 要有该注解，这个前缀就是也是需要的，你可以在配置文件中输入试试看
     - pom需要导入一个依赖，这个依赖作用相当于是帮你自动创建metadata.json用于配置文件和javabean的绑定提示
       ```
       <!--‐导入配置文件处理器，配置文件进行绑定就会有提示-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        ```
3. 创建一个META-INF/spring.factories文件。
    ```
    org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    com.autoconfigurer.custom.HelloAutoConfiguration
    ```

## Ribbon学习
### 自定义一个负载均衡拦截器（模仿LoadBalancerAutoConfiguration）
1. 创建一个@MyLoadBalanced注解
```
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Qualifier // core
public @interface MyLoadBalanced {
}
```
2. 创建一个拦截器实现ClientHttpRequestInterceptor，实现intercept方法
```
@Override
public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                    final ClientHttpRequestExecution execution) throws IOException {
    final URI originalUri = request.getURI();
    String serviceName = originalUri.getHost();
    Assert.state(serviceName != null,
            "Request URI does not contain a valid hostname: " + originalUri);
    return this.loadBalancer.execute(serviceName,
            this.requestFactory.createRequest(request, body, execution));
}
```
3. 写一个配置类将拦截器配置进来添加到RestTemplate
```
@MyLoadBalanced // 这样写就只会注入有@MyLoadBalanced注解的RestTemplate进来
@Autowired(required = false)
private List<RestTemplate> restTemplates = Collections.emptyList();

@Bean
public MyLoadBalancerInterceptor myLoadBalancerInterceptor(LoadBalancerClient loadBalancerClient,
                                                           LoadBalancerRequestFactory loadBalancerRequestFactory) {
    return new MyLoadBalancerInterceptor(loadBalancerClient, loadBalancerRequestFactory);
}

/**
 * spring 扩展点 - 在所有的非懒加载单例bean都加载完成之后会去执行
 * @see  SmartInitializingSingleton
 * @param myLoadBalancerInterceptor
 * @return
 */
@Bean
public SmartInitializingSingleton myLoadBalancerAfterSingletonInitial(final MyLoadBalancerInterceptor myLoadBalancerInterceptor) {
    return new SmartInitializingSingleton() {
        @Override
        public void afterSingletonsInstantiated() {
            for (RestTemplate restTemplate : MyLoadBalancerAutoConfiguration.this.restTemplates) {
                List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
                interceptors.add(myLoadBalancerInterceptor);
                restTemplate.setInterceptors(interceptors);
            }
        }
    };
}
```

## feign学习
### 使用

### 自定义拦截器

## sentinel学习
### 单独用（具体可以看sentinel-demo）
核心代码（try ...  catch(BlockException)）：
```
    @RequestMapping("/demo")
    public String demo() {
        Entry entry = null;
        try {
            // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串
            entry = SphU.entry(RESOURCE_NAME);
            // 被保护的业务逻辑 - 实际就是给业务逻辑做个before的切面
            String str = "hello world";
            logger.info("----------------> {}", str);

            return str;
        } catch (BlockException e) {
            // 资源访问阻止，被限流或被降级  Sentinel定义异常  流控规则，降级规则，热点参数规则。。。。  服务降级(降级规则)
            // 进行相应的处理操作
            logger.info("block!");
            return "被流控啦！";
        } catch (Exception ex) {
            // 若需要配置降级规则，需要通过这种方式记录业务异常  RuntimeException  服务降级  mock  feign:fallback
            Tracer.traceEntry(ex, entry);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }

        return null;
    }
```
- 引入依赖
- @Bean SentinelResourceAspect
- @SentinelResource
- 控制台配置流控规则

### spring cloud alibaba 整合（具体可看sentinel-springcloud-demo）
- 引入依赖
- 控制台配置流控规则
- @SentinelResource注解可以不用了，使用sentinel默认会使用spring mvc的mapping，然后在preHandler的时候去进行BlockException限流异常的处理

### sentinel配合控制台功能的测试 （看 sentinel_learn_01 的UserController）

### RestTemplate 整合sentinel （看sentinel-ribbon-demo）
1. 引入依赖
2. 配置类
```
    @Bean
    @LoadBalanced
    @SentinelRestTemplate(blockHandlerClass = RestSentinelExceptionUtil.class, blockHandler = "handlerBlockException",
            fallbackClass = RestSentinelExceptionUtil.class, fallback = "fallback")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
```
3. 异常处理 - 方法要static且参数和返回值不能错啊，具体可以看下**SentinelProtectInterceptor**的源码
```
public class RestSentinelExceptionUtil {
    public static ClientHttpResponse handlerBlockException(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution, BlockException ex) {
        R r = R.error(-1, "========> 被限流啦");
        try {
            return new SentinelClientHttpResponse(new ObjectMapper().writeValueAsString(r));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ClientHttpResponse fallback(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution, BlockException ex) {
        R r = R.error(-1, "========> 被熔断降级啦");
        try {
            return new SentinelClientHttpResponse(new ObjectMapper().writeValueAsString(r));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
```
4. 配置文件
```
# 这个其实可以不配，因为默认就是true的
# true开启sentinel对resttemplate的支持，false则关闭 默认true
resttemplate:
  sentinel:
    enabled: true

```

### feign 整合sentinel
1. 引入依赖（@EnableFeignClients 不要忘记加哦）
2. 配置文件 
```
# true开启sentinel对feign的支持，false则关闭 默认false
feign:
  sentinel:
    enabled: true
```
3. 限流异常处理
```
@FeignClient(value = "order-service", path = "/order",
         fallback = FeignFallBackOrderService.class
        // fallbackFactory = FeignFallBackOrderServiceFactory.class
        )
  public interface OrderService {
    @RequestMapping("/findOrderByUserId/{id}")
    String findOrderByUserId(@PathVariable Integer id);
}

@Component
public class FeignFallBackOrderService implements OrderService{
    @Override
    public String findOrderByUserId(Integer id) {
        return "===============> BlockException ----> 服务限流->降级";
    }
}
```
- fallback：指定的类型需要实现当前接口且**需要是个bean**
- fallbackFactory：指定的类型需要实现FallbackFactory且**需要是个bean**，泛型要传入当前接口类型
- fallback和fallbackFactory同时指定的话，优先使用fallback（看下SentinelInvocationHandler的源码就知道了）
- feign整合sentinel核心就是 SentinelFeign.Builder 和 SentinelInvocationHandler

### dubbo 整合sentinel（看文档吧）