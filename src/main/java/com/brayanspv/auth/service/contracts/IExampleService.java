package com.brayanspv.auth.service.contracts;

import com.brayanspv.auth.model.response.ExampleResponse;
import reactor.core.publisher.Mono;

public interface IExampleService {

    Mono<ExampleResponse> getExample();
}