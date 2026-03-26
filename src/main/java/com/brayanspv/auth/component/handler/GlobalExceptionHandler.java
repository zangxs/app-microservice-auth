package com.brayanspv.auth.component.handler;

import com.brayanspv.auth.model.response.generic.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("error in handler handleDataIntegrityViolationException: {}", e.getMessage());
        
        ApiResponse apiResponse = ApiResponse.builder()
                .dateTime(System.currentTimeMillis())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .data(e.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }


    @ExceptionHandler(value = ServerWebInputException.class)
    public ResponseEntity<?> handleWebExchangeBindException(ServerWebInputException e) {
        log.error("error in handler handleWebExchangeBindException: {}", e.getMessage());
        
        ApiResponse apiResponse = ApiResponse.builder()
                .dateTime(System.currentTimeMillis())
                .code(HttpStatus.BAD_REQUEST.value())
                .data(e.getMessage())
                //.data(e.getBindingResult().getFieldErrors().stream()
                //        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                //        .collect(Collectors.joining(", ")))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
