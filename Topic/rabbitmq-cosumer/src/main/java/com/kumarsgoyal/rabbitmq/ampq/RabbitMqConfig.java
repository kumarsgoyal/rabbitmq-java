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

    @Value("${ampq.picture.image.name}")
    private String pictureImageQueueName;

    @Value("${ampq.picture.vector.name}")
    private String pictureVectorQueueName;

    @Value("${ampq.picture.mobile.name}")
    private String pictureMobileQueueName;

    @Value("${ampq.picture.web.name}")
    private String pictureWebQueueName;

    @Value("${ampq.picture.large.name}")
    private String pictureLargeQueueName;

    @Value("${ampq.exchange.name}")
    private String exchangeName;

    @Value("${ampq.picture.image.routing.key1}")
    private String pictureImageRoutingKey1;

    @Value("${ampq.picture.image.routing.key2}")
    private String pictureImageRoutingKey2;

    @Value("${ampq.picture.vector.routing.key}")
    private String pictureVectorRoutingKey;

    @Value("${ampq.picture.mobile.routing.key}")
    private String pictureMobileRoutingKey;

    @Value("${ampq.picture.web.routing.key}")
    private String pictureWebRoutingKey;

    @Value("${ampq.picture.large.routing.key}")
    private String pictureLargeRoutingKey;

    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.port}")
    private Integer rabbitmqPort;

    @Bean
    public RabbitMqCredentials rabbitMqCredentials() {
        return new RabbitMqCredentials();
    }

    @Bean(name= "consumerPictureImageQueueNameQueue")
    public Queue consumerPictureImageQueueNameQueue() {
        Queue queue = new Queue(pictureImageQueueName, true, false, false);
        return queue;
    }

    @Bean(name= "consumerPictureVectorQueueNameQueue")
    public Queue consumerPictureVectorQueueNameQueue() {
        Queue queue = new Queue(pictureVectorQueueName, true, false, false);
        return queue;
    }

    @Bean(name = "consumerPictureMobileQueueNameQueue")
    public Queue consumerPictureMobileQueueNameQueue() {
        Queue queue = new Queue(pictureMobileQueueName, true, false, false);
        return queue;
    }

    @Bean(name = "consumerPictureWebQueueNameQueue")
    public Queue consumerPictureWebQueueNameQueue() {
        Queue queue = new Queue(pictureWebQueueName, true, false, false);
        return queue;
    }

    @Bean(name = "consumerPictureLargeQueueNameQueue")
    public Queue consumerPictureLargeQueueNameQueue() {
        Queue queue = new Queue(pictureLargeQueueName, true, false, false);
        return queue;
    }


    @Bean(name = "consumerExchange")
    public TopicExchange consumerExchange() {
        TopicExchange topicExchange = new TopicExchange(exchangeName, true, false);
        return topicExchange;
    }

    @Bean(name = "consumerPictureImageQueueNameQueueBinding1")
    public Binding consumerPictureImageQueueNameQueueBinding1(@Qualifier("consumerPictureImageQueueNameQueue") Queue consumerPictureImageQueueNameQueue, @Qualifier("consumerExchange") TopicExchange consumerExchange) {
        Binding binding = BindingBuilder.bind(consumerPictureImageQueueNameQueue).to(consumerExchange()).with(pictureImageRoutingKey1);
        return binding;
    }

    @Bean(name = "consumerPictureImageQueueNameQueueBinding2")
    public Binding consumerPictureImageQueueNameQueueBinding2(@Qualifier("consumerPictureImageQueueNameQueue") Queue consumerPictureImageQueueNameQueue, @Qualifier("consumerExchange") TopicExchange consumerExchange) {
        Binding binding = BindingBuilder.bind(consumerPictureImageQueueNameQueue).to(consumerExchange()).with(pictureImageRoutingKey2);
        return binding;
    }

    @Bean(name = "consumerPictureVectorQueueNameQueueBinding")
    public Binding consumerPictureVectorQueueNameQueueBinding(@Qualifier("consumerPictureVectorQueueNameQueue") Queue consumerPictureVectorQueueNameQueue, @Qualifier("consumerExchange") TopicExchange consumerExchange) {
        Binding binding = BindingBuilder.bind(consumerPictureVectorQueueNameQueue).to(consumerExchange()).with(pictureVectorRoutingKey);
        return binding;
    }

    @Bean(name = "consumerPictureMobileQueueNameQueueBinding")
    public Binding consumerPictureMobileQueueNameQueueBinding(@Qualifier("consumerPictureMobileQueueNameQueue") Queue consumerPictureMobileQueueNameQueue, @Qualifier("consumerExchange") TopicExchange consumerExchange) {
        Binding binding = BindingBuilder.bind(consumerPictureMobileQueueNameQueue).to(consumerExchange()).with(pictureMobileRoutingKey);
        return binding;
    }

    @Bean(name = "consumerPictureWebQueueNameQueueBinding")
    public Binding consumerPictureWebQueueNameQueueBinding(@Qualifier("consumerPictureWebQueueNameQueue") Queue consumerPictureWebQueueNameQueue, @Qualifier("consumerExchange") TopicExchange consumerExchange) {
        Binding binding = BindingBuilder.bind(consumerPictureWebQueueNameQueue).to(consumerExchange()).with(pictureWebRoutingKey);
        return binding;
    }

    @Bean(name = "consumerPictureLargeQueueNameQueueBinding")
    public Binding consumerPictureLargeQueueNameQueueBinding(@Qualifier("consumerPictureLargeQueueNameQueue") Queue consumerPictureLargeQueueNameQueue, @Qualifier("consumerExchange") TopicExchange consumerExchange) {
        Binding binding = BindingBuilder.bind(consumerPictureLargeQueueNameQueue).to(consumerExchange()).with(pictureWebRoutingKey);
        return binding;
    }

    // optional else spring will create it for you
    @Bean(name = "consumerConnectionFactory")
    ConnectionFactory createconsumerConnectionFactory() {
        return createConnectionFactory(rabbitmqHost, rabbitmqPort, rabbitMqCredentials());
    }

    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("consumerConnectionFactory") ConnectionFactory connectionFactory) {
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
