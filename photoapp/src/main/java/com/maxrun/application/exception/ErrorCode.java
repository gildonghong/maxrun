package com.maxrun.application.exception;

/**
 * @author gildo
 * @date 2021. 6. 14
 * @desc 
 */
public enum ErrorCode {
	
	//4×× Client Error
	INVALID_INPUT_VALUE(400, "C001", "Invalid Input Value"),
	INVALID_ARGUMENT_TYPE(400, "C002", "Invalid Type for Argurment"),
	UNAUTHORIZED(401, "C003", "Unauthorized"),
	PAYMENT_REQUIRED(402, "C004", "Payment Required"),
	FORBIDDEN(403, "C005", "Forbidden"),
	NOT_FOUND(404, "C006", "Not Found"),
	METHOD_NOT_ALLOWED(405, "C007", "Method Not Allowed"),
	NOT_ACCEPTABLE(406, "C008", "Not Acceptable"),
	PROXY_AUTHENTICATION_REQUIRED(407, "C009","Proxy Authentication Required"),
	REQUEST_TIMEOUT(408, "C010", "Request Timeout"),
	CONFLICT(409, "C011", "Conflict"),
	GONE(410, "C012", "Gone"),
	LENGTH_REQUIRED(411, "C013", "Length Required"),
	PRECONDITION_FAILED(412, "C014", "Precondition Failed"),
	PAYLOAD_TOO_LARGE(413, "C015", "Payload Too Large"),
	URI_TOO_LONG(414, "C016", "URI Too Long"),
	UNSUPPORTED_MEDIA_TYPE(415, "C017", "Unsupported Media Type"),
	REQUESTED_RANGE_NOT_SATISFIABLE(416, "C018", "Requested range not satisfiable"),
	EXPECTATION_FAILED(417, "C019", "Expectation Failed"),
	I_AM_A_TEAPOT(418, "C020", "I'm a teapot"),
	MISDIRECTED_REQUEST(421, "C021", "Misdirected Request"),
	UNPROCESSABLE_ENTITY(422, "C022", "Unprocessable Entity"),
	LOCKED(423, "C023", "Locked"),
	FAILED_DEPENDENCY(424, "C024", "Failed Dependency"),
	UPGRADE_REQUIRED(426, "C025", "Upgrade Required"),
	PRECONDITION_REQUIRED(428, "C026", "Precondition Required"),
	TOO_MANY_REQUESTS(429, "C027", "Too Many Requests"),
	REQUEST_HEADER_FIELDS_TOO_LARGE(431, "C028", "Request Header Fields Too Large"),
	CONNECTION_CLOSED_WITHOUT_RESPONSE(444, "C029", "Connection Closed Without Response"),
	UNAVAILABLE_FOR_LEGAL_REASONS(451, "C030","Unavailable For Legal Reasons"),
	CLIENT_CLOSED_REQUEST(499, "C031","Client Closed Request"),
	
    //5×× Server Error
    INTERNAL_SERVER_ERROR(500, "S001", "Internal Server Error raised"),
    NOT_IMPLEMENTED(501, "S002",  "Not Implemented"),
    BAD_GATEWAY(502, "S003",  "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "S004",  "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "S005",  "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "S006",  "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, "S007",  "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "S008",  "Insufficient Storage"),
    LOOP_DETECTED(508, "S009",  "Loop Detected"),
    NOT_EXTENDED(510, "S010",  "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "S011",  "Network Authentication Required"),
    NETWORK_CONNECT_TIMEOUT_ERROR(599, "S012",  "Network Connect Timeout Error"),
	
    //Member
    EMAIL_DUPLICATION(400, "M001", "Email is Duplication"),
    LOGIN_INPUT_INVALID(400, "M002", "Login input is invalid"),
    
    //TOKEN 관련
    EXPIRED_OR_INVALID_TOKEN(400, "T001", "JWT token is not valid, so login is required")
    ;
	private int status;
    private final String code;
	private final String message;

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	ErrorCode(final int status, final String code, final String message){
		this.status = status;
		this.message = message;
		this.code = code;
	}
}
