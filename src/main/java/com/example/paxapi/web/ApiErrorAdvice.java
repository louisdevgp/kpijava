package com.example.paxapi.web;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.Map;


@RestControllerAdvice
public class ApiErrorAdvice {


@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
return ResponseEntity.badRequest().body(Map.of(
"error", "validation_error",
"message", ex.getMessage()
));
}


@ExceptionHandler(RuntimeException.class)
public ResponseEntity<?> handleRuntime(RuntimeException ex) {
return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
"error", "pax_api_error",
"message", ex.getMessage()
));
}
}