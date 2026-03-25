package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.mocks.JSONMockConstants;
import com.brayanspv.auth.model.request.SignUpRequest;
import com.brayanspv.auth.model.response.SignUpResponse;
import com.brayanspv.auth.repositories.contracts.IUserRepository;
import com.brayanspv.auth.repositories.entities.UserEntity;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import({UserService.class, UserEntity.class})
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    private final Gson gson = new Gson();

    @Test
    void signUpTestOk() {
        SignUpRequest request = gson.fromJson(JSONMockConstants.SIGNUP_REQUEST, SignUpRequest.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setId(1L);
        userEntity.setPassword(request.getPassword());
        userEntity.setUsername(request.getUsername());
        // Mock the save operation to return a Mono.just with an id assigned
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserService userService = new UserService(userRepository);
        StepVerifier.create(userService.signUp(request))
                .expectNextMatches(signUpResponse ->  signUpResponse.getUsername().equals(request.getUsername()))
                .verifyComplete();
    }
}