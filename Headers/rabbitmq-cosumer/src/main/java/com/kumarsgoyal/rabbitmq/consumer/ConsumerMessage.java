package com.kumarsgoyal.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumarsgoyal.rabbitmq.dto.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumerMessage {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMessage.class);

    private static final String HIGH_PRIORITY_QUEUE_NAME = "queue.high.priority.name";

    private static final String LOW_PRIORITY_QUEUE_NAME = "queue.low.priority.name";

    private static final String LOW_HIGH_PRIORITY_QUEUE_NAME = "queue.low.high.priority.name";

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = HIGH_PRIORITY_QUEUE_NAME)
    public void onHrMessageReceive(String message) {
        processMessage(message, "HIGH");
    }

    @RabbitListener(queues = LOW_PRIORITY_QUEUE_NAME)
    public void onAccountsMessageReceive(String message) {
        processMessage(message, "LOW");
    }

    @RabbitListener(queues = LOW_HIGH_PRIORITY_QUEUE_NAME)
    public void onItMessageReceive(String message) {
        processMessage(message, "LOW_HIGH");
    }

    private void processMessage(String message, String messageType) {
        try {
            Employee employee = objectMapper.readValue(message, Employee.class);
            logger.info("Received {} message: {}", messageType, employee.toString());
        } catch (JsonProcessingException e) {
            logger.error("Error processing {} message: {}", messageType, e.getMessage());
            // Handle the error, e.g., move message to a dead-letter queue or acknowledge it.
        }
    }
}
