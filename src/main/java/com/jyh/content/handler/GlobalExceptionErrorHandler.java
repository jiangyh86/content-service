package com.jyh.content.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author jiangyiheng
 * @date 2022-10-04 16:12
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionErrorHandler {
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity error(SecurityException e){
        log.error("发生 SecurityException 异常");
        return new ResponseEntity<>(
                ErrorBody.builder()
                        .body(e.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build(),HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity error(IllegalArgumentException e){
        log.error("发生 IllegalArgumentException 异常");
        return new ResponseEntity<>(
                ErrorBody.builder()
                        .body(e.getMessage())
                        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                        .build(),HttpStatus.METHOD_NOT_ALLOWED
        );
    }
}
