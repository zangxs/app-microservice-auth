package com.brayanspv.auth.controller.implementations;

import com.brayanspv.auth.mocks.JSONMockConstants;
import com.brayanspv.auth.model.request.SignUpRequest;
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
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
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
        ApiResponse apiResponse = new ApiResponse();

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
}
