package com.example.redirector.exceptionHandler;

import com.example.redirector.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(exception = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception exception) {
        log.warn("Attempt to violate business rules: ", exception);
        ErrorResponse errorResponse = new ErrorResponse(
                "Unable to create link - short name might already be taken",
                exception.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(Exception exception) {
        log.warn("Requested entity was not found: ", exception);
        ErrorResponse errorResponse = new ErrorResponse(
                "Requested link was not found",
                exception.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(Exception exception) {
        log.error("ValidationException occurred: ", exception);
        ErrorResponse errorResponse = new ErrorResponse(
                "Arguments did not pass validation",
                exception.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<ErrorResponse> handleBasicException(Exception exception) {
        log.error("Unhandled exception: ", exception);
        ErrorResponse err = new ErrorResponse(
                "Unexpected error",
                exception.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

}
