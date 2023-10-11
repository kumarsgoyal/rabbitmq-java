package com.kumarsgoyal.rabbitmq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumarsgoyal.rabbitmq.dto.Picture;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProducerMessage {

    private static final Logger logger = Logger.getLogger(ProducerMessage.class.getName());

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final TopicExchange topicExchange;

    @Autowired
    private ObjectMapper objectMapper;

    public ProducerMessage(RabbitTemplate rabbitTemplate, TopicExchange topicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
    }

    public void sendMessage(Picture picture)  {
        try {
            StringBuilder sb = new StringBuilder("");
            sb.append(picture.getSource());
            sb.append(".");
            sb.append(picture.getSize() > 4000 ? "large" : "small");
            sb.append(".");
            sb.append(picture.getType());

            String jsonMessage = objectMapper.writeValueAsString(picture);
            Message message = MessageBuilder
                                      .withBody(jsonMessage.getBytes(StandardCharsets.UTF_8))
                                      .build();

            rabbitTemplate.convertAndSend(topicExchange.getName(), sb.toString(), message);
            logger.info("Message sent successfully.");
        } catch (AmqpException e) {
            logger.log(Level.SEVERE, "Error sending the message to RabbitMQ.", e);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing the message to JSON.", e);

        }
    }
}
