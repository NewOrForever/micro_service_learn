package com.example.ribbon.demo;

import com.google.common.collect.Lists;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import com.netflix.loadbalancer.reactive.ServerOperation;
import rx.Observable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * ClassName:RibboDemo
 * Package:com.example.ribbon.demo
 * Description:
 *
 * @Date:2022/6/16 16:07
 * @Author:qs@1.com
 */
public class RibboDemo {
    public static void main(String[] args) {
        // 服务列表
        List<Server> serverList = Lists.newArrayList(
                new Server("localhost", 8085),
                new Server("localhost", 8086),
                new Server("localhost", 8087)
        );

        // 构建负载实例
        ILoadBalancer loadBalancer = LoadBalancerBuilder.newBuilder().
                buildFixedServerListLoadBalancer(serverList);

        for (int i = 0; i < 5; i++) {
            String result = LoadBalancerCommand.<String>builder().
                    withLoadBalancer(loadBalancer)
                    .build().submit(new ServerOperation<String>() {
                        @Override
                        public Observable<String> call(Server server) {
                            String addr = String.format("http://%s:%s/echo/%s", server.getHost(), server.getPort(), "Ribbon");
                            System.out.println("调用地址： ==========>" + addr);
                            URL url = null;
                            try {
                                url = new URL(addr);
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET");
                                conn.connect();
                                InputStream in = conn.getInputStream();
                                byte[] data = new byte[in.available()];
                                in.read(data);
                                return Observable.just(new String(data));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }).toBlocking().first();
        }

    }
}
