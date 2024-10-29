package org.yida.live.voice.recognition.websocket.model;

import java.io.Serializable;

/**
 * WebSocket事件
 *
 * @param <T>
 * @author Administrator
 */
public class WebSocketEvent<T> implements Serializable {
	private static final long serialVersionUID = 8379590378417619790L;

	private int eventCode;

	private String eventName;

	private long timestamp;

	private T message;

	public WebSocketEvent(int eventCode, String eventName, T message) {
		this(eventCode, eventName, message, System.currentTimeMillis());
	}

	public WebSocketEvent(int eventCode, String eventName, T message, long timestamp) {
		this.eventCode = eventCode;
		this.eventName = eventName;
		this.message = message;
		this.timestamp = timestamp;
	}


	public int getEventCode() {
		return eventCode;
	}

	public void setEventCode(int eventCode) {
		this.eventCode = eventCode;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}
}
