package com.brayanspv.auth.repositories.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private String email;
}
