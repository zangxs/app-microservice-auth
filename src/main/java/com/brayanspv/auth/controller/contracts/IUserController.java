package com.brayanspv.auth.controller.contracts;

import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.SignUpResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IUserController {

    Mono<ResponseEntity<SignUpResponse>> signUp(SignUpRequest request);
}
