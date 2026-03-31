package com.brayanspv.auth.service.contracts;

import com.brayanspv.auth.model.request.EmailRequest;
import com.brayanspv.auth.model.request.ForgotPasswordRequest;
import com.brayanspv.auth.model.response.SendEmailResponse;
import reactor.core.publisher.Mono;

public interface IMailService {

    Mono<SendEmailResponse> sendEmail(ForgotPasswordRequest request);
}
