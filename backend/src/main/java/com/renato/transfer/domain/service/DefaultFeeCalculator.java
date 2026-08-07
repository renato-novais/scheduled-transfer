package com.renato.transfer.domain.service;

import com.renato.transfer.exception.FeeNotApplicableException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DefaultFeeCalculator implements FeeCalculator {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    public BigDecimal calculate(BigDecimal transferAmount, LocalDate schedulingDate, LocalDate transferDate) {
        long daysUntilTransfer = ChronoUnit.DAYS.between(schedulingDate, transferDate);

        if (daysUntilTransfer < 0 || daysUntilTransfer > 50) {
            throw new FeeNotApplicableException(daysUntilTransfer);
        }
        if (daysUntilTransfer == 0) {
            return fee(transferAmount, "3.00", "0.025");
        }
        if (daysUntilTransfer <= 10) {
            return fee(transferAmount, "12.00", "0.00");
        }
        if (daysUntilTransfer <= 20) {
            return fee(transferAmount, "0.00", "0.082");
        }
        if (daysUntilTransfer <= 30) {
            return fee(transferAmount, "0.00", "0.069");
        }
        if (daysUntilTransfer <= 40) {
            return fee(transferAmount, "0.00", "0.047");
        }
        return fee(transferAmount, "0.00", "0.017");
    }

    private BigDecimal fee(BigDecimal amount, String fixedFee, String percentage) {
        return new BigDecimal(fixedFee)
            .add(amount.multiply(new BigDecimal(percentage)))
            .setScale(2, ROUNDING_MODE);
    }
}
