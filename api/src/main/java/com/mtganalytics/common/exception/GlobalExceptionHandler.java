package com.mtganalytics.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @ExceptionHandler(GameEntryNotFoundException.class)
        public ResponseEntity<String> handleNotFound(
                        GameEntryNotFoundException ex) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(GameEntryRecordFailureException.class)
        public ResponseEntity<String> handleRecordFailure(
                        GameEntryRecordFailureException ex) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ex.getMessage());
        }

        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler(GameServiceException.class)
        public ResponseEntity<String> handleServiceError(
                        GameServiceException ex) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ex.getMessage());
        }
}