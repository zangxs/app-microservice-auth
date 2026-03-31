package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.exception.InvalidLoginException;
import com.brayanspv.auth.model.request.*;
import com.brayanspv.auth.model.response.GenericResponse;
import com.brayanspv.auth.model.response.LoginResponse;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.repositories.contracts.IUserRepository;
import com.brayanspv.auth.repositories.entities.UserEntity;
import com.brayanspv.auth.service.contracts.IJWTService;
import com.brayanspv.auth.service.contracts.IMailService;
import com.brayanspv.auth.service.contracts.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@Log4j2
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final IJWTService jwtService;
    private final IMailService mailService;

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
        EmailRequest emailRequest = new EmailRequest(
                "noreply@auth.com",
                request.email(),
                "Password Reset Request",
                "Your password reset code is: 123456"
        );
        return mailService.sendEmail(emailRequest)
                .map(id -> new GenericResponse("Email sent successfully with id: " + id));
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
