package com.renato.transfer.exception;

public class FeeNotApplicableException extends RuntimeException {

    public FeeNotApplicableException(long daysUntilTransfer) {
        super("No fee is applicable for a transfer scheduled " + daysUntilTransfer
            + " day(s) ahead. Allowed range is 0 to 50 days.");
    }
}
