package com.zh.ch.bigdata.springboot.service;

import com.zh.ch.bigdata.springboot.config.ServiceConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.stereotype.Service;


import java.net.InetAddress;

@Service
public class LeaderElectionService implements LeaderLatchListener {

    public LeaderElectionService(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    private final CuratorFramework curatorFramework;

    @Override
    public void isLeader() {
        try {
            InetAddress address = InetAddress.getLocalHost();

            ServiceInstance<ServiceConfig> instance = ServiceInstance.<ServiceConfig>builder()
                    .address(address.getHostAddress())//ip地址：192.168.0.106
                    .port(8080)//port:8080
                    .name("userService") //服务的名称
                    .payload(new ServiceConfig("用户服务", 1))
                    .build();

            ServiceDiscovery<ServiceConfig> serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceConfig.class)
                    .client(curatorFramework)
                    //.serializer() //序列化方式
                    .basePath(ServiceConfig.REGISTER_ROOT_PATH)
                    .build();

            //服务注册
            serviceDiscovery.registerService(instance);
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void notLeader() {
        System.out.println("slave");
    }
}
