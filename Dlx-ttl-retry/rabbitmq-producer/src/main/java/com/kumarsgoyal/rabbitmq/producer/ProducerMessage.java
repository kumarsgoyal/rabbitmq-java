package com.kumarsgoyal.rabbitmq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kumarsgoyal.rabbitmq.dto.Employee;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProducerMessage {

    private static final Logger logger = Logger.getLogger(ProducerMessage.class.getName());

    private static String WORK_ROUTING_KEY = "ampq.routing.work.key";

    private static String WAIT_EXCHANGE_NAME = "ampq.queue.wait.exchange.name";

    private static String WAIT_QUEUE_NAME= "ampq.queue.wait.name";
    private static String WAIT_ROUTING_KEY = "ampq.routing.wait.key";

    private final RabbitTemplate rabbitTemplate;

    private final DirectExchange directExchange;

    @Autowired
    private ObjectMapper objectMapper;

    public ProducerMessage(RabbitTemplate rabbitTemplate, @Qualifier("workExchangeName") DirectExchange directExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.directExchange = directExchange;
    }

    public void sendMessage(Employee employee)  {
        try {

            String jsonMessage = objectMapper.writeValueAsString(employee);
            Map<String, Object> argsMap = new HashMap<>();

            Message message = MessageBuilder
                                      .withBody(jsonMessage.getBytes(StandardCharsets.UTF_8))
                                      .build();

            rabbitTemplate.convertAndSend(directExchange.getName(), WORK_ROUTING_KEY, message);
            logger.info("Message sent successfully for Emp: " + employee.getEmpId() + ".");
        } catch (AmqpException e) {
            logger.log(Level.SEVERE, "Error sending the message to RabbitMQ.", e);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing the message to JSON.", e);

        }
    }
}
