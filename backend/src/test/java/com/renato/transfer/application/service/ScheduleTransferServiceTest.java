package com.renato.transfer.application.service;

import com.renato.transfer.application.dto.CreateTransferRequest;
import com.renato.transfer.application.dto.TransferResponse;
import com.renato.transfer.domain.model.TransferSchedule;
import com.renato.transfer.domain.model.TransferStatus;
import com.renato.transfer.domain.service.FeeCalculator;
import com.renato.transfer.exception.FeeNotApplicableException;
import com.renato.transfer.infrastructure.messaging.TransferEventPublisher;
import com.renato.transfer.infrastructure.persistence.TransferScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleTransferServiceTest {

    @Mock
    private FeeCalculator feeCalculator;

    @Mock
    private TransferScheduleRepository repository;

    @Mock
    private TransferEventPublisher eventPublisher;

    @Test
    void shouldScheduleTransferAndReturnResponseWithFeeAndTotal() {
        ScheduleTransferService service = new ScheduleTransferService(feeCalculator, repository, eventPublisher);
        CreateTransferRequest request = new CreateTransferRequest();
        request.setSourceAccount("1234567890");
        request.setDestinationAccount("0987654321");
        request.setAmount(new BigDecimal("1000.00"));
        request.setTransferDate(LocalDate.now().plusDays(15));

        when(feeCalculator.calculate(any(), any(), any())).thenReturn(new BigDecimal("82.00"));
        when(repository.save(any(TransferSchedule.class))).thenAnswer(invocation -> {
            TransferSchedule entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        TransferResponse response = service.schedule(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSourceAccount()).isEqualTo("1234567890");
        assertThat(response.getDestinationAccount()).isEqualTo("0987654321");
        assertThat(response.getAmount()).isEqualByComparingTo("1000.00");
        assertThat(response.getFee()).isEqualByComparingTo("82.00");
        assertThat(response.getTotalAmount()).isEqualByComparingTo("1082.00");
        assertThat(response.getSchedulingDate()).isEqualTo(LocalDate.now());
        assertThat(response.getStatus()).isEqualTo(TransferStatus.SCHEDULED.name());

        ArgumentCaptor<TransferSchedule> captor = ArgumentCaptor.forClass(TransferSchedule.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getFeeAmount()).isEqualByComparingTo("82.00");
        verify(eventPublisher).publishTransferScheduled(captor.getValue());
    }

    @Test
    void shouldNotPersistOrPublishWhenFeeIsNotApplicable() {
        ScheduleTransferService service = new ScheduleTransferService(feeCalculator, repository, eventPublisher);
        CreateTransferRequest request = new CreateTransferRequest();
        request.setSourceAccount("1234567890");
        request.setDestinationAccount("0987654321");
        request.setAmount(new BigDecimal("1000.00"));
        request.setTransferDate(LocalDate.now().plusDays(60));

        when(feeCalculator.calculate(any(), any(), any())).thenThrow(new FeeNotApplicableException(60));

        assertThatThrownBy(() -> service.schedule(request))
            .isInstanceOf(FeeNotApplicableException.class);

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishTransferScheduled(any());
    }
}
