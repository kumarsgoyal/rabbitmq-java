package com.kumarsgoyal.rabbitmq.ampq;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${ampq.exchange.name}")
    private String exchangeName;

    @Value("${ampq.queue.name.slow}")
    private String slowQueueName;

    @Value("${ampq.queue.name.fast}")
    private String fastQueueName;

    @Value("${ampq.key.name.slow}")
    private String keySlowName;

    @Value("${ampq.key.name.fast}")
    private String keyFastName;

    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.port}")
    private Integer rabbitmqPort;

    @Bean
    public RabbitMqCredentials rabbitMqCredentials() {
        return new RabbitMqCredentials();
    }

    @Bean(name= "slowQueue")
    public Queue slowQueue() {
        Queue queue = new Queue(slowQueueName, true, false, false);
        return queue;
    }

    @Bean(name= "fastQueue")
    public Queue fastQueue() {
        Queue queue = new Queue(fastQueueName, true, false, false);
        return queue;
    }
    @Bean(name = "exchange")
    public DirectExchange exchange() {
        DirectExchange directExchange = new DirectExchange(exchangeName, true, false);
        return directExchange;
    }

    @Bean(name = "slowQueueBinding")
    public Binding slowQueueBinding(@Qualifier("slowQueue") Queue queue, @Qualifier("exchange") DirectExchange exchange) {
        Binding binding = BindingBuilder.bind(queue).to(exchange()).with(keySlowName);
        return binding;
    }

    @Bean(name = "fastQueueBinding")
    public Binding fastQueueBinding(@Qualifier("fastQueue") Queue queue, @Qualifier("exchange") DirectExchange exchange) {
        Binding binding = BindingBuilder.bind(queue).to(exchange()).with(keyFastName);
        return binding;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }

    @Bean
    public Jackson2JsonMessageConverter converter(@Autowired ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // optional else spring will create it for you
    @Bean(name = "connectionFactory")
    ConnectionFactory connectionFactory() {
        return createConnectionFactory(rabbitmqHost, rabbitmqPort, rabbitMqCredentials());
    }

    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory, @Qualifier("converter") Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean(name = "prefetchOneContainerFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> prefetchOneContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(simpleRabbitListenerContainerFactory, connectionFactory);
        simpleRabbitListenerContainerFactory.setPrefetchCount(1);
        return simpleRabbitListenerContainerFactory;
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
