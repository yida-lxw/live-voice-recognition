package org.yida.live.voice.recognition.bean;

/**
 * @author yida
 * @package org.yida.live.voice.recognition.bean
 * @date 2024-10-29 16:40
 * @description Type your description over here.
 */
public class PerToken {
	private int conf;
	private float start;
	private float end;
	private String word;

	public PerToken() {
	}

	public PerToken(int conf, float start, float end, String word) {
		this.conf = conf;
		this.start = start;
		this.end = end;
		this.word = word;
	}

	public int getConf() {
		return conf;
	}

	public void setConf(int conf) {
		this.conf = conf;
	}

	public float getStart() {
		return start;
	}

	public void setStart(float start) {
		this.start = start;
	}

	public float getEnd() {
		return end;
	}

	public void setEnd(float end) {
		this.end = end;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
