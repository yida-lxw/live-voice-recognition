package org.yida.live.voice.recognition;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.vosk.Recognizer;
import org.yida.live.voice.recognition.audio.AudioRecognizerHolder;
import org.yida.live.voice.recognition.utils.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;

/**
 * @author yida
 * @package org.yida.live.voice.recognition
 * @date 2024-10-28 16:17
 * @description Type your description over here.
 */
@SpringBootApplication(scanBasePackages={"org.yida.live.voice.recognition"})
@EnableScheduling
@EnableAsync
public class Application implements ServletContextInitializer {
	/**
	 * 音频采样率
	 */
	@Value("${audio.sample-rate:16000}")
	private int audioSampleRate;

	/**
	 * 语音识别模型路径
	 */
	@Value("${audio.model-path}")
	private String audioModelPath;

	/**
	 * 语音识别模型临时输出目录
	 */
	@Value("${audio.temp-output-path}")
	private String modelTempOutputPath;

	public static void main(String[] args) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		System.setProperty("file.encoding", "UTF-8");
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		if (StringUtils.isNotEmpty(modelTempOutputPath)) {
			File spiderWorkSpaceFile = new File(modelTempOutputPath);
			if (!spiderWorkSpaceFile.exists()) {
				spiderWorkSpaceFile.mkdirs();
			}
		}
		AudioRecognizerHolder audioRecognizerHolder = AudioRecognizerHolder.getInstance();
		Recognizer recognizer = AudioRecognizerHolder.getInstance().buildRecognizer(audioModelPath, audioSampleRate);
		audioRecognizerHolder.setRecognizer(recognizer);
	}
}
