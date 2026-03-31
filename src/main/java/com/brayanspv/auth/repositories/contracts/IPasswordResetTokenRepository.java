package com.brayanspv.auth.repositories.contracts;

import com.brayanspv.auth.repositories.entities.PasswordResetToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IPasswordResetTokenRepository extends ReactiveCrudRepository<PasswordResetToken, Long> {

    Mono<PasswordResetToken> findByUserIdAndCode(Long userId, String code);
}
