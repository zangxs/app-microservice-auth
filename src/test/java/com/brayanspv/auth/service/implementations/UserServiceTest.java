package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.exception.InvalidLoginException;
import com.brayanspv.auth.mocks.JSONMockConstants;
import com.brayanspv.auth.model.request.ForgotPasswordRequest;
import com.brayanspv.auth.model.request.LoginRequest;
import com.brayanspv.auth.model.request.ResetPasswordRequest;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.request.VerifyCodeRequest;
import com.brayanspv.auth.model.response.GenericResponse;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SendEmailResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.repositories.contracts.IPasswordResetTokenRepository;
import com.brayanspv.auth.repositories.contracts.IUserRepository;
import com.brayanspv.auth.repositories.entities.PasswordResetToken;
import com.brayanspv.auth.repositories.entities.UserEntity;
import com.brayanspv.auth.service.contracts.IJWTService;
import com.brayanspv.auth.service.contracts.IMailService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IJWTService jwtService;
    @Mock
    private IMailService mailService;
    @Mock
    private IPasswordResetTokenRepository passwordResetTokenRepository;

    private final Gson gson = new Gson();

    @Test
    void signUpTestOk() {
        SignUpRequest request = gson.fromJson(JSONMockConstants.SIGNUP_REQUEST, SignUpRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setId(1L);
        userEntity.setPassword(request.getPassword());
        userEntity.setUsername(request.getUsername());
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.signUp(request))
                .expectNextMatches(signUpResponse -> signUpResponse.getUsername().equals(request.getUsername()))
                .verifyComplete();
    }

    @Test
    void loginTestOk() {
        LoginRequest request = gson.fromJson(JSONMockConstants.LOGIN_REQUEST, LoginRequest.class);

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(request.getPassword());
        userEntity.setUsername(request.getUsername());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Mono.just(userEntity));
        when(passwordEncoder.matches(request.getPassword(), request.getPassword())).thenReturn(true);
        when(jwtService.generateToken(userEntity)).thenReturn("fake-jwt-token");

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.login(request))
                .expectNextMatches(loginResponse -> Objects.nonNull(loginResponse.getJwtToken()))
                .verifyComplete();
    }

    @Test
    void loginTest_userNotFound() {
        LoginRequest request = gson.fromJson(JSONMockConstants.LOGIN_REQUEST, LoginRequest.class);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Mono.empty());

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.login(request))
                .expectError(InvalidLoginException.class)
                .verify();
    }

    @Test
    void loginTest_wrongPassword() {
        LoginRequest request = gson.fromJson(JSONMockConstants.LOGIN_REQUEST, LoginRequest.class);

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("encoded-password");
        userEntity.setUsername(request.getUsername());
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Mono.just(userEntity));
        when(passwordEncoder.matches(request.getPassword(), "encoded-password")).thenReturn(false);

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.login(request))
                .expectError(InvalidLoginException.class)
                .verify();
    }

    @Test
    void forgotPassword_success() {
        ForgotPasswordRequest request = gson.fromJson(JSONMockConstants.FORGOT_PASSWORD_REQUEST, ForgotPasswordRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(request.email());
        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(mailService.sendEmail(any())).thenReturn(Mono.just(new SendEmailResponse("email-id-12345", request.email(), "123456")));
        when(passwordResetTokenRepository.save(any())).thenReturn(Mono.just(new PasswordResetToken()));

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.forgotPassword(request))
                .expectNextMatches(response ->
                        response.message().contains("Email sent successfully with id: email-id-12345")
                )
                .verifyComplete();
    }

    @Test
    void forgotPassword_emailServiceFails() {
        ForgotPasswordRequest request = gson.fromJson(JSONMockConstants.FORGOT_PASSWORD_REQUEST, ForgotPasswordRequest.class);
        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(new UserEntity()));
        when(mailService.sendEmail(any())).thenReturn(Mono.error(new RuntimeException("Email service error")));

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.forgotPassword(request))
                .expectErrorMatches(error -> error.getMessage().contains("Email service error"))
                .verify();
    }

    @Test
    void forgotPassword_userNotFound() {
        ForgotPasswordRequest request = gson.fromJson(JSONMockConstants.FORGOT_PASSWORD_REQUEST, ForgotPasswordRequest.class);
        when(userRepository.findByEmail(request.email())).thenReturn(Mono.empty());

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.forgotPassword(request))
                .expectErrorMatches(error -> error.getMessage().contains("error invalid email"))
                .verify();
    }

    @Test
    void verifyCode_success() {
        VerifyCodeRequest request = gson.fromJson(JSONMockConstants.VERIFY_CODE_REQUEST, VerifyCodeRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(request.email());

        PasswordResetToken token = new PasswordResetToken();
        token.setId(java.util.UUID.randomUUID());
        token.setUserId(1L);
        token.setCode(request.code());
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(10));

        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.findByUserIdAndCode(userEntity.getId(), request.code())).thenReturn(Mono.just(token));
        when(passwordResetTokenRepository.save(any())).thenReturn(Mono.just(token));

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.verifyCode(request))
                .expectNextMatches(response -> response.message().contains("code verified successfully"))
                .verifyComplete();
    }

    @Test
    void verifyCode_userNotFound() {
        VerifyCodeRequest request = gson.fromJson(JSONMockConstants.VERIFY_CODE_REQUEST, VerifyCodeRequest.class);
        when(userRepository.findByEmail(request.email())).thenReturn(Mono.empty());

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.verifyCode(request))
                .expectErrorMatches(error -> error.getMessage().contains("error invalid email"))
                .verify();
    }

    @Test
    void verifyCode_codeNotFound() {
        VerifyCodeRequest request = gson.fromJson(JSONMockConstants.VERIFY_CODE_REQUEST, VerifyCodeRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.findByUserIdAndCode(userEntity.getId(), request.code())).thenReturn(Mono.empty());

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.verifyCode(request))
                .expectErrorMatches(error -> error.getMessage().contains("code invalid"))
                .verify();
    }

    @Test
    void verifyCode_tokenAlreadyUsed() {
        VerifyCodeRequest request = gson.fromJson(JSONMockConstants.VERIFY_CODE_REQUEST, VerifyCodeRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        PasswordResetToken token = new PasswordResetToken();
        token.setId(java.util.UUID.randomUUID());
        token.setUserId(1L);
        token.setCode(request.code());
        token.setUsed(true);
        token.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(10));

        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.findByUserIdAndCode(userEntity.getId(), request.code())).thenReturn(Mono.just(token));

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.verifyCode(request))
                .expectErrorMatches(error -> error.getMessage().contains("token invalid"))
                .verify();
    }

    @Test
    void verifyCode_tokenExpired() {
        VerifyCodeRequest request = gson.fromJson(JSONMockConstants.VERIFY_CODE_REQUEST, VerifyCodeRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        PasswordResetToken token = new PasswordResetToken();
        token.setId(java.util.UUID.randomUUID());
        token.setUserId(1L);
        token.setCode(request.code());
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));

        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.findByUserIdAndCode(userEntity.getId(), request.code())).thenReturn(Mono.just(token));

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.verifyCode(request))
                .expectErrorMatches(error -> error.getMessage().contains("token expired"))
                .verify();
    }

    @Test
    void resetPassword_success() {
        ResetPasswordRequest request = gson.fromJson(JSONMockConstants.RESET_PASSWORD_REQUEST, ResetPasswordRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(request.email());
        userEntity.setPassword("old-password");

        PasswordResetToken token = new PasswordResetToken();
        token.setId(java.util.UUID.randomUUID());
        token.setUserId(1L);
        token.setCode(request.code());
        token.setUsed(true);
        token.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(10));

        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.findByUserIdAndCode(userEntity.getId(), request.code())).thenReturn(Mono.just(token));
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-new-password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.delete(any(PasswordResetToken.class))).thenReturn(Mono.empty());

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.resetPassword(request))
                .expectNextMatches(response -> response.message().contains("password reset successfully"))
                .verifyComplete();
    }

    @Test
    void resetPassword_userNotFound() {
        ResetPasswordRequest request = gson.fromJson(JSONMockConstants.RESET_PASSWORD_REQUEST, ResetPasswordRequest.class);
        when(userRepository.findByEmail(request.email())).thenReturn(Mono.empty());

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.resetPassword(request))
                .expectErrorMatches(error -> error.getMessage().contains("error invalid email"))
                .verify();
    }

    @Test
    void resetPassword_codeNotFound() {
        ResetPasswordRequest request = gson.fromJson(JSONMockConstants.RESET_PASSWORD_REQUEST, ResetPasswordRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.findByUserIdAndCode(userEntity.getId(), request.code())).thenReturn(Mono.empty());

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.resetPassword(request))
                .expectErrorMatches(error -> error.getMessage().contains("code invalid"))
                .verify();
    }

    @Test
    void resetPassword_codeNotVerified() {
        ResetPasswordRequest request = gson.fromJson(JSONMockConstants.RESET_PASSWORD_REQUEST, ResetPasswordRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        PasswordResetToken token = new PasswordResetToken();
        token.setId(java.util.UUID.randomUUID());
        token.setUserId(1L);
        token.setCode(request.code());
        token.setUsed(false);
        token.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(10));

        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.findByUserIdAndCode(userEntity.getId(), request.code())).thenReturn(Mono.just(token));

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.resetPassword(request))
                .expectErrorMatches(error -> error.getMessage().contains("code not verified"))
                .verify();
    }

    @Test
    void resetPassword_tokenExpired() {
        ResetPasswordRequest request = gson.fromJson(JSONMockConstants.RESET_PASSWORD_REQUEST, ResetPasswordRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        PasswordResetToken token = new PasswordResetToken();
        token.setId(java.util.UUID.randomUUID());
        token.setUserId(1L);
        token.setCode(request.code());
        token.setUsed(true);
        token.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));

        when(userRepository.findByEmail(request.email())).thenReturn(Mono.just(userEntity));
        when(passwordResetTokenRepository.findByUserIdAndCode(userEntity.getId(), request.code())).thenReturn(Mono.just(token));

        UserService userService = new UserService(userRepository, passwordEncoder, jwtService, mailService, passwordResetTokenRepository);
        StepVerifier.create(userService.resetPassword(request))
                .expectErrorMatches(error -> error.getMessage().contains("token expired"))
                .verify();
    }
}
