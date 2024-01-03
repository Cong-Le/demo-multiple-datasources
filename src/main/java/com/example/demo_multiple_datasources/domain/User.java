package com.example.demo_multiple_datasources.domain;

import com.example.demo_multiple_datasources.model.dto.UserDTO;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(name = "UserResult", classes = {
        @ConstructorResult(targetClass = UserDTO.class, columns = {
                @ColumnResult(name = "id", type = String.class),
                @ColumnResult(name = "email", type = String.class),
                @ColumnResult(name = "password", type = String.class),
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "address", type = String.class),
                @ColumnResult(name = "phone_number", type = String.class)
        })
})
public class User extends AbstractAuditingEntity {
    
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false, length = 50)
    private String id;
    
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "deleted")
    private boolean deleted;
}
