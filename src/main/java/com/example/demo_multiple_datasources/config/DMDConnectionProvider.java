package com.example.demo_multiple_datasources.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class DMDConnectionProvider implements ConnectionProvider {
    
    private static final long serialVersionUID = 691062681827489804L;
    
    private final transient DataSource dataSource;
    
    
    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }
    
    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }
    
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return DataSource.class.isAssignableFrom(unwrapType);
    }
    
    @Override
    @SuppressWarnings({
        "unchecked"
    })
    public <T> T unwrap(Class<T> unwrapType) {
        if (DataSource.class.isAssignableFrom(unwrapType)) {
            return (T) dataSource;
        } else {
            throw new UnknownUnwrapTypeException(unwrapType);
        }
    }
}
