package com.kumarsgoyal.rabbitmq.ampq;



import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${ampq.details.hr.name}")
    private String orgDetailsHr;

    @Value("${ampq.details.accounts.name}")
    private String orgDetailAccounts;

    @Value("${ampq.details.it.name}")
    private String orgDetailsIt;

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

    @Bean(name= "orgDetailsHrQueue")
    public Queue orgDetailsHrQueue() {
        Queue queue = new Queue(orgDetailsHr, true, false, false);
        return queue;
    }

    @Bean(name= "orgDetailAccountsQueue")
    public Queue orgDetailAccounts() {
        Queue queue = new Queue(orgDetailAccounts, true, false, false);
        return queue;
    }

    @Bean(name = "orgDetailsItQueue")
    public Queue orgDetailsIt() {
        Queue queue = new Queue(orgDetailsIt, true, false, false);
        return queue;
    }

    @Bean(name = "orgExchange")
    public FanoutExchange orgExchange() {
        FanoutExchange fanoutExchange = new FanoutExchange(exchangeName, true, false);
        return fanoutExchange;
    }

    @Bean(name = "orgDetailsHrQueueBinding")
    public Binding orgDetailsHrQueueBinding(@Qualifier("orgDetailsHrQueue") Queue orgDetailsHrQueue, @Qualifier("orgExchange") FanoutExchange orgExchange) {
        Binding binding = BindingBuilder.bind(orgDetailsHrQueue).to(orgExchange());
        return binding;
    }

    @Bean(name = "orgDetailAccountsQueueBinding")
    public Binding orgDetailAccountsQueueBinding(@Qualifier("orgDetailAccountsQueue") Queue orgDetailAccountsQueue, @Qualifier("orgExchange") FanoutExchange orgExchange) {
        Binding binding = BindingBuilder.bind(orgDetailAccountsQueue).to(orgExchange());
        return binding;
    }

    @Bean(name = "orgDetailsItQueueBinding")
    public Binding orgDetailsItQueueBinding(@Qualifier("orgDetailsItQueue") Queue orgDetailsItQueue, @Qualifier("orgExchange") FanoutExchange orgExchange) {
        Binding binding = BindingBuilder.bind(orgDetailsItQueue).to(orgExchange());
        return binding;
    }

    // optional else spring will create it for you
    @Bean(name = "orgConnectionFactory")
    ConnectionFactory orgConnectionFactory() {
        return createConnectionFactory(rabbitmqHost, rabbitmqPort, rabbitMqCredentials());
    }

    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("orgConnectionFactory") ConnectionFactory connectionFactory) {
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
