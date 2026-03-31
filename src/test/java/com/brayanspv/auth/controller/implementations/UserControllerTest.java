package com.brayanspv.auth.controller.implementations;

import com.brayanspv.auth.mocks.JSONMockConstants;
import com.brayanspv.auth.model.request.ForgotPasswordRequest;
import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.ResetPasswordRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.request.VerifyCodeRequest;
import com.brayanspv.auth.model.response.GenericResponse;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.service.contracts.IUserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

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

    @Test
    void forgotPassword_success() {
        ForgotPasswordRequest request = gson.fromJson(JSONMockConstants.FORGOT_PASSWORD_REQUEST, ForgotPasswordRequest.class);

        Mockito.when(userService.forgotPassword(request)).thenReturn(Mono.just(new GenericResponse("Email sent successfully with id: email-123")));

        webTestClient.post()
                .uri("/forgot-password")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.message").isEqualTo("Email sent successfully with id: email-123");
        verify(userService, Mockito.times(1)).forgotPassword(request);
    }

    @Test
    void forgotPassword_invalidEmail() {
        ForgotPasswordRequest request = gson.fromJson(JSONMockConstants.FORGOT_PASSWORD_REQUEST, ForgotPasswordRequest.class);

        Mockito.when(userService.forgotPassword(request)).thenReturn(Mono.error(new RuntimeException("error invalid email")));

        webTestClient.post()
                .uri("/forgot-password")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
        verify(userService, Mockito.times(1)).forgotPassword(request);
    }

    @Test
    void verifyCode_success() {
        VerifyCodeRequest request = gson.fromJson(JSONMockConstants.VERIFY_CODE_REQUEST, VerifyCodeRequest.class);

        Mockito.when(userService.verifyCode(request)).thenReturn(Mono.just(new GenericResponse("code verified successfully")));

        webTestClient.post()
                .uri("/verify-code")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.message").isEqualTo("code verified successfully");
        verify(userService, Mockito.times(1)).verifyCode(request);
    }

    @Test
    void verifyCode_invalidCode() {
        VerifyCodeRequest request = gson.fromJson(JSONMockConstants.VERIFY_CODE_REQUEST, VerifyCodeRequest.class);

        Mockito.when(userService.verifyCode(request)).thenReturn(Mono.error(new RuntimeException("codigo invalido")));

        webTestClient.post()
                .uri("/verify-code")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
        verify(userService, Mockito.times(1)).verifyCode(request);
    }

    @Test
    void verifyCode_tokenExpired() {
        VerifyCodeRequest request = gson.fromJson(JSONMockConstants.VERIFY_CODE_REQUEST, VerifyCodeRequest.class);

        Mockito.when(userService.verifyCode(request)).thenReturn(Mono.error(new RuntimeException("token expired")));

        webTestClient.post()
                .uri("/verify-code")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
        verify(userService, Mockito.times(1)).verifyCode(request);
    }

    @Test
    void resetPassword_success() {
        ResetPasswordRequest request = gson.fromJson(JSONMockConstants.RESET_PASSWORD_REQUEST, ResetPasswordRequest.class);

        Mockito.when(userService.resetPassword(request)).thenReturn(Mono.just(new GenericResponse("password reset successfully")));

        webTestClient.post()
                .uri("/reset-password")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data.message").isEqualTo("password reset successfully");
        verify(userService, Mockito.times(1)).resetPassword(request);
    }

    @Test
    void resetPassword_error() {
        ResetPasswordRequest request = gson.fromJson(JSONMockConstants.RESET_PASSWORD_REQUEST, ResetPasswordRequest.class);

        Mockito.when(userService.resetPassword(request)).thenReturn(Mono.error(new RuntimeException("reset error")));

        webTestClient.post()
                .uri("/reset-password")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
        verify(userService, Mockito.times(1)).resetPassword(request);
    }
}
