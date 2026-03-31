package com.brayanspv.auth.repositories.contracts;

import com.brayanspv.auth.repositories.entities.PasswordResetToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPasswordResetTokenRepository extends ReactiveCrudRepository<PasswordResetToken, Long> {
}
