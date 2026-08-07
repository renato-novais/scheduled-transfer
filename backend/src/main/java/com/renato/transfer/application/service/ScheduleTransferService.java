package com.renato.transfer.application.service;

import com.renato.transfer.application.dto.CreateTransferRequest;
import com.renato.transfer.application.dto.TransferResponse;
import com.renato.transfer.domain.model.TransferSchedule;
import com.renato.transfer.domain.model.TransferStatus;
import com.renato.transfer.domain.service.FeeCalculator;
import com.renato.transfer.infrastructure.messaging.TransferEventPublisher;
import com.renato.transfer.infrastructure.persistence.TransferScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ScheduleTransferService {

    private final FeeCalculator feeCalculator;
    private final TransferScheduleRepository repository;
    private final TransferEventPublisher eventPublisher;

    public ScheduleTransferService(FeeCalculator feeCalculator, TransferScheduleRepository repository,
                                    TransferEventPublisher eventPublisher) {
        this.feeCalculator = feeCalculator;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TransferResponse schedule(CreateTransferRequest request) {
        LocalDate schedulingDate = LocalDate.now();
        BigDecimal fee = feeCalculator.calculate(request.getAmount(), schedulingDate, request.getTransferDate());

        TransferSchedule entity = TransferSchedule.builder()
            .sourceAccount(request.getSourceAccount())
            .destinationAccount(request.getDestinationAccount())
            .transferAmount(request.getAmount())
            .feeAmount(fee)
            .transferDate(request.getTransferDate())
            .schedulingDate(schedulingDate)
            .status(TransferStatus.SCHEDULED)
            .build();

        TransferSchedule saved = repository.save(entity);
        eventPublisher.publishTransferScheduled(saved);
        return TransferResponse.from(saved);
    }
}
