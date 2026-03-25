package com.brayanspv.auth.service.implementations;
import com.brayanspv.auth.service.contracts.IExampleService;
import com.brayanspv.auth.model.response.ExampleResponse;
import reactor.core.publisher.Mono;

public class ExampleService implements IExampleService {
    @Override
    public Mono<ExampleResponse> getExample() {
        return null;
    }
}
