package com.fooddelivery.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
    EmailAlreadyExistsException.class,
    PhoneAlreadyExistsException.class
})
public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException e) {

    Map<String, Object> body = Map.of(
        "message", e.getMessage(),
        "status", 409,
        "timestamp", LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error
                -> errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({
    InvalidCredentialsException.class
})
public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException e) {

    Map<String, Object> body = Map.of(
        "message", e.getMessage(),
        "status", HttpStatus.UNAUTHORIZED.value(),
        "timestamp", LocalDateTime.now()
    );

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
}

@ExceptionHandler(InvalidRefreshTokenException.class)
public ResponseEntity<?> handleInvalidRefreshToken(
        InvalidRefreshTokenException ex) {

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", ex.getMessage()));
}


}
