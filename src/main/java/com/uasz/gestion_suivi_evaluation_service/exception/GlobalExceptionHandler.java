package com.uasz.gestion_suivi_evaluation_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> badRequest(
            BadRequestException e
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> details = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            details.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "VALIDATION_ERROR");
        body.put("message", "Données invalides.");
        body.put("timestamp", LocalDateTime.now());
        body.put("details", details);

        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> accessDenied(
            AccessDeniedException e
    ) {
        return response(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "Accès refusé."
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> illegalArgument(
            IllegalArgumentException e
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "ILLEGAL_ARGUMENT",
                e.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> runtime(
            RuntimeException e
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "RUNTIME_ERROR",
                e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> exception(
            Exception e
    ) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Erreur interne du serveur."
        );
    }

    private ResponseEntity<Map<String, Object>> response(
            HttpStatus status,
            String error,
            String message
    ) {
        Map<String, Object> body = new HashMap<>();

        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity
                .status(status)
                .body(body);
    }
}