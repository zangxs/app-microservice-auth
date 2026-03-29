package com.brayanspv.auth.controller.implementations;

import com.brayanspv.auth.controller.contracts.IUserController;
import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.model.response.generic.ApiResponse;
import com.brayanspv.auth.service.contracts.IUserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@RestController
@Log4j2
public class UserController implements IUserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Override
    @PostMapping(path = "signUp")
    public Mono<ResponseEntity<ApiResponse>> signUp(@RequestBody @Valid SignUpRequest request) {
        log.info("request received: {}", request.toString());
        return userService.signUp(request)
                .map(signUpResponse -> ResponseEntity.ok(ApiResponse.builder()
                        .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                        .code(200)
                        .data(signUpResponse).build())
                )
                .onErrorResume(Mono::error);
    }

    @Override
    @PostMapping(path = "login")
    public Mono<ResponseEntity<ApiResponse>> login(@RequestBody @Valid LoginRequest request) {
        return userService.login(request)
                .map(loginResponse -> ResponseEntity.ok(ApiResponse.builder()
                        .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                        .code(200)
                        .data(loginResponse).build())
                )
                .onErrorResume(Mono::error).log();
    }
}
