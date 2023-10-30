package com.kumarsgoyal.rabbitmq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumarsgoyal.rabbitmq.dto.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

@Component
public class ConsumerMessage {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMessage.class);

    private static final String QUEUE_NAME_SLOW = "ampq.queue.slow.name";

    private static final String QUEUE_NAME_FAST = "ampq.queue.fast.name";


    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = QUEUE_NAME_SLOW, concurrency = "2")
    public void onSlowMessageReceive(Employee employee) throws InterruptedException{
        logger.info("Received message slow: {}", employee);
        TimeUnit.MINUTES.sleep(1);
    }

    @RabbitListener(queues = QUEUE_NAME_FAST, concurrency = "2", containerFactory = "prefetchOneContainerFactory")
    public void onFastMessageReceive(Employee employee)  throws InterruptedException{
        logger.info("Received message fast: {}", employee);
        TimeUnit.MILLISECONDS.sleep(100);
    }
}
