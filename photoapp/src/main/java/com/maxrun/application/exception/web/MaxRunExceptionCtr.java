package com.maxrun.application.exception.web;

import org.springframework.http.HttpHeaders;
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
		System.out.println("에러-->" + e.getMessage());
		HttpStatus status=HttpStatus.INTERNAL_SERVER_ERROR;
		HttpHeaders responseHeaders = new HttpHeaders();
	    //responseHeaders.set("CONTENT_ENCODING","utf-8"); //
	    responseHeaders.set("Content-Type", "application/json;charset=utf-8"); 

	    return ResponseEntity.status(e.getHttpStatus()).headers(responseHeaders).body(e.getMessage());
        //return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } 
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity handleException(Exception ex){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}

