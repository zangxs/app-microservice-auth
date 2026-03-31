package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.exception.InvalidEmailException;
import com.brayanspv.auth.component.exception.InvalidLoginException;
import com.brayanspv.auth.component.exception.SendEmailException;
import com.brayanspv.auth.model.request.*;
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
import com.brayanspv.auth.service.contracts.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
@Log4j2
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final IJWTService jwtService;
    private final IMailService mailService;
    private final IPasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public Mono<SignUpResponse> signUp(SignUpRequest request) {
        log.info("Sign up request received");
        UserEntity userEntity = getUserEntity(request);

        return userRepository.save(userEntity)
                .map(savedEntity -> {
                    log.info("User saved");
                    SignUpResponse response = new SignUpResponse();
                    response.setUsername(savedEntity.getUsername());
                    return response;
                });
    }

    private UserEntity getUserEntity(SignUpRequest request) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(encoder.encode(request.getPassword()));
        userEntity.setUsername(request.getUsername());
        return userEntity;
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new InvalidLoginException("error no user")))
                .flatMap(userEntity -> {
                    if (!encoder.matches(request.getPassword(), userEntity.getPassword())) {
                        return Mono.error(new InvalidLoginException("error"));
                    }
                    String token = jwtService.generateToken(userEntity);
                    return Mono.just(new LoginResponse(token));
                });
    }

    @Override
    public Mono<GenericResponse> forgotPassword(ForgotPasswordRequest request) {
        return userRepository.findByEmail(request.email())
                .switchIfEmpty(Mono.error(new InvalidEmailException("error invalid email")))
                .flatMap(userEntity -> mailService.sendEmail(request)
                        .flatMap(sendEmailResponse -> {
                            PasswordResetToken passwordResetToken = new PasswordResetToken();
                            passwordResetToken.setUsed(false);
                            passwordResetToken.setUserId(userEntity.getId());
                            passwordResetToken.setCode(sendEmailResponse.code());
                            passwordResetToken.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                            passwordResetToken.setExpiresAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(15));
                            return passwordResetTokenRepository.save(passwordResetToken)
                                    .thenReturn(new GenericResponse("Email sent successfully with id: " + sendEmailResponse.id()));
                        }));
    }

    @Override
    public Mono<GenericResponse> verifyCode(VerifyCodeRequest request) {
        return null;
    }

    @Override
    public Mono<GenericResponse> resetPassword(ResetPasswordRequest request) {
        return null;
    }
}
