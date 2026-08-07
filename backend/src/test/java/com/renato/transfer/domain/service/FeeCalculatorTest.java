package com.renato.transfer.domain.service;

import com.renato.transfer.exception.FeeNotApplicableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeeCalculatorTest {

    private final FeeCalculator feeCalculator = new DefaultFeeCalculator();
    private final LocalDate schedulingDate = LocalDate.of(2026, 8, 6);

    @ParameterizedTest(name = "day {0} -> fee {2}")
    @DisplayName("Should calculate fee for each band of the fee table")
    @CsvSource({
        "0,  1000.00, 28.00",
        "1,  1000.00, 12.00",
        "10, 1000.00, 12.00",
        "11, 1000.00, 82.00",
        "20, 1000.00, 82.00",
        "21, 1000.00, 69.00",
        "30, 1000.00, 69.00",
        "31, 1000.00, 47.00",
        "40, 1000.00, 47.00",
        "41, 1000.00, 17.00",
        "50, 1000.00, 17.00"
    })
    void shouldCalculateFeeForEachBand(long daysUntilTransfer, BigDecimal amount, BigDecimal expectedFee) {
        LocalDate transferDate = schedulingDate.plusDays(daysUntilTransfer);

        BigDecimal fee = feeCalculator.calculate(amount, schedulingDate, transferDate);

        assertThat(fee).isEqualByComparingTo(expectedFee);
    }

    @Test
    @DisplayName("Should throw FeeNotApplicableException when transfer date is before scheduling date")
    void shouldRejectTransferDateBeforeSchedulingDate() {
        LocalDate transferDate = schedulingDate.minusDays(1);

        assertThatThrownBy(() -> feeCalculator.calculate(BigDecimal.valueOf(1000), schedulingDate, transferDate))
            .isInstanceOf(FeeNotApplicableException.class);
    }

    @Test
    @DisplayName("Should throw FeeNotApplicableException when transfer date is more than 50 days ahead")
    void shouldRejectTransferDateBeyondFiftyDays() {
        LocalDate transferDate = schedulingDate.plusDays(51);

        assertThatThrownBy(() -> feeCalculator.calculate(BigDecimal.valueOf(1000), schedulingDate, transferDate))
            .isInstanceOf(FeeNotApplicableException.class);
    }

    @Test
    @DisplayName("Should round the fee to two decimal places using HALF_UP")
    void shouldRoundFeeToTwoDecimalPlaces() {
        BigDecimal fee = feeCalculator.calculate(BigDecimal.valueOf(333.33), schedulingDate, schedulingDate.plusDays(15));

        assertThat(fee).isEqualByComparingTo(new BigDecimal("27.33"));
    }
}
