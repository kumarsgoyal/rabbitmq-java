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

    @Value("${ampq.queue.name}")
    private String queueName;

    @Value("${ampq.queue.dlx.name}")
    private String queueDlxName;

    @Value("${ampq.exchange.name}")
    private String exchangeName;

    @Value("${ampq.dlx.exchange.name}")
    private String dlxExchangeName;

    @Value("${ampq.queue.dlx.routing.key}")
    private String dlxExchangeRoutingKey;

    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.port}")
    private Integer rabbitmqPort;

    @Bean
    public RabbitMqCredentials rabbitMqCredentials() {
        return new RabbitMqCredentials();
    }

    @Bean(name= "queueName")
    public Queue queueName() {
        Map<String, Object> argsMap = new HashMap<> ();
        argsMap.put("x-dead-letter-exchange", dlxExchangeName);
        argsMap.put("x-dead-letter-routing-key", dlxExchangeRoutingKey);
        argsMap.put("x-message-ttl", 5000);
        Queue queue = new Queue(queueName, true, false, false, argsMap);
        return queue;
    }

    @Bean(name= "queueDlxName")
    public Queue queueDlxName() {
        Queue queue = new Queue(queueDlxName, true, false, false);
        return queue;
    }

    @Bean(name = "exchange")
    public FanoutExchange exchange() {
        FanoutExchange fanoutExchange = new FanoutExchange(exchangeName, true, false);
        return fanoutExchange;
    }

    // Define the DLX exchange as a DirectExchange
    @Bean(name = "dlxExchange")
    public DirectExchange dlxExchange() {
        DirectExchange directExchange = new DirectExchange(dlxExchangeName, true, false);
        return directExchange;
    }

    @Bean(name = "queueBinding")
    public Binding queueBinding(@Qualifier("queueName") Queue queueName, @Qualifier("exchange") FanoutExchange exchange) {
        Binding binding = BindingBuilder.bind(queueName).to(exchange);
        return binding;
    }

    @Bean(name = "queueDlxBinding")
    public Binding queueDlxBinding(@Qualifier("queueDlxName") Queue queueDlxName, @Qualifier("dlxExchange") DirectExchange dlxExchange) {
        Binding binding = BindingBuilder.bind(queueDlxName).to(dlxExchange).with(dlxExchangeRoutingKey);
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
        return connectionFactory;
    }
}
