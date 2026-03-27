package com.brayanspv.auth.repositories.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "au_user")
public class UserEntity {
    @Id
    private Long id;
    private String username;
    private String password;
    private String email;
}
