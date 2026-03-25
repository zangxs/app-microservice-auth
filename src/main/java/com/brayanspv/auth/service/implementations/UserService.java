package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.repositories.entities.UserEntity;
import com.brayanspv.auth.service.contracts.IUserService;
import reactor.core.publisher.Mono;

public class UserService implements IUserService {
    @Override
    public Mono<SignUpResponse> signUp(SignUpRequest request) {
        return Mono.just(new SignUpResponse()).flatMap(signUpResponse -> {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(request.getEmail());
            userEntity.setPassword(PasswordHelper.createPassword(request.getPassword()));
            userEntity.setUsername(request.getUsername());
        });
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return null;
    }
}
