package com.renato.transfer.infrastructure.messaging;

import com.renato.transfer.domain.model.TransferSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class TransferEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TransferEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTransferScheduled(TransferSchedule transfer) {
        TransferScheduledEvent event = TransferScheduledEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("TRANSFER_SCHEDULED")
            .occurredAt(Instant.now())
            .transferId(transfer.getId())
            .sourceAccount(transfer.getSourceAccount())
            .destinationAccount(transfer.getDestinationAccount())
            .amount(transfer.getTransferAmount())
            .fee(transfer.getFeeAmount())
            .transferDate(transfer.getTransferDate())
            .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMqConfiguration.EXCHANGE, RabbitMqConfiguration.ROUTING_KEY, event);
            log.info("Transfer event published eventId={} transferId={}", event.getEventId(), event.getTransferId());
        } catch (AmqpException ex) {
            log.error("RabbitMQ publication failed transferId={}", transfer.getId(), ex);
        }
    }
}
