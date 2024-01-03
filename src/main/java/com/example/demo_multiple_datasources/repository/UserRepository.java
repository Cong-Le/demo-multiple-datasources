package com.example.demo_multiple_datasources.repository;

import com.example.demo_multiple_datasources.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String>  {

    @Query(value = "SELECT * FROM user WHERE deleted = 0", nativeQuery = true)
    List<User> findAllUsers();
    
    @Query(value = "SELECT * FROM user " +
            "WHERE id = :userId AND deleted = 0", nativeQuery = true)
    List<User> findUserById(@Param("userId") String userId);
}
