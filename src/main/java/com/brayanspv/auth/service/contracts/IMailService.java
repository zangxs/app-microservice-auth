package com.brayanspv.auth.service.contracts;

import com.brayanspv.auth.model.request.EmailRequest;
import reactor.core.publisher.Mono;

public interface IMailService {

    Mono<String> sendEmail(EmailRequest request);
}
