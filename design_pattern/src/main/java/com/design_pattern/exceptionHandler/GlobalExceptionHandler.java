package com.design_pattern.exceptionHandler;

import com.design_pattern.dto.ApiResponse;
import com.design_pattern.exceptionHandler.exception.ResourceAlreadyExistException;
import com.design_pattern.exceptionHandler.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.design_pattern.constant.ApiConstant.ERROR_MSG;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger("");

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<?> badRequestExceptionHandle(HttpMessageConversionException exp) {
        log.warn("Bad request exception occurred: {}", exp.getMessage());
        log.debug("Exception details", exp);

        ApiResponse apiResponse = ApiResponse
                .builder()
                .message(ERROR_MSG)
                .status(false)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception exp) {
        log.error("Unexpected exception occurred", exp);

        ApiResponse apiResponse = ApiResponse
                .builder()
                .message("An unexpected error occurred")
                .status(false)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exp) {
        ApiResponse apiResponse = ApiResponse
                .builder()
                .message(exp.getMessage())
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException exp) {
        logger.info("Resource not found exception has occurred: {}", exp.getMessage());
        return new ResponseEntity<>(
                ApiResponse
                        .builder()
                        .message(exp.getMessage())
                        .status(false)
                        .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<?> resourceAlreadyExistException(ResourceAlreadyExistException exp) {
        return new ResponseEntity<>(ApiResponse.builder().message(exp.getMessage())
                .status(false)
                .build(), HttpStatus.CONFLICT);
    }
}
