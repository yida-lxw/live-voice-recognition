package org.yida.live.voice.recognition.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yida.live.voice.recognition.constants.Constants;

import javax.websocket.Session;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yida
 * @package org.spiderflow.websocket
 * @date 2024-08-25 19:41
 * @description WebSocket管理者
 */
public class WebSocketManager {
	private static Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

	private static final CopyOnWriteArraySet<VoiceRecognitionWebSocketServer> webSocketServerSet = new CopyOnWriteArraySet<>();
	private static final Map<String, VoiceRecognitionWebSocketServer> webSocketServerMap = new ConcurrentHashMap<>();
	private static final Map<String, WebSocketSession> webSocketSessionMap = new ConcurrentHashMap<>();
	private static final Map<String, VoiceRecognitionWebSocketContext> voiceRecognitionWebSocketContextMap = new ConcurrentHashMap<>();

	private static final ReentrantLock lock = new ReentrantLock();

	/**
	 * @description 检测缓存的Websocket Session是否仍存活，若已不存活，则将其从缓存中移除,避免JVM内存不断膨胀
	 * @author yida
	 * @date 2024-08-25 20:51:33
	 */
	public static void checkWebsocketClientIfAlive(Long maxIntervalOfNotRecievingHeartbeatPacket) {
		if (!webSocketServerSet.isEmpty()) {
			try {
				lock.lock();
				for (VoiceRecognitionWebSocketServer webSocketServer : webSocketServerSet) {
					String sessionId = webSocketServer.getSessionId();
					Session session = webSocketServer.getSession();
					if (!session.isOpen()) {
						boolean removeResult = webSocketServerSet.remove(webSocketServer);
						if (removeResult) {
							webSocketServerMap.remove(sessionId);
							session.close();
						}
					} else {
						WebSocketSession webSocketSession = webSocketSessionMap.get(sessionId);
						if (null != webSocketSession) {
							long lastRecieveHeartbeatTime = webSocketSession.getLastRecieveHeartbeatTime();
							long currentTimeMills = System.currentTimeMillis();
							if (null == maxIntervalOfNotRecievingHeartbeatPacket || maxIntervalOfNotRecievingHeartbeatPacket <= 0L) {
								maxIntervalOfNotRecievingHeartbeatPacket = Constants.MAX_INTERVAL_OF_NOT_RECIEVING_HEARTBEAT_PACKET;
							}
							if (currentTimeMills - lastRecieveHeartbeatTime > maxIntervalOfNotRecievingHeartbeatPacket) {
								boolean removeResult = webSocketServerSet.remove(webSocketServer);
								if (removeResult) {
									webSocketServerMap.remove(sessionId);
									webSocketSessionMap.remove(sessionId);
									voiceRecognitionWebSocketContextMap.remove(sessionId);
									session.close();
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("while check the websocket session was opened, we occur exception:\n{}.", e.getMessage());
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * @param session
	 * @description 更新当前WebSocket Session的心跳信息
	 * @author yida
	 * @date 2024-08-25 21:45:02
	 */
	public static void updateHeartBeat(Session session) {
		if (session != null && session.isOpen()) {
			String sessionId = session.getId();
			long currentTimeMills = System.currentTimeMillis();
			WebSocketSession webSocketSession = webSocketSessionMap.get(sessionId);
			if (null == webSocketSession) {
				webSocketSession = new WebSocketSession(sessionId, currentTimeMills);
			} else {
				webSocketSession.setSessionId(sessionId);
				webSocketSession.setLastRecieveHeartbeatTime(currentTimeMills);
			}
			webSocketSessionMap.put(sessionId, webSocketSession);
			voiceRecognitionWebSocketContextMap.put(sessionId, new VoiceRecognitionWebSocketContext(session, true));
		}
	}

	public static void addWebSocketServer(VoiceRecognitionWebSocketServer webSocketServer) {
		if (webSocketServer != null) {
			webSocketServerSet.add(webSocketServer);
			webSocketServerMap.put(webSocketServer.getSessionId(), webSocketServer);
		}
	}

	public static void removeWebSocketServer(VoiceRecognitionWebSocketServer webSocketServer) {
		webSocketServerSet.remove(webSocketServer);
		webSocketServerMap.remove(webSocketServer.getSessionId());
	}

	public static void removeWebSocketSession(Session session) {
		String sessionId = session.getId();
		voiceRecognitionWebSocketContextMap.remove(sessionId);
		WebSocketSession webSocketSession = webSocketSessionMap.get(sessionId);
		if (null == webSocketSession) {
			return;
		}
		webSocketSessionMap.remove(sessionId);
	}

	public static VoiceRecognitionWebSocketContext getCurrentVoiceRecognitionWebSocketContext(Session session) {
		String sessionId = session.getId();
		VoiceRecognitionWebSocketContext voiceRecognitionWebSocketContext = voiceRecognitionWebSocketContextMap.get(sessionId);
		if (null != voiceRecognitionWebSocketContext) {
			return voiceRecognitionWebSocketContext;
		}
		voiceRecognitionWebSocketContext = new VoiceRecognitionWebSocketContext(session);
		voiceRecognitionWebSocketContextMap.put(sessionId, voiceRecognitionWebSocketContext);
		return voiceRecognitionWebSocketContext;
	}

	public static boolean removeVoiceRecognitionWebSocketContext(String sessionId) {
		VoiceRecognitionWebSocketContext removeVoiceRecognitionWebSocketContext = voiceRecognitionWebSocketContextMap.remove(sessionId);
		return null != removeVoiceRecognitionWebSocketContext;
	}

	/**
	 * 通过SessionId发送消息
	 *
	 * @param
	 * @param msg
	 */
	public static void sendMessage(String sessionId, String msg) {
		sendMessage(sessionId, msg, false);
	}

	/**
	 * 通过SessionId发送消息
	 *
	 * @param
	 * @param msg
	 */
	public static void sendMessage(String sessionId, String msg, boolean asyncSend) {
		Session session = webSocketServerMap.get(sessionId).getSession();
		sendMessage(session, msg, asyncSend);
	}

	/**
	 * 通过SessionId发送消息
	 *
	 * @param
	 * @param msg
	 */
	public static void sendMessage(Session session, String msg) {
		sendMessage(session, msg, false);
	}


	/**
	 * 通过Session发送消息
	 *
	 * @param session
	 * @param msg
	 */
	public static void sendMessage(Session session, String msg, boolean asyncSend) {
		if (session == null) {
			logger.error("The session doesn't exists，so we can't send message.");
			return;
		}
		if (!session.isOpen()) {
			logger.error("The sesion with sessionId:[{}] was closed, so we can't send any message to websocket client.", session.getId());
			return;
		}
		try {
			if (asyncSend) {
				session.getAsyncRemote().sendText(msg);
			} else {
				session.getBasicRemote().sendText(msg);
			}
		} catch (Exception e) {
			logger.error("As sending message with Websocket Session {} occur exception:\n{}.", asyncSend ? "asynchronously" : "synchronously", e.getMessage());
		}
	}

	/**
	 * 通过SessionId发送字节数组消息
	 *
	 * @param
	 * @param binaryMessage
	 */
	public static void sendMessage(String sessionId, byte[] binaryMessage) {
		sendMessage(sessionId, binaryMessage, false);
	}

	/**
	 * 通过SessionId发送字节数组消息
	 *
	 * @param
	 * @param binaryMessage
	 */
	public static void sendMessage(String sessionId, byte[] binaryMessage, boolean asyncSend) {
		Session session = webSocketServerMap.get(sessionId).getSession();
		sendMessage(session, binaryMessage, asyncSend);
	}

	/**
	 * 通过Session发送字节数组消息
	 *
	 * @param session
	 * @param binaryMessage
	 */
	public static void sendMessage(Session session, byte[] binaryMessage, boolean asyncSend) {
		if (session == null) {
			logger.error("The session doesn't exists，so we can't send binary message.");
			return;
		}
		if (!session.isOpen()) {
			logger.error("The sesion with sessionId:[{}] was closed, so we can't send any binary message to websocket client.", session.getId());
			return;
		}
		try {
			ByteBuffer byteBuffer = ByteBuffer.wrap(binaryMessage);
			if (asyncSend) {
				session.getAsyncRemote().sendBinary(byteBuffer);
			} else {
				session.getBasicRemote().sendBinary(byteBuffer);
			}
		} catch (Exception e) {
			logger.error("As sending binary message with Websocket Session {} occur exception:\n{}.", asyncSend ? "asynchronously" : "synchronously", e.getMessage());
		}
	}

	/**
	 * 通过Session发送字节数组消息
	 *
	 * @param session
	 * @param binaryMessage
	 */
	public static void sendMessage(Session session, byte[] binaryMessage) {
		sendMessage(session, binaryMessage, false);
	}

	/**
	 * 发送消息
	 *
	 * @param msg
	 */
	public static void sendMessage(String msg) {
		for (VoiceRecognitionWebSocketServer webSocketServer : webSocketServerSet) {
			sendMessage(webSocketServer.getSession(), msg);
		}
		logger.info("send message:[{}] to all websocket client successfully.", msg);
	}
}