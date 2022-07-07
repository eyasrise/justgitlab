package com.eyas.framework.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.config.Config;

//@Configuration
public class RedissonConfig {

    @Value("${spring.redis.clusters}")
    private  String cluster;
    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.host}")
    private  String host;
    @Value("${spring.redis.port}")
    private  String port;

    @Bean
    public Redisson getRedisson() {
        String[] nodes = cluster.split(",");
        //redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = "redis://" + nodes[i];
        }
        RedissonClient redisson = null;
        Config config = new Config();
        // cluster配置
//        config.useClusterServers() //这是用的集群server
//                .setScanInterval(2000) //设置集群状态扫描时间
//                .addNodeAddress(nodes)
//                .setPassword(password);
        // standalone
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password).setDatabase(0);
        redisson = Redisson.create(config);

        //可通过打印redisson.getConfig().toJSON().toString()来检测是否配置成功
        return (Redisson) redisson;
    }
}
