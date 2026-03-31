package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.component.exception.SendEmailException;
import com.brayanspv.auth.model.request.EmailRequest;
import com.brayanspv.auth.service.contracts.IMailService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Log4j2
public class ResendMailService implements IMailService {

    private final Resend resend;

    public ResendMailService(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public ResendMailService(Resend resend) {
        this.resend = resend;
    }

    @Override
    public Mono<String> sendEmail(EmailRequest request) {
        return Mono.fromCallable(() -> {
                    log.info("Sending email to {}", request.to());
                    CreateEmailOptions params = CreateEmailOptions.builder()
                            .from(request.from())
                            .to(request.to())
                            .subject(request.subject())
                            .html(request.body())
                            .build();
                    return resend.emails().send(params);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(response -> log.info("Email sent successfully with id: {}", response.getId()))
                .doOnError(error -> log.error("Failed to send email: {}", error.getMessage()))
                .onErrorMap(error -> new SendEmailException("Error sending email: " + error.getMessage()))
                .map(CreateEmailResponse::getId);
    }
}
