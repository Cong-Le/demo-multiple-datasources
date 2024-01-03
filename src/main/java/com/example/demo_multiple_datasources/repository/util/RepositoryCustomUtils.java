package com.example.demo_multiple_datasources.repository.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RepositoryCustomUtils {

    @SuppressWarnings("unchecked")
    public <T> List<T> getResultList(String sql, String resultSetMappingName, Map<String, Object> parameters,
                                     JpaTransactionManager transactionManager) {
        try (EntityManager entityManager = Objects.requireNonNull(transactionManager.getEntityManagerFactory()).createEntityManager()) {
            Query query = createQuery(entityManager, sql, resultSetMappingName, parameters);
            return query.getResultList();
        }
    }

    private Query createQuery(EntityManager entityManager, String sql, String resultSetMappingName,
                              Map<String, Object> parameters) {
        Query query;
        if (StringUtils.isEmpty(resultSetMappingName)) {
            query = entityManager.createNativeQuery(sql);
        } else {
            query = entityManager.createNativeQuery(sql, resultSetMappingName);
        }

        if (parameters == null) {
            return query;
        }

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }
    
}
