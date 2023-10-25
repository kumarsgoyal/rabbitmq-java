package com.kumarsgoyal.rabbitmq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kumarsgoyal.rabbitmq.dto.Employee;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProducerMessage {

    private static final Logger logger = Logger.getLogger(ProducerMessage.class.getName());

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final FanoutExchange fanoutExchange;

    @Autowired
    private ObjectMapper objectMapper;

    public ProducerMessage(RabbitTemplate rabbitTemplate, FanoutExchange fanoutExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.fanoutExchange = fanoutExchange;
    }

    public void sendMessage(Employee employee)  {
        try {

            String jsonMessage = objectMapper.writeValueAsString(employee);
            Message message = MessageBuilder
                                      .withBody(jsonMessage.getBytes(StandardCharsets.UTF_8))
                                      .build();

            rabbitTemplate.convertAndSend(fanoutExchange.getName(),"", message);
            logger.info("Message sent successfully for Emp: " + employee.getEmpId() + ".");
        } catch (AmqpException e) {
            logger.log(Level.SEVERE, "Error sending the message to RabbitMQ.", e);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing the message to JSON.", e);

        }
    }
}
