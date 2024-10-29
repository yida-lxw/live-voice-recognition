package org.yida.live.voice.recognition.bean;

import java.io.Serializable;

/**
 * @author yida
 * @package org.yida.live.voice.recognition.bean
 * @date 2024-10-29 17:03
 * @description Type your description over here.
 */
public class ResponseResult<T> implements Serializable {
	private static final long serialVersionUID = 5705363635055700224L;

	public static final String SUCCESS_MSG = "success";
	public static final String FAILED_MSG = "failed";

	private int code;
	private String msg;
	private String eventName;
	private T data;

	public ResponseResult(int code, String msg, String eventName, T data) {
		this.code = code;
		this.msg = msg;
		this.eventName = eventName;
		this.data = data;
	}

	public static <T> ResponseResult success(String msg, String eventName, T data) {
		return new ResponseResult(RecognitionResult.SUCCESS_CODE, msg, eventName, data);
	}

	public static <T> ResponseResult success(String msg, String eventName) {
		return new ResponseResult(RecognitionResult.SUCCESS_CODE, msg, eventName, null);
	}

	public static <T> ResponseResult success(String eventName, T data) {
		return new ResponseResult(RecognitionResult.SUCCESS_CODE, SUCCESS_MSG, eventName, data);
	}

	public static <T> ResponseResult success(String eventName) {
		return new ResponseResult(RecognitionResult.SUCCESS_CODE, SUCCESS_MSG, eventName, null);
	}

	public static <T> ResponseResult error(String msg, String eventName, T data) {
		return new ResponseResult(RecognitionResult.FAILED_CODE, msg, eventName, data);
	}

	public static <T> ResponseResult error(String msg, String eventName) {
		return new ResponseResult(RecognitionResult.FAILED_CODE, msg, eventName, null);
	}

	public static <T> ResponseResult error(String eventName, T data) {
		return new ResponseResult(RecognitionResult.FAILED_CODE, FAILED_MSG, eventName, data);
	}

	public static <T> ResponseResult error(String eventName) {
		return new ResponseResult(RecognitionResult.FAILED_CODE, FAILED_MSG, eventName, null);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
