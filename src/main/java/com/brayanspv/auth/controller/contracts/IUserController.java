package com.brayanspv.auth.controller.contracts;

import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.model.response.generic.ApiResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IUserController {

    Mono<ResponseEntity<ApiResponse>> signUp(SignUpRequest request);
}
