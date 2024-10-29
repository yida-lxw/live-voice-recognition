package org.yida.live.voice.recognition.utils;

import java.io.InputStream;

/**
 * @author yida
 * @package org.yida.live.voice.recognition.utils
 * @date 2024-10-29 16:22
 * @description Type your description over here.
 */
public class AudioInfo {
	private int sampleRate;
	private InputStream inputStream;
	private String audioFilePath;

	public AudioInfo(int sampleRate, InputStream inputStream, String audioFilePath) {
		this.sampleRate = sampleRate;
		this.inputStream = inputStream;
		this.audioFilePath = audioFilePath;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getAudioFilePath() {
		return audioFilePath;
	}

	public void setAudioFilePath(String audioFilePath) {
		this.audioFilePath = audioFilePath;
	}
}
