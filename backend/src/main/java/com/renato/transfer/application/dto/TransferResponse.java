package com.renato.transfer.application.dto;

import com.renato.transfer.domain.model.TransferSchedule;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class TransferResponse {

    private Long id;
    private String sourceAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalAmount;
    private LocalDate transferDate;
    private LocalDate schedulingDate;
    private String status;

    public static TransferResponse from(TransferSchedule entity) {
        return TransferResponse.builder()
            .id(entity.getId())
            .sourceAccount(entity.getSourceAccount())
            .destinationAccount(entity.getDestinationAccount())
            .amount(entity.getTransferAmount())
            .fee(entity.getFeeAmount())
            .totalAmount(entity.getTransferAmount().add(entity.getFeeAmount()))
            .transferDate(entity.getTransferDate())
            .schedulingDate(entity.getSchedulingDate())
            .status(entity.getStatus().name())
            .build();
    }
}
