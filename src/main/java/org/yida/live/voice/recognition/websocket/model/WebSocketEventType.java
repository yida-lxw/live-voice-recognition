package org.yida.live.voice.recognition.websocket.model;

/**
 * @author yida
 * @package org.yida.live.voice.recognition.websocket.model
 * @date 2024-10-29 09:33
 * @description Type your description over here.
 */
public enum WebSocketEventType {
	LIVE_VOICE_RECOGNITION(WebSocketEventType.LIVE_VOICE_RECOGNITION_EVENT_CODE, WebSocketEventType.LIVE_VOICE_RECOGNITION_EVENT_NAME),
	PING(WebSocketEventType.PING_EVENT_CODE, WebSocketEventType.PING_EVENT_NAME),
	QUIT(WebSocketEventType.QUIT_EVENT_CODE, WebSocketEventType.QUIT_EVENT_NAME),
	UNSUPPORT(WebSocketEventType.UNSUPPORT_EVENT_CODE, WebSocketEventType.UNSUPPORT_EVENT_NAME);

	/**
	 * 事件代码
	 */
	private int eventCode;

	/**
	 * 事件名称
	 */
	private String eventName;

	public static final int LIVE_VOICE_RECOGNITION_EVENT_CODE = 0;
	public static final String LIVE_VOICE_RECOGNITION_EVENT_NAME = "live-voice-recognition-event";

	public static final int PING_EVENT_CODE = 1;
	public static final String PING_EVENT_NAME = "ping";

	public static final int QUIT_EVENT_CODE = 2;
	public static final String QUIT_EVENT_NAME = "quit";

	public static final int UNSUPPORT_EVENT_CODE = 3;
	public static final String UNSUPPORT_EVENT_NAME = "unsupport";

	WebSocketEventType(int eventCode, String eventName) {
		this.eventCode = eventCode;
		this.eventName = eventName;
	}

	public static WebSocketEventType of(int eventCode) {
		if (WebSocketEventType.LIVE_VOICE_RECOGNITION_EVENT_CODE == eventCode) {
			return LIVE_VOICE_RECOGNITION;
		}
		if (WebSocketEventType.PING_EVENT_CODE == eventCode) {
			return PING;
		}
		if (WebSocketEventType.QUIT_EVENT_CODE == eventCode) {
			return QUIT;
		}
		if (WebSocketEventType.UNSUPPORT_EVENT_CODE == eventCode) {
			return UNSUPPORT;
		}
		throw new IllegalArgumentException("Unknow eventCode:[" + eventCode + "] for WebSocketEventType");
	}

	public static WebSocketEventType of(String eventName) {
		if (WebSocketEventType.LIVE_VOICE_RECOGNITION_EVENT_NAME.equals(eventName)) {
			return LIVE_VOICE_RECOGNITION;
		}
		if (WebSocketEventType.PING_EVENT_NAME.equals(eventName)) {
			return PING;
		}
		if (WebSocketEventType.QUIT_EVENT_NAME.equals(eventName)) {
			return QUIT;
		}
		if (WebSocketEventType.UNSUPPORT_EVENT_NAME.equals(eventName)) {
			return UNSUPPORT;
		}
		throw new IllegalArgumentException("Unknow eventName:[" + eventName + "] for WebSocketEventType");
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
}
