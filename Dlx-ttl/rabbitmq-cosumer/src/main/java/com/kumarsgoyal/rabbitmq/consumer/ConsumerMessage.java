package com.kumarsgoyal.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumarsgoyal.rabbitmq.dto.Employee;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConsumerMessage {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMessage.class);

    private static final String NORMAL_FANOUT_QUEUE_NAME = "queue.fan_out.queue";
    private static final String DLX_DIRECT_QUEUE_NAME = "queue.dlx.direct.queue";

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = NORMAL_FANOUT_QUEUE_NAME)
    public void onNormalMessageReceive(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            Employee employee = objectMapper.readValue(message, Employee.class);
            if(Integer.parseInt(employee.getEmpId()) % 2 == 1) {
                channel.basicReject(tag, false);
                return;
            }
            processMessage(employee, "Queue");
            channel.basicAck(tag, false);
        } catch (JsonProcessingException e) {
            logger.error("Error processing message: {}", e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = DLX_DIRECT_QUEUE_NAME)
    public void onDlxMessageReceive(String message) {
        try {
            Employee employee = objectMapper.readValue(message, Employee.class);
            processMessage(employee, "DLXQueue");
        } catch (JsonProcessingException e) {
            logger.error("Error processing message: {}", e.getMessage());
            // Handle the error, e.g., move message to a dead-letter queue or acknowledge it.
        }
    }

    private void processMessage(Employee employee, String messageType) {
        logger.info("Received {} message: {}", messageType, employee.toString());
    }
}
