package com.renato.transfer.infrastructure.messaging;

import com.renato.transfer.domain.model.TransferSchedule;
import com.renato.transfer.domain.model.TransferStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private TransferSchedule transferSchedule() {
        return TransferSchedule.builder()
            .id(1L)
            .sourceAccount("1234567890")
            .destinationAccount("0987654321")
            .transferAmount(new BigDecimal("1000.00"))
            .feeAmount(new BigDecimal("82.00"))
            .transferDate(LocalDate.now().plusDays(15))
            .schedulingDate(LocalDate.now())
            .status(TransferStatus.SCHEDULED)
            .build();
    }

    @Test
    void shouldPublishEventWithExchangeRoutingKeyAndPayload() {
        TransferEventPublisher publisher = new TransferEventPublisher(rabbitTemplate);

        publisher.publishTransferScheduled(transferSchedule());

        ArgumentCaptor<TransferScheduledEvent> captor = ArgumentCaptor.forClass(TransferScheduledEvent.class);
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMqConfiguration.EXCHANGE),
            eq(RabbitMqConfiguration.ROUTING_KEY),
            captor.capture()
        );

        TransferScheduledEvent event = captor.getValue();
        assertThat(event.getTransferId()).isEqualTo(1L);
        assertThat(event.getSourceAccount()).isEqualTo("1234567890");
        assertThat(event.getFee()).isEqualByComparingTo("82.00");
        assertThat(event.getEventType()).isEqualTo("TRANSFER_SCHEDULED");
        assertThat(event.getEventId()).isNotBlank();
    }

    @Test
    void shouldNotPropagateExceptionWhenRabbitMqPublicationFails() {
        TransferEventPublisher publisher = new TransferEventPublisher(rabbitTemplate);
        doThrow(new AmqpException("broker unavailable"))
            .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        assertThatNoException().isThrownBy(() -> publisher.publishTransferScheduled(transferSchedule()));
    }
}
