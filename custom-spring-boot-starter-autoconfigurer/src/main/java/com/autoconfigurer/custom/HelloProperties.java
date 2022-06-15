package com.autoconfigurer.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ClassName:HelloProperties
 * Package:com.autoconfigurer.custom
 * Description: 这个实际就是和配置文件绑定的javabean
 *
 * @Date:2022/6/9 16:47
 * @Author:qs@1.com
 */
@ConfigurationProperties("custom.hello")
public class HelloProperties {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
