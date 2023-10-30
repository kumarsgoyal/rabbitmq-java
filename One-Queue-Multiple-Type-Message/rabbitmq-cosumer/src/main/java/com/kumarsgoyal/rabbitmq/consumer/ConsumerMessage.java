package com.kumarsgoyal.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumarsgoyal.rabbitmq.dto.InvoiceCreatedMessage;
import com.kumarsgoyal.rabbitmq.dto.InvoicePaidMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RabbitListener(queues = "ampq.queue.name", concurrency = "2")
public class ConsumerMessage {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMessage.class);


    @Autowired
    private ObjectMapper objectMapper;

    @RabbitHandler
    public void onInvoiceCreationMessageReceive(InvoiceCreatedMessage invoiceCreatedMessage) throws InterruptedException{
        logger.info("invoiceCreatedMessage Received: {}", invoiceCreatedMessage.getInvoiceNumber());
    }

    @RabbitHandler
    public void onInvoicePaidMessageReceive(InvoicePaidMessage invoicePaidMessage)  throws InterruptedException{
        logger.info("InvoicePaidMessage Received: {}", invoicePaidMessage.getInvoiceNumber());
    }
}
