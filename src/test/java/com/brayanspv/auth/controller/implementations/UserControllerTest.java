package com.brayanspv.auth.controller.implementations;

import com.brayanspv.auth.mocks.JSONMockConstants;
import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.model.response.generic.ApiResponse;
import com.brayanspv.auth.service.contracts.IUserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private WebTestClient webTestClient;

    private Gson gson = new Gson();

    @BeforeEach
    public void setUp() {
        this.webTestClient = WebTestClient.bindToController(userController).build();
    }

    @Test
    void createUser() {
        SignUpRequest signUpRequest = gson.fromJson(JSONMockConstants.SIGNUP_REQUEST, SignUpRequest.class);

        Mockito.when(userService.signUp(signUpRequest)).thenReturn(Mono.just(SignUpResponse.builder().username("brayan").build()));

        webTestClient.post()
                .uri("/signUp")
                .contentType(APPLICATION_JSON)
                .bodyValue(signUpRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data").exists()
                .jsonPath("$.data.username").isEqualTo("brayan");
        verify(userService, Mockito.times(1)).signUp(signUpRequest);
    }

    @Test
    void createUserDBError() {
        SignUpRequest signUpRequest = gson.fromJson(JSONMockConstants.SIGNUP_REQUEST, SignUpRequest.class);

        Mockito.when(userService.signUp(signUpRequest)).thenThrow(new DataIntegrityViolationException("db error"));

        webTestClient.post()
                .uri("/signUp")
                .contentType(APPLICATION_JSON)
                .bodyValue(signUpRequest)
                .exchange()
                .expectStatus().is5xxServerError();
        verify(userService, Mockito.times(1)).signUp(signUpRequest);
    }

    @Test
    void createUserInvalidParameters() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("brayan");
        signUpRequest.setPassword("");

        webTestClient.post()
                .uri("/signUp")
                .contentType(APPLICATION_JSON)
                .bodyValue(signUpRequest)
                .exchange()
                .expectStatus().isBadRequest();
        verifyNoInteractions(userService);
    }

    @Test
    void loginOk() {
        LoginRequest loginRequest = gson.fromJson(JSONMockConstants.LOGIN_REQUEST, LoginRequest.class);

        Mockito.when(userService.login(loginRequest)).thenReturn(Mono.just(LoginResponse.builder().jwtToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJkYW5pZWxAZ21haWwuY29tIiwiaWF0IjoxNzc0NTc2NjYwLCJleHAiOjE3NzQ2NjMwNjB9.oCM1ae15tvyuOESAVrJKz9um323TDHgb4naBjRclA_U").build()));

        webTestClient.post()
                .uri("/login")
                .contentType(APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data").exists()
                .jsonPath("$.data.jwtToken").isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJkYW5pZWxAZ21haWwuY29tIiwiaWF0IjoxNzc0NTc2NjYwLCJleHAiOjE3NzQ2NjMwNjB9.oCM1ae15tvyuOESAVrJKz9um323TDHgb4naBjRclA_U");
        verify(userService, Mockito.times(1)).login(loginRequest);

    }
}
