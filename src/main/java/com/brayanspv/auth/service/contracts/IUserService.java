package com.brayanspv.auth.service.contracts;

import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import reactor.core.publisher.Mono;

public interface IUserService {

    Mono<SignUpResponse> signUp(SignUpRequest request);
    Mono<LoginResponse> login(LoginRequest request);
}
