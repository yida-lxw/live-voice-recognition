package org.yida.live.voice.recognition.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.vosk.Recognizer;
import org.yida.live.voice.recognition.audio.AudioRecognizerHolder;
import org.yida.live.voice.recognition.bean.RecognitionResult;
import org.yida.live.voice.recognition.bean.ResponseResult;
import org.yida.live.voice.recognition.utils.AudioUtils;
import org.yida.live.voice.recognition.utils.JacksonUtils;
import org.yida.live.voice.recognition.utils.StringUtils;
import org.yida.live.voice.recognition.websocket.model.WebSocketEventType;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * WebSocket通讯编辑服务
 *
 * @author Administrator
 */
@ServerEndpoint("/ws")
@Component
public class VoiceRecognitionWebSocketServer {
	private static Logger logger = LoggerFactory.getLogger(VoiceRecognitionWebSocketServer.class);
	private Session session;

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		WebSocketManager.sendMessage(session, "connected");
		WebSocketManager.addWebSocketServer(this);
		WebSocketManager.updateHeartBeat(session);
		logger.info("Establish a WebSocket connection with the client with sessionId:[{}] successfully.", session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		if (WebSocketEventType.PING_EVENT_NAME.equals(message)) {
			handlePingMessage(session);
			return;
		}
		if (WebSocketEventType.QUIT_EVENT_NAME.equals(message)) {
			handleQuitMessage(session);
			return;
		}
		Map<String, Object> event = JacksonUtils.json2Map(message);
		Object eventNameObj = event.get("eventName");
		String eventName = null;
		if (null == eventNameObj) {
			logger.warn("Message:[{}] doesn't contains eventName.", message);
			return;
		}
		eventName = eventNameObj.toString();
		if (WebSocketEventType.PING_EVENT_NAME.equalsIgnoreCase(eventName)) {
			handlePingMessage(session);
		} else if (WebSocketEventType.QUIT_EVENT_NAME.equalsIgnoreCase(eventName)) {
			handleQuitMessage(session);
		} else {
			handleOtherMessage(session, eventName);
		}
	}


	@OnMessage
	public void onMessage(byte[] bytesMessage, Session session) {
		if (null != session && session.isOpen() && null != bytesMessage && bytesMessage.length > 0) {
			WebSocketManager.updateHeartBeat(session);
			Recognizer recognizer = AudioRecognizerHolder.getInstance().getRecognizer();
			if (null != recognizer) {
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesMessage);
				RecognitionResult recognitionResult = AudioUtils.recognize(recognizer, byteArrayInputStream);
				boolean recognizeSuccess = (null != recognitionResult &&
						RecognitionResult.SUCCESS_CODE == recognitionResult.getCode() &&
						StringUtils.isNotEmpty(recognitionResult.getText()));
				ResponseResult<String> responseResult = null;
				if (recognizeSuccess) {
					responseResult = ResponseResult.success(WebSocketEventType.LIVE_VOICE_RECOGNITION_EVENT_NAME, recognitionResult.getText());
				} else {
					responseResult = ResponseResult.error(WebSocketEventType.LIVE_VOICE_RECOGNITION_EVENT_NAME);
				}
				String responseJSONData = JacksonUtils.toJSONString(responseResult);
				VoiceRecognitionWebSocketContext voiceRecognitionWebSocketContext = WebSocketManager.getCurrentVoiceRecognitionWebSocketContext(session);
				if (null != voiceRecognitionWebSocketContext) {
					voiceRecognitionWebSocketContext.response(WebSocketEventType.LIVE_VOICE_RECOGNITION, responseJSONData);
				}
			}
		}
	}

	@OnClose
	public void onClose(Session session) {
		WebSocketManager.removeWebSocketServer(this);
		WebSocketManager.removeWebSocketSession(session);
		logger.info("WebSocket connection was closed.");
		VoiceRecognitionWebSocketContext voiceRecognitionWebSocketContext = WebSocketManager.getCurrentVoiceRecognitionWebSocketContext(session);
		if (null != voiceRecognitionWebSocketContext) {
			voiceRecognitionWebSocketContext.setRunning(false);
			voiceRecognitionWebSocketContext.stop();
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		logger.info("Establish a WebSocket connection with the client with sessionId:[{}] failed.", session.getId());
	}

	private static void handlePingMessage(Session session) {
		String sessionId = session.getId();
		logger.info("Recieved the heartbeat packet from the websocket client with sessionId:[{}].", sessionId);
		WebSocketManager.updateHeartBeat(session);
		if (session.isOpen()) {
			WebSocketManager.sendMessage(sessionId, "pong");
		}
	}

	private static void handleQuitMessage(Session session) {
		if (null == session) {
			return;
		}
		if (!session.isOpen()) {
			String sessionId = session.getId();
			WebSocketManager.removeVoiceRecognitionWebSocketContext(sessionId);
			return;
		}
		String sessionId = session.getId();
		logger.info("Recieved the quit command message from the websocket client with sessionId:[{}].", sessionId);
		VoiceRecognitionWebSocketContext voiceRecognitionWebSocketContext = WebSocketManager.getCurrentVoiceRecognitionWebSocketContext(session);
		voiceRecognitionWebSocketContext.stop();
		WebSocketManager.removeVoiceRecognitionWebSocketContext(sessionId);
	}

	private static void handleOtherMessage(Session session, String eventName) {
		if (null == session || !session.isOpen()) {
			return;
		}
		WebSocketManager.updateHeartBeat(session);
		VoiceRecognitionWebSocketContext voiceRecognitionWebSocketContext = WebSocketManager.getCurrentVoiceRecognitionWebSocketContext(session);
		voiceRecognitionWebSocketContext.response(WebSocketEventType.UNSUPPORT, "The websocket client with sessionId:[" +
				session.getId() + "] doesn't support this eventName:[" + eventName + "].");
	}

	public Session getSession() {
		return session;
	}

	public String getSessionId() {
		return session.getId();
	}
}