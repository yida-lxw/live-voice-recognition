package org.yida.live.voice.recognition.utils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.wav.WavFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.Recognizer;
import org.yida.live.voice.recognition.bean.RecognitionResult;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author yida
 * @package org.yida.live.voice.recognition.utils
 * @date 2024-10-29 14:15
 * @description 音频文件操作工具类
 */
public class AudioUtils {
	private static Logger logger = LoggerFactory.getLogger(AudioUtils.class);

	/**
	 * @param recognizer
	 * @param inputStream
	 * @return {@link RecognitionResult}
	 * @description 识别音频为字符串文本
	 * @author yida
	 * @date 2024-10-29 16:45:58
	 */
	public static RecognitionResult recognize(Recognizer recognizer, InputStream inputStream) {
		int bytes;
		byte[] b = new byte[4096];
		try {
			while ((bytes = inputStream.read(b)) >= 0) {
				recognizer.acceptWaveForm(b, bytes);
			}
		} catch (Exception e) {
			RecognitionResult recognitionResult = new RecognitionResult(RecognitionResult.FAILED_CODE, "", new ArrayList<>());
			return recognitionResult;
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
		}
		String finalResult = recognizer.getFinalResult();
		RecognitionResult recognitionResult = JacksonUtils.json2Bean(finalResult, RecognitionResult.class);
		recognitionResult.setCode(RecognitionResult.SUCCESS_CODE);
		return recognitionResult;
	}

	/**
	 * 更改音频采样率
	 *
	 * @param targetSampleRate 转换后的音频采样率
	 * @param audioInputStream 音频输入流
	 * @param outputPath       转换后的音频临时输出目录
	 * @return 转换后的音频输入流
	 * @throws Exception 如果发生错误
	 */
	public static AudioInfo changeAudioSampleRate(int targetSampleRate, InputStream audioInputStream, String outputPath) throws Exception {
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream);
			 AudioInputStream originalAudioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream)) {
			AudioFormat originalFormat = originalAudioInputStream.getFormat();
			AudioFormat targetFormat = new AudioFormat(targetSampleRate, originalFormat.getSampleSizeInBits(),
					originalFormat.getChannels(),
					originalFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED),
					originalFormat.isBigEndian());
			AudioInputStream convertedAudioInputStream = AudioSystem.getAudioInputStream(targetFormat, originalAudioInputStream);

			//生成随机文件名
			String audioFileName = StringUtils.generateRandomFileName("wav", 16);
			String outputAudioFilePath = outputPath + audioFileName;
			File tempFile = new File(outputAudioFilePath);
			AudioSystem.write(convertedAudioInputStream, AudioFileFormat.Type.WAVE, tempFile);
			FileInputStream fileInputStream = new FileInputStream(tempFile);
			AudioInfo audioInfo = new AudioInfo(targetSampleRate, fileInputStream, outputAudioFilePath);
			return audioInfo;
		}
	}

	public static Integer getSampleRate(File file) throws Exception {
		WavFileReader fileReader = new WavFileReader();
		AudioFile audioFile = fileReader.read(file);
		String sampleRate = audioFile.getAudioHeader().getSampleRate();
		return Integer.valueOf(sampleRate);
	}

	public static Integer getSampleRate(InputStream inputStream) {
		return getSampleRate(inputStream, true);
	}

	public static Integer getSampleRate(InputStream inputStream, boolean closeInputStream) {
		if (closeInputStream) {
			try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
				 AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream)) {
				AudioFormat audioFormat = audioInputStream.getFormat();
				Float sampleRate = audioFormat.getSampleRate();
				return sampleRate.intValue();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
			AudioFormat audioFormat = audioInputStream.getFormat();
			Float sampleRate = audioFormat.getSampleRate();
			int sampleRateValue = sampleRate.intValue();
			return sampleRateValue;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		File file = new File("F:/tmp/output/0001.wav");
		InputStream inputStream = new FileInputStream(file);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
		Integer sampleRate = AudioUtils.getSampleRate(bufferedInputStream);
		System.out.println("[before]sampleRate:" + sampleRate);
		inputStream = new FileInputStream(file);
		bufferedInputStream = new BufferedInputStream(inputStream);
		AudioInfo audioInfo = AudioUtils.changeAudioSampleRate(16000, bufferedInputStream, "F:/tmp/output/");
		sampleRate = AudioUtils.getSampleRate(audioInfo.getInputStream());
		System.out.println("[after]sampleRate:" + sampleRate);
	}
}
