package com.eyas.framework.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class CacheConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Configuration
    @ConditionalOnClass({Redisson.class})
    @ConditionalOnExpression("'${spring.redis.mode}'=='single' or '${spring.redis.mode}'=='cluster' or '${spring.redis.mode}'=='sentinel'")
    protected class RedissonSingleClientConfiguration {

        /**
         * 单机模式 redisson 客户端
         */

        @Bean
        @ConditionalOnProperty(name="spring.redis.mode", havingValue="single")
        public RedissonClient redissonSingle() {
            Config config = new Config();
            String node = redisProperties.getSingle().getAddress();
            node = node.startsWith("redis://") ? node : "redis://" + node;
            SingleServerConfig serverConfig = config.useSingleServer()
                    .setAddress(node)
                    .setTimeout(redisProperties.getJedis().getConnTimeout())
                    .setConnectionPoolSize(redisProperties.getJedis().getSize())
                    .setConnectionMinimumIdleSize(redisProperties.getJedis().getMinIdle());
            if(!StringUtils.isEmpty(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
            return Redisson.create(config);
        }

        /**
         * 集群模式的 redisson 客户端
         *
         * @return
         */
        @Bean
        @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "cluster")
        public RedissonClient redissonCluster() {
            System.out.println("cluster redisProperties:" + redisProperties.getCluster());

            Config config = new Config();
            String[] nodes = redisProperties.getCluster().getNodes().split(",");
            List<String> newNodes = new ArrayList<>(nodes.length);
            Arrays.stream(nodes).forEach((index) -> newNodes.add(
                    index.startsWith("redis://") ? index : "redis://" + index)
            );

            ClusterServersConfig serverConfig = config.useClusterServers()
                    .addNodeAddress(newNodes.toArray(new String[0])
                    ).setScanInterval(
                            redisProperties.getCluster().getScanInterval()
                    ).setIdleConnectionTimeout(
                            redisProperties.getJedis().getSoTimeout()
                    ).setConnectTimeout(
                            redisProperties.getJedis().getConnTimeout()
                    ).setRetryAttempts(
                            redisProperties.getCluster().getRetryAttempts()
                    ).setRetryInterval(
                            redisProperties.getCluster().getRetryInterval()
                    ).setMasterConnectionPoolSize(
                            redisProperties.getCluster().getMasterConnectionPoolSize()
                    ).setSlaveConnectionPoolSize(
                            redisProperties.getCluster().getSlaveConnectionPoolSize()
                    ).setTimeout(
                            redisProperties.getTimeout()
                    );

            if (!StringUtils.isEmpty(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
            return Redisson.create(config);
        }


        /**
         * 哨兵模式 redisson 客户端
         * @return
         */
        @Bean
        @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "sentinel")
        public RedissonClient redissonSentinel() {
            System.out.println("sentinel redisProperties:" + redisProperties.getSentinel());
            Config config = new Config();
            String[] nodes = redisProperties.getSentinel().getNodes().split(",");
            List<String> newNodes = new ArrayList<>(nodes.length);
            Arrays.stream(nodes).forEach((index) -> newNodes.add(
                    index.startsWith("redis://") ? index : "redis://" + index)
            );

            SentinelServersConfig serverConfig = config.useSentinelServers()
                    .addSentinelAddress(newNodes.toArray(new String[0]))
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .setReadMode(ReadMode.SLAVE)
                    .setTimeout(redisProperties.getTimeout())
                    .setMasterConnectionPoolSize(redisProperties.getJedis().getSize())
                    .setSlaveConnectionPoolSize(redisProperties.getJedis().getSize());

            if (!StringUtils.isEmpty(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
            return Redisson.create(config);
        }
    }
}
