package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.repositories.contracts.IUserRepository;
import com.brayanspv.auth.repositories.entities.UserEntity;
import com.brayanspv.auth.service.contracts.IUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder encoder;
    public UserService(IUserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public Mono<SignUpResponse> signUp(SignUpRequest request) {
        return Mono.just(new SignUpResponse()).flatMap(signUpResponse -> {
            log.info("Sign up request received");
            log.info("request received: {}",request.toString());
            UserEntity userEntity = getUserEntity(request);

            userRepository.save(userEntity);
            log.info("User saved");
            SignUpResponse response = new SignUpResponse();
            response.setUsername(userEntity.getUsername());
            return Mono.just(response);
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
        return null;
    }
}
