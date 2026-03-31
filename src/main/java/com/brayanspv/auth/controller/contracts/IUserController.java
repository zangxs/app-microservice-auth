package com.brayanspv.auth.controller.contracts;

import com.brayanspv.auth.model.request.*;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.model.response.generic.ApiResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IUserController {

    Mono<ResponseEntity<ApiResponse>> signUp(SignUpRequest request);
    Mono<ResponseEntity<ApiResponse>> login(LoginRequest request);
    Mono<ResponseEntity<ApiResponse>> forgotPassword(ForgotPasswordRequest request);
    Mono<ResponseEntity<ApiResponse>> verifyCode(VerifyCodeRequest request);
    Mono<ResponseEntity<ApiResponse>> resetPassword(ResetPasswordRequest request);
}
