package org.yida.live.voice.recognition.bean;

import java.util.List;

/**
 * @author yida
 * @package org.yida.live.voice.recognition.bean
 * @date 2024-10-29 16:39
 * @description Type your description over here.
 */
public class RecognitionResult {
	public static final int SUCCESS_CODE = 200;
	public static final int FAILED_CODE = 500;
	private int code;
	private String text;
	private List<PerToken> result;

	public RecognitionResult(int code, String text, List<PerToken> result) {
		this.code = code;
		this.text = text;
		this.result = result;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<PerToken> getResult() {
		return result;
	}

	public void setResult(List<PerToken> result) {
		this.result = result;
	}
}
