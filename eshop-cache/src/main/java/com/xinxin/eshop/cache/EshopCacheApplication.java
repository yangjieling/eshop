package com.xinxin.eshop.cache;

import com.xinxin.eshop.cache.listener.InitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class EshopCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopCacheApplication.class, args);
    }

    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean(){
        ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
        servletListenerRegistrationBean.setListener(new InitListener());
        return servletListenerRegistrationBean;
    }

    @Bean
    public JedisCluster JedisClusterFactory(){
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("192.168.56.103", 7004));
        jedisClusterNodes.add(new HostAndPort("192.168.56.104", 7005));
        jedisClusterNodes.add(new HostAndPort("192.168.56.103", 7003));
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
        return  jedisCluster;
    }
}