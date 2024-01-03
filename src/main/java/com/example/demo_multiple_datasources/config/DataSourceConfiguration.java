package com.example.demo_multiple_datasources.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "com.example.demo_multiple_datasources.repository",
        "com.example.demo_multiple_datasources.domain"
})
@EnableJpaRepositories(basePackages = {
        "com.example.demo_multiple_datasources.repository",
        "com.example.demo_multiple_datasources.domain"
}, entityManagerFactoryRef = "dmdEntityManagerFactory", transactionManagerRef = "dmdTransactionManager")
@RequiredArgsConstructor
public class DataSourceConfiguration {

    private final DataSourceConfigurationProperties datasourceConfigurationProperties;
    
    @Bean(name = "dmdWriteDataSource")
    public DataSource writeDataSource() {
        return getDataSource(datasourceConfigurationProperties, DataSourceType.MASTER);
    }
    
    @Bean(name = "dmdReadDataSource")
    public DataSource readDataSource() {
        return getDataSource(datasourceConfigurationProperties, DataSourceType.SLAVE);
    }

    @Bean(name = "dmdRoutingDataSource")
    public DataSource routingDataSource(
            @Qualifier("dmdWriteDataSource") DataSource masterDataSource,
            @Qualifier("dmdReadDataSource") DataSource slaveDataSource) {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.MASTER, masterDataSource);
        dataSourceMap.put(DataSourceType.SLAVE, slaveDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    @Bean
    public DataSource dataSource(@Qualifier("dmdRoutingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
    
    @Bean(name = "dmdTransactionManager")
    public JpaTransactionManager transactionManager(
            @Qualifier("dmdEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean(name = "dmdConnectionProvider")
    public ConnectionProvider dmdConnectionProvider(@Qualifier("dmdRoutingDataSource") DataSource dataSource) {
        return new DMDConnectionProvider(dataSource);
    }
    
    @Bean(name = "dmdEntityManagerFactory")
    @ConditionalOnBean(name = "dmdConnectionProvider")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dmdConnectionProvider") ConnectionProvider connectionProvider) {

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        
        factoryBean.setPackagesToScan(
                "com.example.demo_multiple_datasources.repository",
                "com.example.demo_multiple_datasources.domain",
                "com.example.demo_multiple_datasources.service");

        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put(org.hibernate.cfg.Environment.CONNECTION_PROVIDER, connectionProvider);
        factoryBean.setJpaPropertyMap(propertiesMap);

        Properties properties = new Properties();
        properties.put(org.hibernate.cfg.Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, true);
        factoryBean.setJpaProperties(properties);
        
        return factoryBean;
    }
    

    public static DataSource getDataSource(
            DataSourceConfigurationProperties datasourceConfigurationProperties,
            DataSourceType dataSourceType) {

        HikariDataSource dataSource = new HikariDataSource();
        String jdbcUrl = dataSourceType == DataSourceType.MASTER
                ? datasourceConfigurationProperties.getMasterUrl()
                : datasourceConfigurationProperties.getSlaveUrl();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(datasourceConfigurationProperties.getUsername());
        dataSource.setPassword(datasourceConfigurationProperties.getPassword());
        dataSource.setDriverClassName(datasourceConfigurationProperties.getDriverClassName());

        dataSource.setPoolName("demo-multi-ds-" + dataSourceType + "-"
                        + datasourceConfigurationProperties.getPoolName());
        
        dataSource.setMaximumPoolSize(datasourceConfigurationProperties.getMaxPoolSize());
        dataSource.setMinimumIdle(datasourceConfigurationProperties.getMinIdle());
        dataSource.setConnectionTimeout(datasourceConfigurationProperties.getConnectionTimeout());
        dataSource.setIdleTimeout(datasourceConfigurationProperties.getIdleTimeout());
        dataSource.setAutoCommit(datasourceConfigurationProperties.isAutoCommit());
        dataSource.setMaxLifetime(datasourceConfigurationProperties.getMaxLifetime());
        dataSource.setLeakDetectionThreshold(datasourceConfigurationProperties.getLeakDetectionThreshold());
        
        return dataSource;
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setPrepareConnection(false);
        return hibernateJpaVendorAdapter;
    }
}
