package com.jycforest29.commerce.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice // 전역 예외 처리
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleException(CustomException e, HttpServletRequest httpServletRequest){
        log.error(e.getExceptionCode().toString(), httpServletRequest.getRequestURI());
        CustomExceptionResponse customExceptionResponse = CustomExceptionResponse.builder()
                .status(e.getExceptionCode().getStatus())
                .msg(e.getExceptionCode().getMsg())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customExceptionResponse);
    }

}
