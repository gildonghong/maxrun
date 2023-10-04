package com.maxrun.application.exception;

//import com.ebs.gslp.common.code.ErrorCode;

/**
 * @author gildo
 * @date 2021. 6. 14
 * @desc 
 */
public class ExceptionResponse {

	private String message;
	private ParameterError error;	
	private ErrorCode errorCode;
	
	public static class ParameterError{
		private String name;
		private String value;
		
		public ParameterError() {};
		public ParameterError(String name, String value) {
			this.name = name;
			this.value = value;
		};
		
		/**
		 * @return the name
		 */
		public String getParam() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setParam(String name) {
			this.name = name;
		}
		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}
	}

	public ExceptionResponse(String message, ParameterError error, ErrorCode errorCode) {
		this.message = message;
		this.error = error;
		this.setErrorCode(errorCode);
	}
	
	public ExceptionResponse(String message, ErrorCode errorCode) {
		this.message = message;
		this.error = new ParameterError();
		this.setErrorCode(errorCode);
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public ParameterError getError() {
		return error;
	}

	public void setError(ParameterError error) {
		this.error = error;
	}

	/**
	 * @return the errorCode
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
