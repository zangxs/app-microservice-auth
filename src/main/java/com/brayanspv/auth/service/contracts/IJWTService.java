package com.brayanspv.auth.service.contracts;

import com.brayanspv.auth.repositories.entities.UserEntity;

public interface IJWTService {

    String generateToken(UserEntity user);
}
