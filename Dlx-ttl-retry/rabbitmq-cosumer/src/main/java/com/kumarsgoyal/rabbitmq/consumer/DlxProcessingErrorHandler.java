package com.kumarsgoyal.rabbitmq.consumer;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.lang.NonNull;

import com.kumarsgoyal.rabbitmq.ampq.RabbitmqHeader;
import com.rabbitmq.client.Channel;

public class DlxProcessingErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(DlxProcessingErrorHandler.class);

	private static String DEAD_ROUTING_KEY = "ampq.routing.dead.key";

	@NonNull
	private String deadExchangeName;

	private int maxRetryCount = 3;

	private RabbitTemplate rabbitTemplate;

	public DlxProcessingErrorHandler(RabbitTemplate rabbitTemplate, String deadExchangeName) throws IllegalArgumentException {
		if (StringUtils.isAnyEmpty(deadExchangeName)) {
			throw new IllegalArgumentException("Must define dlx exchange name");
		}

		this.rabbitTemplate = rabbitTemplate;
		this.deadExchangeName = deadExchangeName;
	}

	public String getDeadExchangeName() {
		return deadExchangeName;
	}

	public int getMaxRetryCount() {
		return maxRetryCount;
	}

	public boolean handleErrorProcessingMessage(Message message, Channel channel, long deliveryTag) {
		var rabbitMqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

		try {
			if (rabbitMqHeader.getFailedRetryCount() >= maxRetryCount) {
				// publish to dead and ack
				logger.warn("[DEAD] Error at " + LocalDateTime.now() + " on retry " + rabbitMqHeader.getFailedRetryCount()
						+ " for message " + new String(message.getBody()));
				rabbitTemplate.convertAndSend(getDeadExchangeName(), DEAD_ROUTING_KEY, message);
			} else {
				logger.warn("[REQUEUE] Error at " + LocalDateTime.now() + " on retry "
						+ rabbitMqHeader.getFailedRetryCount() + " for message " + new String(message.getBody()));
				channel.basicReject(deliveryTag, false);
			}
			return true;
		} catch (IOException e) {
			logger.warn("[HANDLER-FAILED] Error at " + LocalDateTime.now() + " on retry "
					+ rabbitMqHeader.getFailedRetryCount() + " for message " + new String(message.getBody()));
		}

		return false;
	}

	public void setMaxRetryCount(int maxRetryCount) throws IllegalArgumentException {
		if (maxRetryCount > 1000) {
			throw new IllegalArgumentException("max retry must between 0-1000");
		}

		this.maxRetryCount = maxRetryCount;
	}

}
