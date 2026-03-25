package com.brayanspv.auth.repositories.entities;

import lombok.Data;

@Data
public class UserEntity {

    private Long id;
    private String username;
    private String password;
    private String email;
}
