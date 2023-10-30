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

    private static final String QUEUE_NAME = "ampq.queue.name";


    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = QUEUE_NAME)
    public void onHrMessageReceive(Employee employee) {
        processMessage(employee);
    }
    private void processMessage(Employee employee) {
        logger.info("Received message: {}", employee);
    }
}
