package org.yida.live.voice.recognition.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * 配置WebSocket
 *
 * @author yida
 */
@Configuration
public class WebSocketConfiguration {
	@Value("${websocket.message.binary-message-buffer-size:512000000}")
	private int binaryMessageBufferSize;

	@Value("${websocket.message.text-message-buffer-size:10240000}")
	private int textMessageBufferSize;

	@Value("${websocket.session.idle-timeout:1800000}")
	private long sessionIdleTimeout;

	@Value("${websocket.message.async-send-timeout:30000}")
	private long asyncSendTimeout;

	//
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean servletServerContainerFactoryBean = new ServletServerContainerFactoryBean();
		servletServerContainerFactoryBean.setMaxBinaryMessageBufferSize(binaryMessageBufferSize);
		servletServerContainerFactoryBean.setMaxTextMessageBufferSize(textMessageBufferSize);
		servletServerContainerFactoryBean.setMaxSessionIdleTimeout(sessionIdleTimeout);
		servletServerContainerFactoryBean.setAsyncSendTimeout(asyncSendTimeout);
		return servletServerContainerFactoryBean;
	}
}
