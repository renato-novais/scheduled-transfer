package com.renato.transfer.application.service;

import com.renato.transfer.application.dto.TransferResponse;
import com.renato.transfer.domain.model.TransferSchedule;
import com.renato.transfer.domain.model.TransferStatus;
import com.renato.transfer.infrastructure.persistence.TransferScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTransfersServiceTest {

    @Mock
    private TransferScheduleRepository repository;

    @Test
    void shouldReturnAllTransfersOrderedBySchedulingDateAndIdDescending() {
        ListTransfersService service = new ListTransfersService(repository);
        TransferSchedule schedule = TransferSchedule.builder()
            .id(1L)
            .sourceAccount("1234567890")
            .destinationAccount("0987654321")
            .transferAmount(new BigDecimal("1000.00"))
            .feeAmount(new BigDecimal("82.00"))
            .transferDate(LocalDate.now().plusDays(15))
            .schedulingDate(LocalDate.now())
            .status(TransferStatus.SCHEDULED)
            .build();
        when(repository.findAllByOrderBySchedulingDateDescIdDesc()).thenReturn(List.of(schedule));

        List<TransferResponse> responses = service.listAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getTotalAmount()).isEqualByComparingTo("1082.00");
    }
}
