package com.renato.transfer.infrastructure.observability;

public final class AccountMasker {

    private static final int VISIBLE_DIGITS = 4;
    private static final String PLACEHOLDER = "****";

    private AccountMasker() {
    }

    public static String mask(String account) {
        if (account == null || account.length() <= VISIBLE_DIGITS) {
            return PLACEHOLDER;
        }
        String maskedPrefix = "*".repeat(account.length() - VISIBLE_DIGITS);
        String visibleSuffix = account.substring(account.length() - VISIBLE_DIGITS);
        return maskedPrefix + visibleSuffix;
    }
}
