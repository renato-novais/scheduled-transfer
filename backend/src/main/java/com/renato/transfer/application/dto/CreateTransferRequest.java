package com.renato.transfer.application.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateTransferRequest {

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "must contain exactly 10 digits")
    private String sourceAccount;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "must contain exactly 10 digits")
    private String destinationAccount;

    @NotNull
    @DecimalMin(value = "0.01", message = "must be greater than zero")
    private BigDecimal amount;

    @NotNull
    @FutureOrPresent(message = "must be today or a future date")
    private LocalDate transferDate;
}
