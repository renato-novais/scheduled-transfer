package com.renato.transfer.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FeeCalculator {

    BigDecimal calculate(BigDecimal transferAmount, LocalDate schedulingDate, LocalDate transferDate);
}
