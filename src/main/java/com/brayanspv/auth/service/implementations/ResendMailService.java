package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.exception.SendEmailException;
import com.brayanspv.auth.model.request.EmailRequest;
import com.brayanspv.auth.model.request.ForgotPasswordRequest;
import com.brayanspv.auth.model.response.SendEmailResponse;
import com.brayanspv.auth.service.contracts.IMailService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Log4j2
@RequiredArgsConstructor
public class ResendMailService implements IMailService {

    @Value("${resend.api-key}")
    String apiKey;

    @Value("${resend.email}")
    private String emailFrom;


    @Override
    public Mono<SendEmailResponse> sendEmail(ForgotPasswordRequest request) {
        String resetCode = generateResetCode();
        return Mono.fromCallable(() -> {
                    Resend resend = createResendClient();
                    log.info("Sending email to {}", request.email());
                    String htmlContent = buildForgotPasswordHtml(request.email(), resetCode);
                    CreateEmailOptions params = CreateEmailOptions.builder()
                            .from(emailFrom)
                            .to(request.email())
                            .subject("Change your password")
                            .html(htmlContent)
                            .build();
                    return resend.emails().send(params);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(response -> log.info("Email sent successfully with id: {}", response.getId()))
                .doOnError(error -> log.error("Failed to send email: {}", error.getMessage()))
                .onErrorMap(error -> new SendEmailException("Error sending email: " + error.getMessage()))
                .map(response -> new SendEmailResponse(response.getId(), request.email(), resetCode));
    }

    Resend createResendClient() {
        return new Resend(apiKey);
    }

    private String buildForgotPasswordHtml(String email, String resetCode) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Reset your password</title>
                </head>
                <body style="margin:0; padding:0; background-color:#f4f4f4; font-family:Arial,sans-serif;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f4; padding:40px 0;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                                    <tr>
                                        <td style="background-color:#4f46e5; padding:30px 40px; text-align:center;">
                                            <h1 style="color:#ffffff; margin:0; font-size:24px;">Reset your password</h1>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:40px;">
                                            <p style="color:#333333; font-size:16px; line-height:1.6; margin:0 0 20px;">Hello,</p>
                                            <p style="color:#333333; font-size:16px; line-height:1.6; margin:0 0 20px;">We received a request to reset the password for your account associated with <strong>%s</strong>.</p>
                                            <p style="color:#333333; font-size:16px; line-height:1.6; margin:0 0 20px;">Use the following code to reset your password:</p>
                                            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td align="center" style="padding:20px 0;">
                                                        <span style="display:inline-block; background-color:#f3f4f6; color:#111827; font-size:32px; font-weight:bold; letter-spacing:8px; padding:16px 32px; border-radius:8px; font-family:monospace;">%s</span>
                                                    </td>
                                                </tr>
                                            </table>
                                            <p style="color:#6b7280; font-size:14px; line-height:1.6; margin:20px 0 0;">This code will expire in 15 minutes. If you did not request a password reset, you can safely ignore this email.</p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color:#f9fafb; padding:20px 40px; text-align:center; border-top:1px solid #e5e7eb;">
                                            <p style="color:#9ca3af; font-size:12px; margin:0;">&copy; 2026 Auth Service. All rights reserved.</p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(email, resetCode);
    }

    private String generateResetCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
