package com.kumarsgoyal.rabbitmq.scheduler;


import com.kumarsgoyal.rabbitmq.consumer.ConsumerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class RabbitMqScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqScheduler.class);

    @Autowired
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    //<second>  <minute>  <hour>  <day-of-month>  <month>  <day-of-week>
    @Scheduled(cron = "0 28 0 * * *")
    public void stopAll() {
        rabbitListenerEndpointRegistry.getListenerContainers().forEach(s -> {
            logger.info("Stopping listener container {}", s);
            s.stop();
        });
    }

    @Scheduled(cron = "0 30 0 * * *")
    public void startAll() {
        rabbitListenerEndpointRegistry.getListenerContainers().forEach(s -> {
            logger.info("Starting listener container {}", s);
            s.start();
        });
    }

}
