package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.exception.InvalidLoginException;
import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.repositories.contracts.IUserRepository;
import com.brayanspv.auth.repositories.entities.UserEntity;
import com.brayanspv.auth.service.contracts.IJWTService;
import com.brayanspv.auth.service.contracts.IUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
@Log4j2
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final IJWTService jwtService;
    public UserService(IUserRepository userRepository, PasswordEncoder encoder, IJWTService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<SignUpResponse> signUp(SignUpRequest request) {
        log.info("Sign up request received");
        UserEntity userEntity = getUserEntity(request);

        return userRepository.save(userEntity)
                .map(savedEntity -> {
                    log.info("User saved");
                    SignUpResponse response = new SignUpResponse();
                    response.setUsername(savedEntity.getUsername());
                    return response;
                });
    }

    private UserEntity getUserEntity(SignUpRequest request) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(encoder.encode(request.getPassword()));
        userEntity.setUsername(request.getUsername());
        return userEntity;
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new InvalidLoginException("error no user")))
                .flatMap(userEntity -> {
                    if (!encoder.matches(request.getPassword(), userEntity.getPassword())) {
                        return Mono.error(new InvalidLoginException("error"));
                    }
                    String token = jwtService.generateToken(userEntity);
                    return Mono.just(new LoginResponse(token));
                });
    }
}
