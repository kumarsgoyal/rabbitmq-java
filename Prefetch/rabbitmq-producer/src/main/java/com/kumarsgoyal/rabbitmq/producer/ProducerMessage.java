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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProducerMessage {

    private static final Logger logger = Logger.getLogger(ProducerMessage.class.getName());

    @Value("${ampq.key.name.slow}")
    private String keySlowName;

    @Value("${ampq.key.name.fast}")
    private String keyFastName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange directExchange;

    public void sendSlowMessage(Employee employee)  {
        rabbitTemplate.convertAndSend(directExchange.getName(), keySlowName, employee);
        logger.info("Message sent slowly successfully for Emp: " + employee.getEmpId() + ".");
    }

    public void sendFastMessage(Employee employee)  {
        rabbitTemplate.convertAndSend(directExchange.getName(), keyFastName, employee);
        logger.info("Message sent fastly successfully for Emp: " + employee.getEmpId() + ".");
    }
}
