package org.yida.live.voice.recognition.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.Model;
import org.vosk.Recognizer;

/**
 * @author yida
 * @package org.yida.live.voice.recognition.audio
 * @date 2024-10-29 13:58
 * @description Type your description over here.
 */
public class AudioRecognizerHolder {
	private static Logger logger = LoggerFactory.getLogger(AudioRecognizerHolder.class);

	private Recognizer recognizer;

	private AudioRecognizerHolder() {
	}

	private static class SingletonHolder {
		private static final AudioRecognizerHolder INSTANCE = new AudioRecognizerHolder();
	}

	public static AudioRecognizerHolder getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public Recognizer buildRecognizer(String modelPath, int audioSampleRate) {
		Model model = null;
		try {
			model = new Model(modelPath);
		} catch (Exception e) {
			logger.error("Loading Audio Model occur exception:\n{}.", e.getMessage());
			return null;
		}
		Recognizer recognizer = null;
		try {
			recognizer = new Recognizer(model, audioSampleRate);
			recognizer.setWords(true);
		} catch (Exception e) {
			logger.error("Build Audio Recognizer instance occur exception:\n{}.", e.getMessage());
			return null;
		} finally {
			return recognizer;
		}
	}

	public Recognizer getRecognizer() {
		return recognizer;
	}

	public void setRecognizer(Recognizer recognizer) {
		this.recognizer = recognizer;
	}
}
