package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.exception.SendEmailException;
import com.brayanspv.auth.model.request.EmailRequest;
import com.brayanspv.auth.component.exception.SendEmailException;
import com.brayanspv.auth.model.request.EmailRequest;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResendMailServiceTest {

    @Mock
    private Resend resend;

    @Mock
    private Emails emails;

    @InjectMocks
    private ResendMailService resendMailService;

    private CreateEmailResponse createEmailResponse;

    @BeforeEach
    void setUp() {
        createEmailResponse = new CreateEmailResponse();
        createEmailResponse.setId("email-id-12345");

        when(resend.emails()).thenReturn(emails);
    }

    @Test
    void sendEmail_success() throws ResendException, IOException {
        EmailRequest emailRequest = new EmailRequest(
                "noreply@auth.com",
                "user@example.com",
                "Test Subject",
                "<h1>Hello</h1>"
        );

        doReturn(createEmailResponse).when(emails).send(any(CreateEmailOptions.class));

        StepVerifier.create(resendMailService.sendEmail(emailRequest))
                .expectNext("email-id-12345")
                .verifyComplete();
    }

    @Test
    void sendEmail_error_throwsSendEmailException() throws ResendException, IOException {
        EmailRequest emailRequest = new EmailRequest(
                "noreply@auth.com",
                "user@example.com",
                "Test Subject",
                "<h1>Hello</h1>"
        );

        doThrow(new ResendException("API error"))
                .when(emails).send(any(CreateEmailOptions.class));

        StepVerifier.create(resendMailService.sendEmail(emailRequest))
                .expectErrorMatches(error ->
                        error instanceof SendEmailException
                                && error.getMessage().contains("Error sending email: API error")
                )
                .verify();
    }

    @Test
    void sendEmail_withDifferentRequest() throws ResendException, IOException {
        EmailRequest anotherRequest = new EmailRequest(
                "support@auth.com",
                "admin@example.com",
                "Another Subject",
                "<p>Body</p>"
        );

        doReturn(createEmailResponse).when(emails).send(any(CreateEmailOptions.class));

        StepVerifier.create(resendMailService.sendEmail(anotherRequest))
                .expectNext("email-id-12345")
                .verifyComplete();
    }
}
