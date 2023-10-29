package com.maxrun.application.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

/**
 * @author gildo
 * @date 2021. 6. 15
 * @desc 사용자정의 CheckedException
 */
public class BizException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BizExType type = BizExType.UNKNOWN;
	private Map<String, Object> data = new HashMap<>();


	public BizException() {
		super();
	}

	public BizException(Throwable throwable){
		super(throwable);
	}

	public BizException(String message, Throwable throwable){
		super(message, throwable);
	}

	public BizException(BizExType type) {
		this.type = type;
	}

	public BizException(String message) {
		super(message);
	}

	public BizException(BizExType type, String message) {
		super(message);
		this.type = type;
	}
	public BizException(BizExType type, Map<String, Object> data) {
		super();
		this.type = type;
		this.data = data;
	}

	public BizExType getType() {
		return this.type;
	}
	public Map<String, Object> getData() {
		return this.data;
	}
//	INSUFFICIENT_PARAMETERS,
//    WRONG_PARAMETER_VALUE,
//    PARAMETER_MISSING,
//    PARAMETER_NOT_ALLOWDED,
//    DUPLICATED_PARAMETER,
//    REQUEST_FAIL,   // 외부 요청처리중 오류
//
//    // Login
//    NO_MATCHING_USER_FOUND,
//    TOKEN_GENERATION_FAIL,
//    TOKEN_EXPIRED,
//    TOKEN_INVALID,
//    NOT_AUTHORIZED, // 권한없음
//    NOT_AUTHENTICATED,  // 로그인 정보 없음
//    TOO_MANY_LOGIN_ATTEMPT, // 5회 이상 실패로 계정잠김
//    ACCOUNT_DELETED, // 탈퇴한 계정
//    ADDITIONAL_LOGIN_AUTHENTICATION, //추가 로그인 인증    
//
//    PASSWORD_TOO_SHORT,
//    PASSWORD_NOT_MIXED,
//    CATEGORY_COUNT_INVALID,
//    CATEGORY_MUST_BE_CHOSEN,
//
//    // Oauth
//    OAUTH_TOKEN_NOT_FOUND,
//    OAUTH_TOKEN_EXPIRED,
//    OAUTH_USER_INFO_MISSING,
//    OAUTH_EMAIL_MISSING,
//    OAUTH_SIGNED_UP,
//    OAUTH_PAYMENT_PENDING,		// PAYMENT PENDING
//    OAUTH_PAYMENT_INVALID_DATE,	// 영수증 날자 정보가 유효하지 않음
//    OAUTH_IOS_TRY_LATER,		// IOS 영수증 검증시 나중에 다시 시도해야 하는 이슈 발생시 
//    OAUTH_IOS_INVALID_RECEIPT,	// IOS 유효하지 않은 영수증인 경우
//    OAUTH_RECEIPT_INVALID,		// IN APP receipt 정보가 유효하지 않음
//    
//
//    // Message
//    MSG_SEND_FAIL,
//    MSG_NO_DATA,
//    FORM_NOT_FOUND,
//    MSG_NOTHING_RESEND,
//    MSG_RECEPTION_REJECT,
//
//    // Email
//    EMAIL_NOT_VALID,
//    EMAIL_EXISTS,
//
//    // File
//    FILE_NOT_FOUND,
//    FILE_NOT_ALLOW,
//    FILE_SIZE_EXCEEDED,
//
//    // Push
//    PUSH_TOKEN_NOT_FOUND,
//    
//    //JWT TOKEN
//    ACCESS_TOKEN_EXPIRED,
//    ACCESS_TOKEN_MISSING,
//    ACCESS_TOKEN_DIRTY,
//    REF_TOKEN_EXPIRED,
//    REF_TOKEN_MISSING,
//    REF_TOKEN_DIRTY,
//    
//    //SERVER ERROR
//    SERVER_ERROR,
//    API_ERROR,
//    UNKNOWN
	public HttpStatus getHttpStatus() {
		if (type == BizExType.ACCESS_TOKEN_DIRTY || type==BizExType.ACCESS_TOKEN_EXPIRED || 
			type==BizExType.ACCESS_TOKEN_MISSING || type==BizExType.ACCOUNT_DELETED || type==BizExType.FILE_SIZE_EXCEEDED ||
			type==BizExType.REF_TOKEN_DIRTY || type==BizExType.REF_TOKEN_EXPIRED || type==BizExType.REF_TOKEN_MISSING || 
			type==BizExType.PASSWORD_TOO_SHORT || type==BizExType.PASSWORD_NOT_MIXED || type==BizExType.NO_MATCHING_USER_FOUND || 
			type==BizExType.NOT_AUTHENTICATED || type==BizExType.NOT_AUTHORIZED) {
			return HttpStatus.UNAUTHORIZED;
		}else if(type==BizExType.WRONG_PARAMETER_VALUE || type==BizExType.PARAMETER_NOT_ALLOWDED || type==BizExType.PARAMETER_MISSING || 
				type==BizExType.DUPLICATED_PARAMETER || type==BizExType.PASSWORD_TOO_SHORT || type==BizExType.INSUFFICIENT_PARAMETERS) {
			return HttpStatus.BAD_REQUEST;
		}else {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}
}
