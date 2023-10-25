package com.kumarsgoyal.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumarsgoyal.rabbitmq.dto.Employee;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Member;

@Component
public class ConsumerMessage {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMessage.class);

    private static final String WORK_QUEUE_NAME = "ampq.queue.work.name";

    private static final String DLX_EXCHANGE_NAME = "ampq.queue.dead.exchange.name";

    @Autowired
    private ObjectMapper objectMapper;

    private DlxProcessingErrorHandler dlxProcessingErrorHandler;

    public ConsumerMessage(@Qualifier("rabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.dlxProcessingErrorHandler = new DlxProcessingErrorHandler(rabbitTemplate, DLX_EXCHANGE_NAME);
    }

    @RabbitListener(queues = WORK_QUEUE_NAME)
    public void onNormalMessageReceive(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            Employee employee = objectMapper.readValue(message.getBody(), Employee.class);
            if(isIdOdd(employee)) {
                logger.warn("Error processing message {}", employee.toString());
                dlxProcessingErrorHandler.handleErrorProcessingMessage(message, channel, tag);
            }
            else {
                processMessage(employee, "Queue");
                channel.basicAck(tag, false);
            }
        } catch (JsonProcessingException e) {
            logger.error("Error processing message: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error processing message: {}", e.getMessage());
        }
        catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage());
        }
    }

    private boolean isIdOdd(Employee employee) {
        return (Integer.parseInt(employee.getEmpId()) % 2) != 0 ? true : false;
    }

    private void processMessage(Employee employee, String messageType) {
        logger.info("Received {} message: {}", messageType, employee.toString());
    }
}
