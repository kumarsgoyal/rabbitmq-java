package com.kumarsgoyal.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumarsgoyal.rabbitmq.dto.Picture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumerMessage {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMessage.class);

    private static final String IMAGE_QUEUE_NAME = "queue.picture.image_name";
    private static final String VECTOR_QUEUE_NAME = "queue.picture.vector_name";
    private static final String MOBILE_QUEUE_NAME = "queue.picture.mobile_name";
    private static final String WEB_QUEUE_NAME = "queue.picture.web_name";
    private static final String LARGE_QUEUE_NAME = "queue.picture.large_name";

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = IMAGE_QUEUE_NAME)
    public void onImageMessageReceive(String message) {
        processMessage(message, "Image");
    }

    @RabbitListener(queues = VECTOR_QUEUE_NAME)
    public void onVectorMessageReceive(String message) {
        processMessage(message, "Vector");
    }

    @RabbitListener(queues = MOBILE_QUEUE_NAME)
    public void onMobileMessageReceive(String message) {
        processMessage(message, "Mobile");
    }

    @RabbitListener(queues = WEB_QUEUE_NAME)
    public void onWebMessageReceive(String message) {
        processMessage(message, "Web");
    }

    @RabbitListener(queues = LARGE_QUEUE_NAME)
    public void onLargeMessageReceive(String message) {
        processMessage(message, "Large");
    }

    private void processMessage(String message, String messageType) {
        try {
            Picture picture = objectMapper.readValue(message, Picture.class);
            logger.info("Received {} message: {}", messageType, picture.toString());
        } catch (JsonProcessingException e) {
            logger.error("Error processing {} message: {}", messageType, e.getMessage());
            // Handle the error, e.g., move message to a dead-letter queue or acknowledge it.
        }
    }
}
