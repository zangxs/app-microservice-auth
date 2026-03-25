package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.helper.PasswordHelper;
import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.repositories.contracts.IUserRepository;
import com.brayanspv.auth.repositories.entities.UserEntity;
import com.brayanspv.auth.service.contracts.IUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<SignUpResponse> signUp(SignUpRequest request) {
        return Mono.just(new SignUpResponse()).flatMap(signUpResponse -> {
            log.info("Sign up request received");

            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(request.getEmail());
            userEntity.setPassword(PasswordHelper.createPassword(request.getPassword()));
            userEntity.setUsername(request.getUsername());

            log.info("password: {}", request.getPassword());
            log.info("HashedPassword: {}", userEntity.getPassword());

            userRepository.save(userEntity);
            log.info("User saved");
            log.info("user: {}", userEntity);
            SignUpResponse response = new SignUpResponse();
            response.setUsername(userEntity.getUsername());
            return Mono.just(response);
        });
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return null;
    }
}
