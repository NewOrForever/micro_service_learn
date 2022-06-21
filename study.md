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