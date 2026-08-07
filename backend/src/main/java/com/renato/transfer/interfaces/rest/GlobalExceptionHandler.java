package com.renato.transfer.interfaces.rest;

import com.renato.transfer.exception.ErrorResponse;
import com.renato.transfer.exception.FeeNotApplicableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message("One or more fields are invalid")
            .details(details)
            .build());
    }

    @ExceptionHandler(FeeNotApplicableException.class)
    public ResponseEntity<ErrorResponse> handleFeeNotApplicable(FeeNotApplicableException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
            .code("FEE_NOT_APPLICABLE")
            .message(ex.getMessage())
            .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
            .code("INTERNAL_ERROR")
            .message("An unexpected error occurred")
            .build());
    }
}
