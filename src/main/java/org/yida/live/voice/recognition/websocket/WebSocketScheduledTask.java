package org.yida.live.voice.recognition.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yida.live.voice.recognition.utils.DateUtils;

/**
 * @author yida
 * @package org.spiderflow.websocket
 * @date 2024-08-25 20:08
 * @description WebSocket定时任务
 */
@Component
public class WebSocketScheduledTask {
	private static Logger logger = LoggerFactory.getLogger(WebSocketScheduledTask.class);

	/**
	 * 连续多久没有接收到心跳包，则判定Websocket Client已经断线,默认为30秒(单位:毫秒)
	 */
	@Value("${websocket.heartbeat.not-receive-ping-max-interval:30000}")
	private long sessionIdleTimeout;

	@Async("websocketServerAsyncScheduledPool")
	@Scheduled(cron = "${websocket.scheduled-cron}")
	public void checkWebsocketClientIfAlive() {
		logger.info("Start to check the survival status of websocket sessions at {}.", DateUtils.format(System.currentTimeMillis()));
		WebSocketManager.checkWebsocketClientIfAlive(sessionIdleTimeout);
	}
}
