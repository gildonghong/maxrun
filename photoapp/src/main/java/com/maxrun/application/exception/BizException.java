package com.maxrun.application.exception;

import java.util.HashMap;
import java.util.Map;

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
}
