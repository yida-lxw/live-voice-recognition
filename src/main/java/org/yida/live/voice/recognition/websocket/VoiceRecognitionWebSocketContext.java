package org.yida.live.voice.recognition.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yida.live.voice.recognition.utils.JacksonUtils;
import org.yida.live.voice.recognition.websocket.model.WebSocketEvent;
import org.yida.live.voice.recognition.websocket.model.WebSocketEventType;

import javax.websocket.Session;

/**
 * WebSocket通讯中音频识别服务的上下文域
 *
 * @author Administrator
 */
public class VoiceRecognitionWebSocketContext {
	private static Logger logger = LoggerFactory.getLogger(VoiceRecognitionWebSocketContext.class);
	private static final long serialVersionUID = 1205890535069540209L;

	private Session session;

	private volatile boolean running;

	private Object lock = new Object();

	public VoiceRecognitionWebSocketContext(Session session) {
		this(session, false);
	}

	public VoiceRecognitionWebSocketContext(Session session, boolean running) {
		this.session = session;
		this.running = running;
	}

	public void response(WebSocketEventType webSocketEventType, String message) {
		this.write(new WebSocketEvent<>(webSocketEventType.getEventCode(), webSocketEventType.getEventName(), message));
	}

	public void response(WebSocketEventType webSocketEventType, byte[] bytesData) {
		this.write(new WebSocketEvent<>(webSocketEventType.getEventCode(), webSocketEventType.getEventName(), bytesData));
	}

	public <T> void write(WebSocketEvent<T> event) {
		try {
			if (this.running && session.isOpen()) {
				String message = JacksonUtils.toJSONString(event);
				synchronized (session) {
					WebSocketManager.sendMessage(session, message);
				}
			}
		} catch (Throwable ignored) {
		}
	}

	public void pause() {
		if (this.isRunning()) {
			synchronized (this) {
				if (this.isRunning()) {
					synchronized (lock) {
						try {
							this.running = false;
							lock.wait();
						} catch (InterruptedException ignored) {
							Thread.currentThread().interrupt();
						}
					}
				}
			}
		}
	}

	public void resume() {
		synchronized (lock) {
			lock.notifyAll();
			this.running = true;
		}
	}

	public void stop() {
		synchronized (lock) {
			if (null != this.session && this.session.isOpen()) {
				try {
					session.close();
				} catch (Exception e) {
					logger.error("As closing the Websocket Session, occur exception:\n{}.", e.getMessage());
				} finally {
					this.session = null;
					this.running = false;
				}
			}
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Object getLock() {
		return lock;
	}

	public void setLock(Object lock) {
		this.lock = lock;
	}
}
