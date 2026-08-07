package com.renato.transfer.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    public static final String EXCHANGE = "transfer.events";
    public static final String ROUTING_KEY = "transfer.scheduled";
    public static final String QUEUE = "transfer.scheduled.audit";
    public static final String DLQ = "transfer.scheduled.audit.dlq";
    private static final String DLQ_ROUTING_KEY = "transfer.scheduled.dlq";

    @Bean
    public TopicExchange transferEventsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue transferScheduledAuditQueue() {
        return QueueBuilder.durable(QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
            .build();
    }

    @Bean
    public Queue transferScheduledAuditDlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding transferScheduledBinding() {
        return BindingBuilder.bind(transferScheduledAuditQueue())
            .to(transferEventsExchange())
            .with(ROUTING_KEY);
    }

    @Bean
    public Binding transferScheduledDlqBinding() {
        return BindingBuilder.bind(transferScheduledAuditDlq())
            .to(transferEventsExchange())
            .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
