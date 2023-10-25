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

    @Value("${ampq.queue.work.name}")
    private String workQueueName;

    @Value("${ampq.queue.wait.name}")
    private String waitQueueName;

    @Value("${ampq.queue.dead.name}")
    private String deadQueueName;

    @Value("${ampq.queue.work.exchange.name}")
    private String workExchangeName;

    @Value("${ampq.queue.wait.exchange.name}")
    private String waitExchangeName;

    @Value("${ampq.queue.dead.exchange.name}")
    private String deadExchangeName;

    @Value("${ampq.routing.work.key}")
    private String workRoutingKey;

    @Value("${ampq.routing.wait.key}")
    private String waitRoutingKey;

    @Value("${ampq.routing.dead.key}")
    private String deadRoutingKey;

    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.port}")
    private Integer rabbitmqPort;

    @Bean
    public RabbitMqCredentials rabbitMqCredentials() {
        return new RabbitMqCredentials();
    }

    @Bean(name= "workQueueName")
    public Queue workQueueName() {
        Map<String, Object> argsMap = new HashMap<> ();
        argsMap.put("x-dead-letter-exchange", waitExchangeName);
        argsMap.put("x-dead-letter-routing-key", waitRoutingKey);
        Queue queue = new Queue(workQueueName, true, false, false, argsMap);
        return queue;
    }

    @Bean(name= "waitQueueName")
    public Queue waitQueueName() {
        Map<String, Object> argsMap = new HashMap<> ();
        argsMap.put("x-dead-letter-exchange", workExchangeName);
        argsMap.put("x-dead-letter-routing-key", workRoutingKey);
        argsMap.put("x-message-ttl", 5000);
        Queue queue = new Queue(waitQueueName, true, false, false, argsMap);
        return queue;
    }

    @Bean(name= "deadQueueName")
    public Queue deadQueueName() {
        Map<String, Object> argsMap = new HashMap<> ();
        argsMap.put("x-message-ttl", 50000000);
        Queue queue = new Queue(deadQueueName, true, false, false, argsMap);
        return queue;
    }

    @Bean(name = "workExchangeName")
    public DirectExchange workExchangeName() {
        DirectExchange directExchange = new DirectExchange(workExchangeName, true, false);
        return directExchange;
    }

    @Bean(name = "waitExchangeName")
    public DirectExchange waitExchangeName() {
        DirectExchange directExchange = new DirectExchange(waitExchangeName, true, false);
        return directExchange;
    }

    @Bean(name = "deadExchangeName")
    public DirectExchange deadExchangeName() {
        DirectExchange directExchange = new DirectExchange(deadExchangeName, true, false);
        return directExchange;
    }

    @Bean(name = "workQueueBinding")
    public Binding workQueueBinding(@Qualifier("workQueueName") Queue workQueueName, @Qualifier("workExchangeName") DirectExchange exchange) {
        Binding binding = BindingBuilder.bind(workQueueName).to(exchange).with(workRoutingKey);
        return binding;
    }

    @Bean(name = "waitQueueBinding")
    public Binding waitQueueBinding(@Qualifier("waitQueueName") Queue waitQueueName, @Qualifier("waitExchangeName") DirectExchange exchange) {
        Binding binding = BindingBuilder.bind(waitQueueName).to(exchange).with(waitRoutingKey);
        return binding;
    }

    @Bean(name = "deadQueueBinding")
    public Binding deadQueueBinding(@Qualifier("deadQueueName") Queue deadQueueName, @Qualifier("deadExchangeName") DirectExchange exchange) {
        Binding binding = BindingBuilder.bind(deadQueueName).to(exchange).with(deadRoutingKey);
        return binding;
    }

    // optional else spring will create it for you
    @Bean(name = "connectionFactory")
    ConnectionFactory connectionFactory() {
        return createConnectionFactory(rabbitmqHost, rabbitmqPort, rabbitMqCredentials());
    }

    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
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
        connectionFactory.setConnectionTimeout(10000);
        connectionFactory.setRequestedHeartBeat(60);
        connectionFactory.setChannelCacheSize(6);
        return connectionFactory;
    }
}
