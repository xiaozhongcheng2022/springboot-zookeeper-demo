package com.zh.ch.bigdata.springboot.config;

import com.zh.ch.bigdata.springboot.service.LeaderElectionService;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ZookeeperConfig {

    @Resource
    private ZooKeeperProperties zooKeeperProperties;

    private static String CURATOR = "/curator";

    @Bean
    public CuratorFramework curatorFramework() {
        /*
            方式二：
             * connectionString zk地址
             * sessionTimeoutMs 会话超时时间
             * connectionTimeoutMs 连接超时时间
             * namespace 每个curatorFramework 可以设置一个独立的命名空间,之后操作都是基于该命名空间，比如操作 /user/message 其实操作的是/curator/user/message
             * retryPolicy 重试策略
         */


        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zooKeeperProperties.getBaseSleepTimeMs(), zooKeeperProperties.getMaxRetries());
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zooKeeperProperties.getConnectString())
                .sessionTimeoutMs(zooKeeperProperties.getSessionTimeoutMs())
                .connectionTimeoutMs(zooKeeperProperties.getConnectionTimeoutMs())
                .namespace(zooKeeperProperties.getNamespace())
                .retryPolicy(retryPolicy)
                .build();

        curatorFramework.getConnectionStateListenable().addListener((client, newState) -> {
            if(newState == ConnectionState.CONNECTED){
                System.out.println("连接成功！");
            }
        });

        curatorFramework.start();

        LeaderLatch leaderLatch = new LeaderLatch(curatorFramework, CURATOR);
        leaderLatch.addListener(new LeaderElectionService(curatorFramework));

        try {
            leaderLatch.start();
            System.out.println(leaderLatch.getLeader());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return curatorFramework;
    }


}
