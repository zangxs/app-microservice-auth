package com.brayanspv.auth.controller.implementations;

import com.brayanspv.auth.controller.contracts.IUserController;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.service.contracts.IUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Log4j2
public class UserController implements IUserController {

    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Override
    @PostMapping(path = "signUp")
    public Mono<ResponseEntity<SignUpResponse>> signUp(@RequestBody SignUpRequest request) {
        log.info("request received: {}",request.toString());
        return userService.signUp(request)
                .map(signUpResponse -> ResponseEntity.ok(signUpResponse))
                .onErrorResume(error -> Mono.error(error));
    }
}
