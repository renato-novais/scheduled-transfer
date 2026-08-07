package com.renato.transfer.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferEventConsumer {

    @RabbitListener(queues = RabbitMqConfiguration.QUEUE)
    public void onTransferScheduled(TransferScheduledEvent event) {
        log.info("Transfer event consumed eventId={} transferId={}", event.getEventId(), event.getTransferId());
        log.info("Event processing completed eventId={}", event.getEventId());
    }
}
