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

    private static final String HR_QUEUE_NAME = "queue.hr.queue";

    private static final String ACCOUNTS_QUEUE_NAME = "queue.accounts.queue";

    private static final String IT_QUEUE_NAME = "queue.it.queue";

    private static final String DB_QUEUE_NAME = "queue.db.queue";

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = HR_QUEUE_NAME)
    public void onHrMessageReceive(String message) {
        processMessage(message, "HR");
    }

    @RabbitListener(queues = ACCOUNTS_QUEUE_NAME)
    public void onAccountsMessageReceive(String message) {
        processMessage(message, "Accounts");
    }

    @RabbitListener(queues = IT_QUEUE_NAME)
    public void onItMessageReceive(String message) {
        processMessage(message, "IT");
    }

    @RabbitListener(queues = DB_QUEUE_NAME)
    public void onDbMessageReceive(String message) {
        processMessage(message, "DB");
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
