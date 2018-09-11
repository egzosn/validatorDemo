package com.egzosn.validator.exception;

/**
 *  自定义异常,
 *
 *  使用要点能不抛异常则不抛，
 *
 * @author egan
 *         email egzosn@gmail.com
 *         date 2014/10/24.16:10
 */
public class CommonException extends RuntimeException {
	/**
	 * 异常状态码
	 */
	private Integer code;
	/**
	 * 异常信息
	 */
	private String message;
	public CommonException(Integer code) {
		this.code = code;
	}
	
	public CommonException(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {

        return message;


	}
	

}
