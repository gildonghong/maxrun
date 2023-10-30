package com.maxrun.application.exception;

public enum BizExType {

    INSUFFICIENT_PARAMETERS,
    WRONG_PARAMETER_VALUE,
    PARAMETER_MISSING,
    PARAMETER_NOT_ALLOWDED,
    DUPLICATED_PARAMETER,
    REQUEST_FAIL,   // 외부 요청처리중 오류

    // Login
    NO_MATCHING_USER_FOUND,
    TOKEN_GENERATION_FAIL,
    TOKEN_EXPIRED,
    TOKEN_INVALID,
    NOT_AUTHORIZED, // 권한없음
    NOT_AUTHENTICATED,  // 로그인 정보 없음
    TOO_MANY_LOGIN_ATTEMPT, // 5회 이상 실패로 계정잠김
    ACCOUNT_DELETED, // 탈퇴한 계정
    ADDITIONAL_LOGIN_AUTHENTICATION, //추가 로그인 인증    

    PASSWORD_TOO_SHORT,
    PASSWORD_NOT_MIXED,
    CATEGORY_COUNT_INVALID,
    CATEGORY_MUST_BE_CHOSEN,

    // Oauth
    OAUTH_TOKEN_NOT_FOUND,
    OAUTH_TOKEN_EXPIRED,
    OAUTH_USER_INFO_MISSING,
    OAUTH_EMAIL_MISSING,
    OAUTH_SIGNED_UP,
    OAUTH_PAYMENT_PENDING,		// PAYMENT PENDING
    OAUTH_PAYMENT_INVALID_DATE,	// 영수증 날자 정보가 유효하지 않음
    OAUTH_IOS_TRY_LATER,		// IOS 영수증 검증시 나중에 다시 시도해야 하는 이슈 발생시 
    OAUTH_IOS_INVALID_RECEIPT,	// IOS 유효하지 않은 영수증인 경우
    OAUTH_RECEIPT_INVALID,		// IN APP receipt 정보가 유효하지 않음
    

    // Message
    MSG_SEND_FAIL,
    MSG_NO_DATA,
    FORM_NOT_FOUND,
    MSG_NOTHING_RESEND,
    MSG_RECEPTION_REJECT,

    // Email
    EMAIL_NOT_VALID,
    EMAIL_EXISTS,

    // File
    FILE_NOT_FOUND,
    FILE_NOT_ALLOW,
    FILE_SIZE_EXCEEDED,

    // Push
    PUSH_TOKEN_NOT_FOUND,
    
    //JWT TOKEN
    ACCESS_TOKEN_EXPIRED,
    ACCESS_TOKEN_MISSING,
    ACCESS_TOKEN_DIRTY,
    REF_TOKEN_EXPIRED,
    REF_TOKEN_MISSING,
    REF_TOKEN_DIRTY,
    
    //Socket 관련
    FILE_NOT_EXISTS,
    
    //SERVER ERROR
    SERVER_ERROR,
    API_ERROR,
    UNKNOWN
}
