package com.brayanspv.auth.service.contracts;

import com.brayanspv.auth.model.request.*;
import com.brayanspv.auth.model.response.GenericResponse;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.model.response.generic.ApiResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IUserService {

    Mono<SignUpResponse> signUp(SignUpRequest request);
    Mono<LoginResponse> login(LoginRequest request);
    Mono<GenericResponse> forgotPassword(ForgotPasswordRequest request);
    Mono<GenericResponse> verifyCode(VerifyCodeRequest request);
    Mono<GenericResponse> resetPassword(ResetPasswordRequest request);
}
