package com.brayanspv.auth.controller.implementations;

import com.brayanspv.auth.controller.contracts.IExampleController;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class ExampleController implements IExampleController {
    @Override
    public Mono<ResponseEntity> getExampe() {
        return null;
    }
}