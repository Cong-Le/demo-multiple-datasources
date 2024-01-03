package com.example.demo_multiple_datasources.repository.impl;

import com.example.demo_multiple_datasources.model.dto.UserDTO;
import com.example.demo_multiple_datasources.repository.UserRepositoryCustom;
import com.example.demo_multiple_datasources.repository.util.RepositoryCustomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryCustomImpl extends RepositoryCustomUtils implements UserRepositoryCustom {

    private final JpaTransactionManager transactionManager;

    public UserRepositoryCustomImpl(@Qualifier("dmdTransactionManager") JpaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    @Override
    public List<UserDTO> findAllUsers(String userId) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM user WHERE deleted = 0");
        
        if (StringUtils.isNotBlank(userId)) {
            sql.append(" AND id = :userId");
            parameters.put("userId", userId);
        }
        
        return getResultList(sql.toString(), "UserResult", parameters, transactionManager);
    }
    
}
