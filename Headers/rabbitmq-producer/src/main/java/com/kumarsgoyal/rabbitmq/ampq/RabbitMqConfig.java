package com.kumarsgoyal.rabbitmq.ampq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Value("${ampq.details.high.priority.name}")
    private String highPriorityQueue;

    @Value("${ampq.details.low.priority.name}")
    private String lowPriorityQueue;

    @Value("${ampq.details.low.high.priority.name}")
    private String lowHighPriorityQueue;

    @Value("${ampq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.port}")
    private Integer rabbitmqPort;

    @Bean
    public RabbitMqCredentials rabbitMqCredentials() {
        return new RabbitMqCredentials();
    }

    @Bean(name= "highPriorityQueue")
    public Queue highPriorityQueue() {
        Queue queue = new Queue(highPriorityQueue, true, false, false);
        return queue;
    }

    @Bean(name= "lowPriorityQueue")
    public Queue lowPriorityQueue() {
        Queue queue = new Queue(lowPriorityQueue, true, false, false);
        return queue;
    }

    @Bean(name = "lowHighPriorityQueue")
    public Queue lowHighPriorityQueue() {
        Queue queue = new Queue(lowHighPriorityQueue, true, false, false);
        return queue;
    }

    @Bean(name = "priorityExchange")
    public HeadersExchange priorityExchange() {
        HeadersExchange headersExchange = new HeadersExchange(exchangeName, true, false);
        return headersExchange;
    }

    @Bean(name = "highPriorityQueueBinding")
    public Binding highPriorityQueueBinding(@Qualifier("highPriorityQueue") Queue highPriorityQueue, @Qualifier("priorityExchange") HeadersExchange headersExchange) {
        Map<String, Object> headerValues = new HashMap<>();
        headerValues.put("priority", "high");
        return BindingBuilder.bind(highPriorityQueue).to(headersExchange).whereAny(headerValues).match();
    }

    @Bean(name = "lowPriorityQueueBinding")
    public Binding bindingLowPriority(@Qualifier("lowPriorityQueue")  Queue lowPriorityQueue, @Qualifier("priorityExchange") HeadersExchange headersExchange) {
        Map<String, Object> headerValues = new HashMap<>();
        headerValues.put("priority", "low");
        return BindingBuilder.bind(lowPriorityQueue).to(headersExchange).whereAny(headerValues).match();
    }

    @Bean(name = "lowHighPriorityQueueBinding")
    public Binding lowHighPriorityQueueBinding(@Qualifier("lowHighPriorityQueue") Queue lowHighPriorityQueue, @Qualifier("priorityExchange") HeadersExchange headersExchange) {
        Map<String, Object> headerValues = new HashMap<>();
        headerValues.put("priority", "high");
        headerValues.put("priority", "low");
        return BindingBuilder.bind(lowHighPriorityQueue).to(headersExchange).whereAll(headerValues).match();
    }

    // optional else spring will create it for you
    @Bean(name = "priorityConnectionFactory")
    ConnectionFactory priorityConnectionFactory() {
        return createConnectionFactory(rabbitmqHost, rabbitmqPort, rabbitMqCredentials());
    }

    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("priorityConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    private ConnectionFactory createConnectionFactory(String rabbitmqHost, Integer rabbitmqPort, final RabbitMqCredentials rabbitMqCredentials) {
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitMqCredentials.getUser());
        connectionFactory.setPassword(rabbitMqCredentials.getPassword());
        connectionFactory.setVirtualHost(rabbitMqCredentials.getVhost());
        return connectionFactory;
    }
}
