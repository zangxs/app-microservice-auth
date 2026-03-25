package com.brayanspv.auth.repositories.contracts;

import com.brayanspv.auth.repositories.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {
}
