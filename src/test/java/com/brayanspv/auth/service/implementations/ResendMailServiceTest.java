package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.exception.SendEmailException;
import com.brayanspv.auth.model.request.ForgotPasswordRequest;
import com.brayanspv.auth.model.response.SendEmailResponse;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResendMailServiceTest {

    @Mock
    private Emails emails;

    @Mock
    private Resend resend;

    @Spy
    private ResendMailService resendMailService = new ResendMailService();

    private CreateEmailResponse createEmailResponse;

    @BeforeEach
    void setUp() {
        createEmailResponse = new CreateEmailResponse();
        createEmailResponse.setId("email-id-12345");

        ReflectionTestUtils.setField(resendMailService, "apiKey", "fake-api-key");
        ReflectionTestUtils.setField(resendMailService, "emailFrom", "noreply@auth.com");

        when(resendMailService.createResendClient()).thenReturn(resend);
        when(resend.emails()).thenReturn(emails);
    }

    @Test
    void sendEmail_success() throws ResendException, IOException {
        ForgotPasswordRequest request = new ForgotPasswordRequest("user@example.com");

        doReturn(createEmailResponse).when(emails).send(any(CreateEmailOptions.class));

        StepVerifier.create(resendMailService.sendEmail(request))
                .expectNextMatches(response ->
                        response.id().equals("email-id-12345")
                                && response.email().equals("user@example.com")
                                && response.code().matches("\\d{6}")
                )
                .verifyComplete();
    }

    @Test
    void sendEmail_error_throwsSendEmailException() throws ResendException, IOException {
        ForgotPasswordRequest request = new ForgotPasswordRequest("user@example.com");

        doThrow(new ResendException("API error"))
                .when(emails).send(any(CreateEmailOptions.class));

        StepVerifier.create(resendMailService.sendEmail(request))
                .expectErrorMatches(error ->
                        error instanceof SendEmailException
                                && error.getMessage().contains("Error sending email: API error")
                )
                .verify();
    }

    @Test
    void sendEmail_withDifferentRequest() throws ResendException, IOException {
        ForgotPasswordRequest anotherRequest = new ForgotPasswordRequest("admin@example.com");

        doReturn(createEmailResponse).when(emails).send(any(CreateEmailOptions.class));

        StepVerifier.create(resendMailService.sendEmail(anotherRequest))
                .expectNextMatches(response ->
                        response.id().equals("email-id-12345")
                                && response.email().equals("admin@example.com")
                )
                .verifyComplete();
    }
}
