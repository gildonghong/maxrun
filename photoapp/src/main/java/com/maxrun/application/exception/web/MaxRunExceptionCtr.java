package com.maxrun.application.exception.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.maxrun.application.exception.BizExType;
import com.maxrun.application.exception.BizException;
import com.maxrun.application.exception.ErrorCode;

@ControllerAdvice
public class MaxRunExceptionCtr {
	
	@ExceptionHandler(BizException.class)
    public ResponseEntity handleException(BizException e) {
        // log exception
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } 
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity handleException(Exception ex){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}

