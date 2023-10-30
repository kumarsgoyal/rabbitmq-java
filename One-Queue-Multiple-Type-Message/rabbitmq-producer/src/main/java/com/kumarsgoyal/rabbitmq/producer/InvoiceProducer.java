package com.kumarsgoyal.rabbitmq.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kumarsgoyal.rabbitmq.dto.InvoiceCancelledMessage;
import com.kumarsgoyal.rabbitmq.dto.InvoiceCreatedMessage;
import com.kumarsgoyal.rabbitmq.dto.InvoicePaidMessage;

@Service
public class InvoiceProducer {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceProducer.class);
	@Autowired
	private RabbitTemplate rabbitTemplate;

	private static final String EXCHANGE = "ampq.direct.exchange.name";

	private static final String KEY = "ampq.key.name";

	public void sendInvoiceCreated(InvoiceCreatedMessage message) {
		rabbitTemplate.convertAndSend(EXCHANGE, KEY, message);
		logger.info("InvoiceCreatedMessage send {} ", message.getInvoiceNumber());
	}

	public void sendInvoicePaid(InvoicePaidMessage message) {
		rabbitTemplate.convertAndSend(EXCHANGE, KEY, message);
		logger.info("InvoicePaidMessage send {} ", message.getInvoiceNumber());
	}

	public void sendInvoiceCancelled(InvoiceCancelledMessage message) {
		rabbitTemplate.convertAndSend(EXCHANGE, KEY, message);
		logger.info("sendInvoiceCancelled send {} ", message.getInvoiceNumber());
	}

}
