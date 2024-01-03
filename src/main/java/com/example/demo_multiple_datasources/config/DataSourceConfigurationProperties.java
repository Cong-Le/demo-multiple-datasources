package com.example.demo_multiple_datasources.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("demo-multi-ds.datasource")
@RequiredArgsConstructor
public class DataSourceConfigurationProperties {
    
    private String masterUrl;
    
    private String slaveUrl;
    
    private String username;
    
    private String password;
    
    private String driverClassName;

    private long connectionTimeout;

    private int maxPoolSize;

    private long idleTimeout;

    private int minIdle;

    private String poolName;

    private boolean autoCommit;

    private long maxLifetime;

    private long leakDetectionThreshold = 0;
}
