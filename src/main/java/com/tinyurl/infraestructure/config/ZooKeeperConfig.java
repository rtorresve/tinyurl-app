package com.tinyurl.infraestructure.config;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ZooKeeperConfig {

    @Value("${zookeeper.connection}")
    private String zookeeperConnection;
    @Value("${feature.zookeeper.enabled}")
    private boolean zookeeperEnabled;
    @Value("${zookeeper.sessionTimeout}")
    private int sessionTimeout;

    @Bean
    public ZooKeeper zooKeeper() throws IOException {
        if (!zookeeperEnabled) {
            return null;
        }
        // Disable SASL authentication
        System.setProperty("zookeeper.sasl.client", "false");
        return new ZooKeeper(zookeeperConnection, sessionTimeout, null);
    }
}