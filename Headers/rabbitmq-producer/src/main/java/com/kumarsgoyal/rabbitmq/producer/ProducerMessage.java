package com.kumarsgoyal.rabbitmq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kumarsgoyal.rabbitmq.dto.Employee;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProducerMessage {

    private static final Logger logger = Logger.getLogger(ProducerMessage.class.getName());

    private static List<String> PRIORITY = List.of("low", "high");

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final HeadersExchange headersExchange;

    @Autowired
    private ObjectMapper objectMapper;

    public ProducerMessage(RabbitTemplate rabbitTemplate, HeadersExchange headersExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.headersExchange = headersExchange;
    }

    public void sendMessage(Employee employee)  {
        try {
            employee.setPriority(PRIORITY.get(Integer.parseInt(employee.getEmpId()) % PRIORITY.size()));
            MessageProperties properties = new MessageProperties();
            properties.setHeader("priority", employee.getPriority());

            String jsonMessage = objectMapper.writeValueAsString(employee);
            Message message = MessageBuilder
                                      .withBody(jsonMessage.getBytes())
                                      .andProperties(properties)
                                      .build();

            rabbitTemplate.convertAndSend(headersExchange.getName(),"", message);
            logger.info("Message sent successfully for Emp: " + employee.getEmpId() + ".");
        } catch (AmqpException e) {
            logger.log(Level.SEVERE, "Error sending the message to RabbitMQ.", e);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing the message to JSON.", e);

        }
    }
}
