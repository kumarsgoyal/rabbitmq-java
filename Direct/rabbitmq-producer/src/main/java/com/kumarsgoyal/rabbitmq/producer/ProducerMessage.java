package com.kumarsgoyal.rabbitmq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
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

    private List<String> department = List.of("hr", "accounts", "it");

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private final DirectExchange directExchange;

    @Autowired
    private ObjectMapper objectMapper;

    public ProducerMessage(RabbitTemplate rabbitTemplate, DirectExchange directExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.directExchange = directExchange;
    }

    public void sendMessage(Employee employee)  {
        try {
            StringBuilder sb = new StringBuilder("");
            sb.append("queue.");
            sb.append(department.get(Integer.parseInt(employee.getEmpId()) % department.size()));
            sb.append(".key");
            String jsonMessage = objectMapper.writeValueAsString(employee);
            Message message = MessageBuilder
                                      .withBody(jsonMessage.getBytes(StandardCharsets.UTF_8))
                                      .build();


            rabbitTemplate.convertAndSend(directExchange.getName(),sb.toString(), message);
            logger.info("Message sent successfully for Emp: " + employee.getEmpId() + ".");
        } catch (AmqpException e) {
            logger.log(Level.SEVERE, "Error sending the message to RabbitMQ.", e);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing the message to JSON.", e);

        }
    }
}
